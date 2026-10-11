package br.com.motiva.repository;
import br.com.motiva.model.EquipeManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Acesso JPA às equipes de manutenção. */
public interface EquipeRepository extends JpaRepository<EquipeManutencao, Long> {
    /** Seleciona uma equipe ativa pela especialidade, independentemente do nome. */
    Optional<EquipeManutencao> findFirstByEspecialidadeIgnoreCaseAndAtivaOrderByIdAsc(
            String especialidade, Boolean ativa);
}
