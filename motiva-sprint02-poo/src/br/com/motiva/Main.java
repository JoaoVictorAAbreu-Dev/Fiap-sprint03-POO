package br.com.motiva;

import br.com.motiva.dao.EquipeManutencaoDAO;
import br.com.motiva.dao.IntervencaoOperacionalDAO;
import br.com.motiva.dao.RelatorioPrioridadeDAO;
import br.com.motiva.dao.TrechoRodoviaDAO;
import br.com.motiva.db.ConexaoBD;
import br.com.motiva.intervencao.IntervencaoOperacional;
import br.com.motiva.intervencao.Pulverizacao;
import br.com.motiva.intervencao.RocadaManual;
import br.com.motiva.intervencao.RocadaMecanizada;
import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TipoCrescimento;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.persistence.EquipeManutencaoRecord;
import br.com.motiva.model.persistence.IntervencaoOperacionalRecord;
import br.com.motiva.model.persistence.RelatorioPrioridadeRecord;
import br.com.motiva.model.persistence.TrechoRodoviaRecord;
import br.com.motiva.service.GeradorRelatorio;
import br.com.motiva.service.ResultadoPrioridade;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    private static final String LINHA = "============================================================";

    public static void main(String[] args) {
        ConexaoBD conexaoBD = ConexaoBD.getInstancia();

        imprimirCabecalho();
        try {
            testarConexao(conexaoBD);

            EquipeManutencaoDAO equipeDAO = new EquipeManutencaoDAO();
            TrechoRodoviaDAO trechoDAO = new TrechoRodoviaDAO();
            IntervencaoOperacionalDAO intervencaoDAO = new IntervencaoOperacionalDAO();
            RelatorioPrioridadeDAO relatorioDAO = new RelatorioPrioridadeDAO();

            demonstrarCrudEquipe(equipeDAO);
            demonstrarCrudTrecho(trechoDAO);
            demonstrarCrudIntervencao(intervencaoDAO, equipeDAO, trechoDAO);
            demonstrarCrudRelatorio(relatorioDAO);
            gerarRelatorioComDadosPersistidos(trechoDAO);
            listarHistoricoRelatorios(relatorioDAO);
        } catch (SQLException e) {
            System.err.println("Erro SQL durante a demonstracao da Sprint 03: " + e.getMessage());
            e.printStackTrace(System.err);
        } catch (IllegalArgumentException e) {
            System.err.println("Dados invalidos durante a demonstracao da Sprint 03: " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            boolean conexaoEncerrada = conexaoBD.desconectar();
            System.out.println();
            System.out.println(conexaoEncerrada
                    ? "Conexao Oracle encerrada."
                    : "Aviso: nao foi possivel confirmar o fechamento da conexao Oracle.");
        }
    }

    private static void imprimirCabecalho() {
        System.out.println(LINHA);
        System.out.println("MOTIVA - SPRINT 03");
        System.out.println("Persistencia Oracle + JDBC");
        System.out.println(LINHA);
    }

    private static void testarConexao(ConexaoBD conexaoBD) throws SQLException {
        imprimirSecao("[1] TESTANDO CONEXAO");
        conexaoBD.conectar();
        if (!conexaoBD.estaConectado()) {
            throw new SQLException("A conexao Oracle foi criada, mas nao esta ativa.");
        }
        System.out.println("Conexao com Oracle realizada com sucesso.");
    }

    private static void demonstrarCrudEquipe(EquipeManutencaoDAO equipeDAO) throws SQLException {
        imprimirSecao("[2] CRUD EQUIPES");
        Long idTemporario = null;

        try {
            EquipeManutencaoRecord novaEquipe = new EquipeManutencaoRecord(
                    null,
                    "Equipe Temporaria CRUD",
                    "APOIO TEMPORARIO",
                    true
            );
            idTemporario = equipeDAO.inserir(novaEquipe);
            System.out.println("INSERT: equipe temporaria criada com ID " + idTemporario + ".");
            System.out.println("SELECT BY ID: " + equipeDAO.buscarPorId(idTemporario).orElseThrow(
                    () -> new IllegalArgumentException("A equipe temporaria inserida nao foi encontrada.")
            ));

            EquipeManutencaoRecord equipeAtualizada = new EquipeManutencaoRecord(
                    idTemporario,
                    "Equipe Temporaria CRUD Atualizada",
                    "APOIO TEMPORARIO",
                    false
            );
            System.out.println("UPDATE: " + equipeDAO.atualizar(equipeAtualizada));
            imprimirLista("SELECT ALL", equipeDAO.listarTodas());
        } finally {
            if (idTemporario != null) {
                System.out.println("DELETE temporario: " + equipeDAO.deletar(idTemporario));
            }
        }
    }

    private static void demonstrarCrudTrecho(TrechoRodoviaDAO trechoDAO) throws SQLException {
        imprimirSecao("[3] CRUD TRECHOS");
        Long idTemporario = null;

        try {
            TrechoRodoviaRecord novoTrecho = new TrechoRodoviaRecord(
                    null,
                    9999,
                    "TEMPORARIO",
                    20.0,
                    TipoCrescimento.SECO,
                    false,
                    false,
                    null
            );
            idTemporario = trechoDAO.inserir(novoTrecho);
            System.out.println("INSERT: trecho temporario criado com ID " + idTemporario + ".");
            System.out.println("SELECT BY ID: " + trechoDAO.buscarPorId(idTemporario).orElseThrow(
                    () -> new IllegalArgumentException("O trecho temporario inserido nao foi encontrado.")
            ));

            TrechoRodoviaRecord trechoAtualizado = new TrechoRodoviaRecord(
                    idTemporario,
                    9999,
                    "TEMPORARIO",
                    25.0,
                    TipoCrescimento.NORMAL,
                    false,
                    false,
                    null
            );
            System.out.println("UPDATE: " + trechoDAO.atualizar(trechoAtualizado));
            imprimirLista("SELECT ALL", trechoDAO.listarTodas());
        } finally {
            if (idTemporario != null) {
                System.out.println("DELETE temporario: " + trechoDAO.deletar(idTemporario));
            }
        }
    }

    private static void demonstrarCrudIntervencao(
            IntervencaoOperacionalDAO intervencaoDAO,
            EquipeManutencaoDAO equipeDAO,
            TrechoRodoviaDAO trechoDAO
    ) throws SQLException {
        imprimirSecao("[4] CRUD INTERVENCOES");
        Long idEquipe = null;
        Long idTrecho = null;
        Long idIntervencao = null;

        try {
            idEquipe = equipeDAO.inserir(new EquipeManutencaoRecord(
                    null,
                    "Equipe Temporaria Intervencao",
                    "TESTE TEMPORARIO",
                    true
            ));
            idTrecho = trechoDAO.inserir(new TrechoRodoviaRecord(
                    null,
                    9998,
                    "TEMPORARIO",
                    45.0,
                    TipoCrescimento.NORMAL,
                    false,
                    false,
                    null
            ));

            IntervencaoOperacionalRecord novaIntervencao = new IntervencaoOperacionalRecord(
                    null,
                    idTrecho,
                    idEquipe,
                    TipoIntervencao.PULVERIZACAO,
                    NivelPrioridade.MEDIA,
                    45.0,
                    "Registro temporario para demonstracao do CRUD.",
                    LocalDateTime.now(),
                    "PLANEJADA"
            );
            idIntervencao = intervencaoDAO.inserir(novaIntervencao);
            System.out.println("INSERT: intervencao temporaria criada com ID " + idIntervencao + ".");
            System.out.println("SELECT BY ID: " + intervencaoDAO.buscarPorId(idIntervencao).orElseThrow(
                    () -> new IllegalArgumentException(
                            "A intervencao temporaria inserida nao foi encontrada."
                    )
            ));

            IntervencaoOperacionalRecord intervencaoAtualizada = new IntervencaoOperacionalRecord(
                    idIntervencao,
                    idTrecho,
                    idEquipe,
                    TipoIntervencao.PULVERIZACAO,
                    NivelPrioridade.MEDIA,
                    45.0,
                    "Registro temporario atualizado antes da exclusao.",
                    novaIntervencao.dataIntervencao(),
                    "CANCELADA"
            );
            System.out.println("UPDATE: " + intervencaoDAO.atualizar(intervencaoAtualizada));
            imprimirLista("SELECT ALL", intervencaoDAO.listarTodas());
        } finally {
            if (idIntervencao != null) {
                System.out.println("DELETE intervencao temporaria: " + intervencaoDAO.deletar(idIntervencao));
            }
            if (idTrecho != null) {
                System.out.println("DELETE trecho de apoio temporario: " + trechoDAO.deletar(idTrecho));
            }
            if (idEquipe != null) {
                System.out.println("DELETE equipe de apoio temporaria: " + equipeDAO.deletar(idEquipe));
            }
        }
    }

    private static void demonstrarCrudRelatorio(RelatorioPrioridadeDAO relatorioDAO)
            throws SQLException {
        imprimirSecao("[5] CRUD RELATORIOS");
        Long idTemporario = null;

        try {
            RelatorioPrioridadeRecord novoRelatorio = new RelatorioPrioridadeRecord(
                    null,
                    LocalDateTime.now(),
                    0,
                    0,
                    0,
                    0,
                    0,
                    "Relatorio temporario para demonstracao do CRUD."
            );
            idTemporario = relatorioDAO.inserir(novoRelatorio);
            System.out.println("INSERT: relatorio temporario criado com ID " + idTemporario + ".");
            System.out.println("SELECT BY ID: " + relatorioDAO.buscarPorId(idTemporario).orElseThrow(
                    () -> new IllegalArgumentException(
                            "O relatorio temporario inserido nao foi encontrado."
                    )
            ));

            RelatorioPrioridadeRecord relatorioAtualizado = new RelatorioPrioridadeRecord(
                    idTemporario,
                    novoRelatorio.dataGeracao(),
                    1,
                    0,
                    0,
                    0,
                    0,
                    "Relatorio temporario atualizado antes da exclusao."
            );
            System.out.println("UPDATE: " + relatorioDAO.atualizar(relatorioAtualizado));
            imprimirLista("SELECT ALL", relatorioDAO.listarTodas());
        } finally {
            if (idTemporario != null) {
                System.out.println("DELETE temporario: " + relatorioDAO.deletar(idTemporario));
            }
        }
    }

    private static void gerarRelatorioComDadosPersistidos(TrechoRodoviaDAO trechoDAO)
            throws SQLException {
        imprimirSecao("[6] GERANDO E PERSISTINDO RELATORIO E INTERVENCOES");
        List<TrechoRodoviaRecord> trechos = trechoDAO.listarTodas();
        if (trechos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nenhum trecho foi encontrado. Execute primeiro o script de dados da Sprint 03."
            );
        }

        System.out.println("Trechos carregados do Oracle: " + trechos.size());
        ResultadoPrioridade[] resultados = new GeradorRelatorio().gerarRelatorio(trechos);
        executarIntervencoesIndicadas(resultados);
        System.out.println("Relatorio e intervencoes planejadas persistidos com sucesso.");
    }

    private static void executarIntervencoesIndicadas(ResultadoPrioridade[] resultados) {
        System.out.println();
        System.out.println("EXECUCAO SIMULADA DAS INTERVENCOES (POLIMORFISMO DA SPRINT 02)");
        System.out.println("------------------------------------------------------------");

        for (ResultadoPrioridade resultado : resultados) {
            IntervencaoOperacional intervencao = criarIntervencao(resultado.getTipoIntervencao());
            if (intervencao == null) {
                System.out.println(
                        "KM " + resultado.getTrecho().getQuilometro() + ": nenhuma equipe acionada."
                );
            } else {
                System.out.println(intervencao.executarServico(resultado.getTrecho()));
            }
        }
    }

    private static IntervencaoOperacional criarIntervencao(TipoIntervencao tipoIntervencao) {
        return switch (tipoIntervencao) {
            case ROCADA_MECANIZADA -> new RocadaMecanizada("Equipe Alpha");
            case ROCADA_MANUAL -> new RocadaManual("Equipe Beta");
            case PULVERIZACAO -> new Pulverizacao("Equipe Preventiva");
            case SEM_INTERVENCAO -> null;
        };
    }

    private static void listarHistoricoRelatorios(RelatorioPrioridadeDAO relatorioDAO)
            throws SQLException {
        imprimirSecao("[7] HISTORICO DE RELATORIOS");
        imprimirLista("Relatorios persistidos", relatorioDAO.listarTodas());
    }

    private static void imprimirSecao(String titulo) {
        System.out.println();
        System.out.println(LINHA);
        System.out.println(titulo);
        System.out.println(LINHA);
    }

    private static void imprimirLista(String titulo, List<?> registros) {
        System.out.println(titulo + " (" + registros.size() + "):");
        for (Object registro : registros) {
            System.out.println("  - " + registro);
        }
    }
}
