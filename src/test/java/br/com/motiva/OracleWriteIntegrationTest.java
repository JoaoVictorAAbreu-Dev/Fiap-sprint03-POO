package br.com.motiva;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.RelatorioRepository;
import br.com.motiva.repository.TrechoRepository;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Teste opcional no Oracle FIAP; todas as escritas participam da transacao revertida. */
@EnabledIfEnvironmentVariable(named = "FIAP_ORACLE_TEST", matches = "true")
@SpringBootTest
@AutoConfigureMockMvc
class OracleWriteIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired EquipeRepository equipes;
    @Autowired TrechoRepository trechos;
    @Autowired IntervencaoRepository intervencoes;
    @Autowired RelatorioRepository relatorios;

    @Test
    @Transactional
    @Rollback
    void crudERelatorioNoOracleSemPersistirDadosDeQa() throws Exception {
        long equipesAntes = equipes.count();
        long trechosAntes = trechos.count();
        long intervencoesAntes = intervencoes.count();
        long relatoriosAntes = relatorios.count();
        String sufixo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String nome = "QA EQUIPE " + sufixo;
        String sentido = "QA" + sufixo;
        int quilometro = 900000 + Math.floorMod(sufixo.hashCode(), 100000);

        String equipeJson = "{\"nome\":\"" + nome + "\",\"especialidade\":\"ROCADA MANUAL\",\"ativa\":true}";
        long equipeId = id(mvc.perform(post("/api/equipes").contentType(MediaType.APPLICATION_JSON)
                .content(equipeJson)).andExpect(status().isCreated()).andReturn());
        mvc.perform(get("/api/equipes/" + equipeId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(nome));
        mvc.perform(put("/api/equipes/" + equipeId).contentType(MediaType.APPLICATION_JSON)
                .content(equipeJson.replace("ROCADA MANUAL", "ROCADA MECANIZADA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.especialidade").value("ROCADA MECANIZADA"));

        String trechoJson = "{\"quilometro\":" + quilometro + ",\"sentido\":\"" + sentido
                + "\",\"alturaVegetacaoCm\":15,\"tipoCrescimento\":\"NORMAL\","
                + "\"areaSensivel\":false,\"monitoradoIot\":false,\"codigoSensor\":null}";
        long trechoId = id(mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON)
                .content(trechoJson)).andExpect(status().isCreated()).andReturn());
        mvc.perform(get("/api/trechos/" + trechoId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.quilometro").value(quilometro));
        mvc.perform(put("/api/trechos/" + trechoId).contentType(MediaType.APPLICATION_JSON)
                .content(trechoJson.replace("\"alturaVegetacaoCm\":15", "\"alturaVegetacaoCm\":18")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alturaVegetacaoCm").value(18));

        String intervencaoJson = "{\"idTrecho\":" + trechoId + ",\"idEquipe\":" + equipeId
                + ",\"tipoIntervencao\":\"ROCADA_MECANIZADA\",\"nivelPrioridade\":\"CRITICA\","
                + "\"alturaProjetadaCm\":90,\"justificativa\":\"Teste transacional Oracle\","
                + "\"status\":\"PLANEJADA\"}";
        long intervencaoId = id(mvc.perform(post("/api/intervencoes")
                .contentType(MediaType.APPLICATION_JSON).content(intervencaoJson))
                .andExpect(status().isCreated()).andReturn());
        mvc.perform(get("/api/intervencoes/" + intervencaoId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.idEquipe").value(equipeId));
        mvc.perform(put("/api/intervencoes/" + intervencaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(intervencaoJson.replace("PLANEJADA", "CONCLUIDA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));
        mvc.perform(delete("/api/intervencoes/" + intervencaoId)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/trechos/" + trechoId)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/equipes/" + equipeId)).andExpect(status().isNoContent());
        assertThat(equipes.count()).isEqualTo(equipesAntes);
        assertThat(trechos.count()).isEqualTo(trechosAntes);
        assertThat(intervencoes.count()).isEqualTo(intervencoesAntes);

        mvc.perform(post("/api/relatorios")).andExpect(status().isCreated());
        assertThat(relatorios.count()).isEqualTo(relatoriosAntes + 1);
        assertThat(intervencoes.count()).isGreaterThan(intervencoesAntes);
    }

    private long id(MvcResult result) throws Exception {
        return json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
