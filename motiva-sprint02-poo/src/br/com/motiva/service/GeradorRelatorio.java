package br.com.motiva.service;

import br.com.motiva.dao.EquipeManutencaoDAO;
import br.com.motiva.dao.IntervencaoOperacionalDAO;
import br.com.motiva.dao.RelatorioPrioridadeDAO;
import br.com.motiva.db.ConexaoBD;
import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.model.persistence.EquipeManutencaoRecord;
import br.com.motiva.model.persistence.IntervencaoOperacionalRecord;
import br.com.motiva.model.persistence.RelatorioPrioridadeRecord;
import br.com.motiva.model.persistence.TrechoRodoviaRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orquestra o motor de regras da Sprint 02 e a persistencia acrescentada na Sprint 03.
 */
public class GeradorRelatorio {
    private static final String STATUS_PLANEJADA = "PLANEJADA";
    private static final Map<TipoIntervencao, String> NOME_EQUIPE_POR_TIPO = Map.of(
            TipoIntervencao.ROCADA_MECANIZADA, "Equipe Alpha",
            TipoIntervencao.ROCADA_MANUAL, "Equipe Beta",
            TipoIntervencao.PULVERIZACAO, "Equipe Preventiva"
    );

    private final MotorRegrasPrioridade motorRegras;
    private final RelatorioPrioridade relatorioConsole;
    private final RelatorioPrioridadeDAO relatorioDAO;

    public GeradorRelatorio() {
        this(
                new MotorRegrasPrioridade(),
                new RelatorioPrioridade(),
                new RelatorioPrioridadeDAO()
        );
    }

    public GeradorRelatorio(
            MotorRegrasPrioridade motorRegras,
            RelatorioPrioridade relatorioConsole,
            RelatorioPrioridadeDAO relatorioDAO
    ) {
        if (motorRegras == null || relatorioConsole == null || relatorioDAO == null) {
            throw new IllegalArgumentException("As dependencias do gerador de relatorio sao obrigatorias.");
        }

        this.motorRegras = motorRegras;
        this.relatorioConsole = relatorioConsole;
        this.relatorioDAO = relatorioDAO;
    }

    /**
     * Executa e imprime o relatorio e persiste apenas seu historico. Esta assinatura
     * mantem o uso direto dos objetos de dominio da Sprint 02.
     */
    public ResultadoPrioridade[] gerarRelatorio(TrechoRodovia[] trechos) throws SQLException {
        ResultadoPrioridade[] resultados = processar(trechos);
        relatorioDAO.salvarRelatorio(criarRegistroRelatorio(resultados, LocalDateTime.now()));
        return resultados;
    }

    /**
     * Executa o relatorio a partir dos registros persistidos e grava, na mesma
     * transacao, o historico e as intervencoes planejadas.
     */
    public ResultadoPrioridade[] gerarRelatorio(List<TrechoRodoviaRecord> trechosPersistidos)
            throws SQLException {
        if (trechosPersistidos == null) {
            throw new IllegalArgumentException("A lista de trechos persistidos e obrigatoria.");
        }

        IdentityHashMap<TrechoRodovia, Long> idPorTrecho = new IdentityHashMap<>();
        List<TrechoRodovia> trechosDominio = new ArrayList<>(trechosPersistidos.size());

        for (TrechoRodoviaRecord registro : trechosPersistidos) {
            if (registro == null) {
                throw new IllegalArgumentException("A lista nao pode conter registro de trecho nulo.");
            }
            if (registro.id() == null || registro.id() <= 0) {
                throw new IllegalArgumentException(
                        "Todos os trechos devem possuir um ID persistido para gerar intervencoes."
                );
            }

            TrechoRodovia trecho = registro.toDomain();
            trechosDominio.add(trecho);
            idPorTrecho.put(trecho, registro.id());
        }

        ResultadoPrioridade[] resultados = processar(trechosDominio.toArray(new TrechoRodovia[0]));
        persistirRelatorioEIntervencoes(resultados, idPorTrecho);
        return resultados;
    }

    private ResultadoPrioridade[] processar(TrechoRodovia[] trechos) {
        validarTrechos(trechos);
        ResultadoPrioridade[] resultados = motorRegras.gerarRelatorioPrioridade(trechos);
        relatorioConsole.imprimir(resultados);
        return resultados;
    }

    private void validarTrechos(TrechoRodovia[] trechos) {
        if (trechos == null) {
            throw new IllegalArgumentException("Os trechos para analise sao obrigatorios.");
        }

        for (TrechoRodovia trecho : trechos) {
            if (trecho == null) {
                throw new IllegalArgumentException("A colecao nao pode conter trecho nulo.");
            }
        }
    }

    private void persistirRelatorioEIntervencoes(
            ResultadoPrioridade[] resultados,
            IdentityHashMap<TrechoRodovia, Long> idPorTrecho
    ) throws SQLException {
        Connection connection = ConexaoBD.getInstancia().conectar();
        boolean autoCommitOriginal = connection.getAutoCommit();
        if (!autoCommitOriginal) {
            throw new SQLException(
                    "O gerador requer uma conexao sem transacao externa ativa."
            );
        }

        Throwable falha = null;

        try {
            connection.setAutoCommit(false);
            LocalDateTime dataGeracao = LocalDateTime.now();
            EquipeManutencaoDAO equipeTransacional = new EquipeManutencaoDAO(connection);
            RelatorioPrioridadeDAO relatorioTransacional = new RelatorioPrioridadeDAO(connection);
            IntervencaoOperacionalDAO intervencaoTransacional =
                    new IntervencaoOperacionalDAO(connection);
            Map<TipoIntervencao, EquipeManutencaoRecord> equipes =
                    mapearEquipesAtivas(resultados, equipeTransacional);

            relatorioTransacional.salvarRelatorio(criarRegistroRelatorio(resultados, dataGeracao));
            persistirIntervencoes(
                    resultados,
                    idPorTrecho,
                    equipes,
                    dataGeracao,
                    intervencaoTransacional
            );
            connection.commit();
        } catch (SQLException | RuntimeException e) {
            falha = e;
            executarRollback(connection, e);
            throw e;
        } finally {
            restaurarAutoCommit(connection, autoCommitOriginal, falha);
        }
    }

    private Map<TipoIntervencao, EquipeManutencaoRecord> mapearEquipesAtivas(
            ResultadoPrioridade[] resultados,
            EquipeManutencaoDAO equipeTransacional
    ) throws SQLException {
        Set<TipoIntervencao> tiposNecessarios = EnumSet.noneOf(TipoIntervencao.class);
        for (ResultadoPrioridade resultado : resultados) {
            if (resultado.getTipoIntervencao() != TipoIntervencao.SEM_INTERVENCAO) {
                tiposNecessarios.add(resultado.getTipoIntervencao());
            }
        }

        Map<TipoIntervencao, EquipeManutencaoRecord> equipesPorTipo =
                new EnumMap<>(TipoIntervencao.class);
        if (tiposNecessarios.isEmpty()) {
            return equipesPorTipo;
        }

        List<EquipeManutencaoRecord> equipesCadastradas = equipeTransacional.listarTodas();
        for (TipoIntervencao tipo : tiposNecessarios) {
            String nomeEsperado = NOME_EQUIPE_POR_TIPO.get(tipo);
            EquipeManutencaoRecord equipe = localizarEquipeAtiva(equipesCadastradas, nomeEsperado);
            equipesPorTipo.put(tipo, equipe);
        }

        return equipesPorTipo;
    }

    private EquipeManutencaoRecord localizarEquipeAtiva(
            List<EquipeManutencaoRecord> equipes,
            String nomeEsperado
    ) {
        for (EquipeManutencaoRecord equipe : equipes) {
            if (equipe.ativa() && nomeEsperado.equalsIgnoreCase(equipe.nome())) {
                if (equipe.id() == null || equipe.id() <= 0) {
                    throw new IllegalArgumentException(
                            "A equipe ativa " + nomeEsperado + " nao possui ID persistido."
                    );
                }
                return equipe;
            }
        }

        throw new IllegalArgumentException(
                "A equipe obrigatoria " + nomeEsperado + " nao foi encontrada ou esta inativa."
        );
    }

    private void persistirIntervencoes(
            ResultadoPrioridade[] resultados,
            IdentityHashMap<TrechoRodovia, Long> idPorTrecho,
            Map<TipoIntervencao, EquipeManutencaoRecord> equipes,
            LocalDateTime dataGeracao,
            IntervencaoOperacionalDAO intervencaoTransacional
    ) throws SQLException {
        for (ResultadoPrioridade resultado : resultados) {
            TipoIntervencao tipo = resultado.getTipoIntervencao();
            if (tipo == TipoIntervencao.SEM_INTERVENCAO) {
                continue;
            }

            Long idTrecho = idPorTrecho.get(resultado.getTrecho());
            if (idTrecho == null) {
                throw new IllegalArgumentException(
                        "Nao foi possivel identificar o ID persistido do trecho analisado."
                );
            }

            EquipeManutencaoRecord equipe = equipes.get(tipo);
            if (equipe == null) {
                throw new IllegalArgumentException("Nao ha equipe ativa mapeada para " + tipo + ".");
            }

            IntervencaoOperacionalRecord intervencao = new IntervencaoOperacionalRecord(
                    null,
                    idTrecho,
                    equipe.id(),
                    tipo,
                    resultado.getNivelPrioridade(),
                    resultado.getAlturaProjetadaCm(),
                    resultado.getJustificativa(),
                    dataGeracao,
                    STATUS_PLANEJADA
            );
            intervencaoTransacional.inserir(intervencao);
        }
    }

    private RelatorioPrioridadeRecord criarRegistroRelatorio(
            ResultadoPrioridade[] resultados,
            LocalDateTime dataGeracao
    ) {
        Map<NivelPrioridade, Integer> contagens = new EnumMap<>(NivelPrioridade.class);
        for (NivelPrioridade nivel : NivelPrioridade.values()) {
            contagens.put(nivel, 0);
        }

        for (ResultadoPrioridade resultado : resultados) {
            contagens.compute(resultado.getNivelPrioridade(), (nivel, quantidade) -> quantidade + 1);
        }

        String resumo = String.format(
                "Total=%d; NORMAL=%d; BAIXA=%d; MEDIA=%d; ALTA=%d; CRITICA=%d",
                resultados.length,
                contagens.get(NivelPrioridade.NORMAL),
                contagens.get(NivelPrioridade.BAIXA),
                contagens.get(NivelPrioridade.MEDIA),
                contagens.get(NivelPrioridade.ALTA),
                contagens.get(NivelPrioridade.CRITICA)
        );

        return new RelatorioPrioridadeRecord(
                null,
                dataGeracao,
                contagens.get(NivelPrioridade.NORMAL),
                contagens.get(NivelPrioridade.BAIXA),
                contagens.get(NivelPrioridade.MEDIA),
                contagens.get(NivelPrioridade.ALTA),
                contagens.get(NivelPrioridade.CRITICA),
                resumo
        );
    }

    private void executarRollback(Connection connection, Throwable falhaOriginal) {
        try {
            connection.rollback();
        } catch (SQLException falhaRollback) {
            falhaOriginal.addSuppressed(falhaRollback);
        }
    }

    private void restaurarAutoCommit(
            Connection connection,
            boolean autoCommitOriginal,
            Throwable falhaOriginal
    ) throws SQLException {
        try {
            connection.setAutoCommit(autoCommitOriginal);
        } catch (SQLException falhaRestauracao) {
            if (falhaOriginal != null) {
                falhaOriginal.addSuppressed(falhaRestauracao);
                return;
            }
            throw falhaRestauracao;
        }
    }
}
