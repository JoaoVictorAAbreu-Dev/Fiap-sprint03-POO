package br.com.motiva;

import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.model.IntervencaoEntity;
import br.com.motiva.model.TipoCrescimento;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.TrechoEntity;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.TrechoRepository;
import br.com.motiva.service.RelatorioService;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:motiva_especialidade;MODE=Oracle;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=file:sql/schema-oracle.sql"
})
class RelatorioEspecialidadeTest {
    @Autowired EquipeRepository equipes;
    @Autowired TrechoRepository trechos;
    @Autowired IntervencaoRepository intervencoes;
    @Autowired RelatorioService relatorios;

    @Test
    @Transactional
    void escolheEquipesPelaEspecialidadeSemDependerDoNome() {
        long mecanizada = equipes.saveAndFlush(new EquipeManutencao("Equipe sem nome padrao", "rocada mecanizada", true)).getId();
        equipes.saveAndFlush(new EquipeManutencao("Equipe reserva", "ROCADA MECANIZADA", true));
        long manual = equipes.saveAndFlush(new EquipeManutencao("Equipe de campo", "ROCADA MANUAL", true)).getId();
        long preventiva = equipes.saveAndFlush(new EquipeManutencao("Equipe de apoio", "PULVERIZACAO PREVENTIVA", true)).getId();

        trechos.saveAndFlush(new TrechoEntity(100, "NORTE", 100, TipoCrescimento.NORMAL, false, false, null));
        trechos.saveAndFlush(new TrechoEntity(101, "NORTE", 70, TipoCrescimento.NORMAL, false, false, null));
        trechos.saveAndFlush(new TrechoEntity(102, "NORTE", 50, TipoCrescimento.NORMAL, false, false, null));

        var relatorio = relatorios.gerar();
        assertThat(relatorio.getQtCritica()).isEqualTo(1);
        assertThat(relatorio.getQtAlta()).isEqualTo(1);
        assertThat(relatorio.getQtMedia()).isEqualTo(1);

        Map<TipoIntervencao, Long> equipePorTipo = intervencoes.findAll().stream()
                .collect(Collectors.toMap(IntervencaoEntity::getTipoIntervencao,
                        intervencao -> intervencao.getEquipe().getId()));
        assertThat(equipePorTipo).containsEntry(TipoIntervencao.ROCADA_MECANIZADA, mecanizada)
                .containsEntry(TipoIntervencao.ROCADA_MANUAL, manual)
                .containsEntry(TipoIntervencao.PULVERIZACAO, preventiva);
    }
}
