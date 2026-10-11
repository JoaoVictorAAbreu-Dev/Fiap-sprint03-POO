package br.com.motiva.service;
import br.com.motiva.dto.IntervencaoRequest;
import br.com.motiva.dto.IntervencaoResponse;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.IntervencaoEntity;
import br.com.motiva.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class IntervencaoService {
    private static final Set<String> STATUS=Set.of("PLANEJADA","EM_ANDAMENTO","CONCLUIDA","CANCELADA");
    private final IntervencaoRepository intervencoes; private final TrechoRepository trechos; private final EquipeRepository equipes;
    public IntervencaoService(IntervencaoRepository intervencoes, TrechoRepository trechos, EquipeRepository equipes) { this.intervencoes=intervencoes; this.trechos=trechos; this.equipes=equipes; }
    @Transactional(readOnly=true) public List<IntervencaoResponse> listar() { return intervencoes.findAll().stream().map(IntervencaoResponse::from).toList(); }
    @Transactional(readOnly=true) public IntervencaoResponse buscar(Long id) { return IntervencaoResponse.from(entidade(id)); }
    private IntervencaoEntity entidade(Long id) { return intervencoes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Intervencao",id)); }
    @Transactional public IntervencaoResponse criar(IntervencaoRequest dados) { validar(dados); var entity=new IntervencaoEntity(trecho(dados.idTrecho()),equipe(dados.idEquipe()),dados.tipoIntervencao(),dados.nivelPrioridade(),dados.alturaProjetadaCm(),dados.justificativa().trim(),LocalDateTime.now(),dados.status()); return IntervencaoResponse.from(intervencoes.saveAndFlush(entity)); }
    @Transactional public IntervencaoResponse atualizar(Long id, IntervencaoRequest dados) { validar(dados); var entity=entidade(id); entity.atualizar(trecho(dados.idTrecho()),equipe(dados.idEquipe()),dados.tipoIntervencao(),dados.nivelPrioridade(),dados.alturaProjetadaCm(),dados.justificativa().trim(),entity.getDataIntervencao(),dados.status()); return IntervencaoResponse.from(intervencoes.saveAndFlush(entity)); }
    @Transactional public void excluir(Long id) { entidade(id); intervencoes.deleteById(id); intervencoes.flush(); }
    private br.com.motiva.model.TrechoEntity trecho(Long id) { return trechos.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trecho",id)); }
    private br.com.motiva.model.EquipeManutencao equipe(Long id) { return equipes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipe",id)); }
    private void validar(IntervencaoRequest dados) { if (!Double.isFinite(dados.alturaProjetadaCm()) || dados.alturaProjetadaCm()<0) throw new IllegalArgumentException("Altura projetada invalida."); if (!STATUS.contains(dados.status())) throw new IllegalArgumentException("Status invalido."); }
}
