package br.com.motiva.service;

import br.com.motiva.model.*;
import br.com.motiva.repository.*;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelatorioService {
    private static final Map<TipoIntervencao,String> EQUIPES = Map.of(
            TipoIntervencao.ROCADA_MECANIZADA,"Equipe Alpha",
            TipoIntervencao.ROCADA_MANUAL,"Equipe Beta",
            TipoIntervencao.PULVERIZACAO,"Equipe Preventiva");
    private final TrechoRepository trechos;
    private final EquipeRepository equipes;
    private final IntervencaoRepository intervencoes;
    private final RelatorioRepository relatorios;
    private final MotorRegrasPrioridade motor = new MotorRegrasPrioridade();
    public RelatorioService(TrechoRepository trechos, EquipeRepository equipes, IntervencaoRepository intervencoes, RelatorioRepository relatorios) {
        this.trechos=trechos; this.equipes=equipes; this.intervencoes=intervencoes; this.relatorios=relatorios;
    }
    @Transactional
    public RelatorioEntity gerar() {
        List<TrechoEntity> atuais=trechos.findAll();
        Map<NivelPrioridade,Integer> contagem=new EnumMap<>(NivelPrioridade.class);
        for (NivelPrioridade nivel:NivelPrioridade.values()) contagem.put(nivel,0);
        LocalDateTime agora=LocalDateTime.now();
        for (TrechoEntity trecho:atuais) {
            ResultadoPrioridade resultado=motor.analisarTrecho(trecho.toDomain());
            contagem.merge(resultado.getNivelPrioridade(),1,Integer::sum);
            if (resultado.getTipoIntervencao()!=TipoIntervencao.SEM_INTERVENCAO) {
                String nome=EQUIPES.get(resultado.getTipoIntervencao());
                EquipeManutencao equipe=equipes.findByNomeIgnoreCaseAndAtiva(nome,true)
                        .orElseThrow(() -> new IllegalArgumentException("Equipe obrigatoria " + nome + " nao encontrada ou inativa."));
                intervencoes.save(new IntervencaoEntity(trecho,equipe,resultado.getTipoIntervencao(),resultado.getNivelPrioridade(),resultado.getAlturaProjetadaCm(),resultado.getJustificativa(),agora,"PLANEJADA"));
            }
        }
        String resumo="Trechos analisados: " + atuais.size() + "; NORMAL=" + contagem.get(NivelPrioridade.NORMAL)
                + "; BAIXA=" + contagem.get(NivelPrioridade.BAIXA) + "; MEDIA=" + contagem.get(NivelPrioridade.MEDIA)
                + "; ALTA=" + contagem.get(NivelPrioridade.ALTA) + "; CRITICA=" + contagem.get(NivelPrioridade.CRITICA);
        return relatorios.saveAndFlush(new RelatorioEntity(agora,contagem.get(NivelPrioridade.NORMAL),contagem.get(NivelPrioridade.BAIXA),contagem.get(NivelPrioridade.MEDIA),contagem.get(NivelPrioridade.ALTA),contagem.get(NivelPrioridade.CRITICA),resumo));
    }
    @Transactional(readOnly=true) public RelatorioEntity buscar(Long id) { return relatorios.findById(id).orElseThrow(() -> new br.com.motiva.exception.ResourceNotFoundException("Relatorio",id)); }
    @Transactional(readOnly=true) public List<RelatorioEntity> listar() { return relatorios.findAllByOrderByDataGeracaoDesc(); }
    @Transactional(readOnly=true) public List<RelatorioEntity> periodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.isAfter(fim)) throw new IllegalArgumentException("Inicio deve ser anterior ou igual ao fim.");
        return relatorios.findByDataGeracaoBetweenOrderByDataGeracaoDesc(inicio,fim);
    }
}
