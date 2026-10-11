package br.com.motiva;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import br.com.motiva.model.TipoCrescimento;
import br.com.motiva.repository.TrechoRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:motiva;MODE=Oracle;DB_CLOSE_DELAY=-1", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.hibernate.dialect=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=validate", "spring.sql.init.mode=always", "spring.sql.init.schema-locations=file:sql/schema-oracle.sql"})
@AutoConfigureMockMvc
class MotivaApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired TrechoRepository trechos;
    private MvcResult criar(String path, String body) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(path).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn();
    }
    private long id(MvcResult result) throws Exception { JsonNode node=json.readTree(result.getResponse().getContentAsString()); return node.get("id").asLong(); }
    @Test void crudERelatorioComCodigosHttp() throws Exception {
        long alpha=id(criar("/api/equipes", "{\"nome\":\"Equipe Alpha\",\"especialidade\":\"ROCADA MECANIZADA\",\"ativa\":true}"));
        long beta=id(criar("/api/equipes", "{\"nome\":\"Equipe Beta\",\"especialidade\":\"ROCADA MANUAL\",\"ativa\":true}"));
        long preventiva=id(criar("/api/equipes", "{\"nome\":\"Equipe Preventiva\",\"especialidade\":\"PULVERIZACAO PREVENTIVA\",\"ativa\":true}"));
        mvc.perform(put("/api/equipes/"+alpha).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Equipe de Campo\",\"especialidade\":\"ROCADA MECANIZADA\",\"ativa\":true}")).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Equipe de Campo"));
        String trecho="{\"quilometro\":10,\"sentido\":\"NORTE\",\"alturaVegetacaoCm\":100,\"tipoCrescimento\":\"NORMAL\",\"areaSensivel\":false,\"monitoradoIot\":false,\"codigoSensor\":null}";
        long trechoId=id(criar("/api/trechos", trecho));
        mvc.perform(get("/api/trechos/"+trechoId)).andExpect(status().isOk()).andExpect(jsonPath("$.quilometro").value(10));
        assertThat(trechos.findByAlturaVegetacaoCmGreaterThanEqual(90.0)).hasSize(1);
        assertThat(trechos.findByTipoCrescimento(TipoCrescimento.NORMAL)).hasSize(1);
        mvc.perform(put("/api/trechos/"+trechoId).contentType(MediaType.APPLICATION_JSON).content(trecho.replace("\"quilometro\":10", "\"quilometro\":11"))).andExpect(status().isOk()).andExpect(jsonPath("$.quilometro").value(11));
        mvc.perform(get("/api/trechos/999999")).andExpect(status().isNotFound());
        mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON).content(trecho.replace("\"alturaVegetacaoCm\":100", "\"alturaVegetacaoCm\":-1"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/relatorios")).andExpect(status().isCreated()).andExpect(jsonPath("$.qtCritica").value(1));
        mvc.perform(get("/api/relatorios")).andExpect(status().isOk()).andExpect(jsonPath("$[0].qtCritica").value(1));
        mvc.perform(get("/api/relatorios/periodo").param("inicio","2020-01-01T00:00:00").param("fim","2030-01-01T00:00:00")).andExpect(status().isOk()).andExpect(jsonPath("$[0].qtCritica").value(1));
        mvc.perform(get("/api/relatorios/periodo").param("inicio","2030-01-01T00:00:00").param("fim","2020-01-01T00:00:00")).andExpect(status().isBadRequest());
        mvc.perform(put("/api/equipes/"+alpha).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Equipe de Campo\",\"especialidade\":\"ROCADA MECANIZADA\",\"ativa\":false}")).andExpect(status().isOk()).andExpect(jsonPath("$.ativa").value(false));
        mvc.perform(post("/api/relatorios")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/api/equipes/"+alpha).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Equipe de Campo\",\"especialidade\":\"ROCADA MECANIZADA\",\"ativa\":true}")).andExpect(status().isOk());
        MvcResult lista=mvc.perform(get("/api/intervencoes")).andExpect(status().isOk()).andReturn();
        JsonNode itens=json.readTree(lista.getResponse().getContentAsString());
        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).get("idEquipe").asLong()).isEqualTo(alpha);
        long intervencaoId=itens.get(0).get("id").asLong();
        mvc.perform(delete("/api/intervencoes/"+intervencaoId)).andExpect(status().isNoContent());
        String manual="{\"idTrecho\":"+trechoId+",\"idEquipe\":"+beta+",\"tipoIntervencao\":\"ROCADA_MANUAL\",\"nivelPrioridade\":\"ALTA\",\"alturaProjetadaCm\":70,\"justificativa\":\"Teste manual\",\"status\":\"PLANEJADA\"}";
        long novoId=id(criar("/api/intervencoes",manual));
        mvc.perform(get("/api/intervencoes/"+novoId)).andExpect(status().isOk()).andExpect(jsonPath("$.tipoIntervencao").value("ROCADA_MANUAL"));
        mvc.perform(put("/api/intervencoes/"+novoId).contentType(MediaType.APPLICATION_JSON).content(manual.replace("PLANEJADA","CONCLUIDA"))).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CONCLUIDA"));
        mvc.perform(delete("/api/intervencoes/"+novoId)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/trechos/"+trechoId)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/equipes/"+beta)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/equipes/"+preventiva)).andExpect(status().isNoContent());
    }
}
