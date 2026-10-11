package br.com.motiva.service;
import br.com.motiva.dto.EquipeRequest;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class EquipeService {
    private final EquipeRepository equipes;
    private final IntervencaoRepository intervencoes;
    public EquipeService(EquipeRepository equipes, IntervencaoRepository intervencoes) { this.equipes=equipes; this.intervencoes=intervencoes; }
    @Transactional(readOnly=true) public List<EquipeManutencao> listar() { return equipes.findAll(); }
    @Transactional(readOnly=true) public EquipeManutencao buscar(Long id) { return equipes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipe", id)); }
    @Transactional public EquipeManutencao criar(EquipeRequest dados) { return equipes.saveAndFlush(new EquipeManutencao(dados.nome().trim(), dados.especialidade().trim(), dados.ativa())); }
    @Transactional public EquipeManutencao atualizar(Long id, EquipeRequest dados) { EquipeManutencao equipe=buscar(id); equipe.atualizar(dados.nome().trim(),dados.especialidade().trim(),dados.ativa()); return equipes.saveAndFlush(equipe); }
    @Transactional public void excluir(Long id) { buscar(id); if (intervencoes.existsByEquipeId(id)) throw new IllegalArgumentException("Equipe possui intervencoes; remova as referencias antes."); equipes.deleteById(id); equipes.flush(); }
}
