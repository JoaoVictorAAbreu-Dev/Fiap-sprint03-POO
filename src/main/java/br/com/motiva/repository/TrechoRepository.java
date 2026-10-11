package br.com.motiva.repository;
import br.com.motiva.model.TrechoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TrechoRepository extends JpaRepository<TrechoEntity, Long> {
    List<TrechoEntity> findByAlturaVegetacaoCmGreaterThanEqual(Double minimo);
    List<TrechoEntity> findByTipoCrescimento(br.com.motiva.model.TipoCrescimento tipo);
}
