package br.com.motiva.repository;
import br.com.motiva.model.RelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface RelatorioRepository extends JpaRepository<RelatorioEntity, Long> {
    List<RelatorioEntity> findByDataGeracaoBetweenOrderByDataGeracaoDesc(LocalDateTime inicio, LocalDateTime fim);
    List<RelatorioEntity> findAllByOrderByDataGeracaoDesc();
}
