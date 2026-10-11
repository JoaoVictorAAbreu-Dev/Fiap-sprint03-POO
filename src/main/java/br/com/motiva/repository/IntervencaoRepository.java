package br.com.motiva.repository;
import br.com.motiva.model.IntervencaoEntity;
import br.com.motiva.model.TipoIntervencao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Acesso JPA às intervenções e verificação de referências a trechos e equipes. */
public interface IntervencaoRepository extends JpaRepository<IntervencaoEntity, Long> {
    List<IntervencaoEntity> findByTipoIntervencao(TipoIntervencao tipo);
    boolean existsByTrechoId(Long trechoId);
    boolean existsByEquipeId(Long equipeId);
}
