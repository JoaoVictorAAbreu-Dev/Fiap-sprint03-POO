package br.com.motiva.repository;
import br.com.motiva.model.EquipeManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface EquipeRepository extends JpaRepository<EquipeManutencao, Long> {
    Optional<EquipeManutencao> findByNomeIgnoreCaseAndAtiva(String nome, Boolean ativa);
    java.util.List<EquipeManutencao> findByEspecialidade(String especialidade);
}
