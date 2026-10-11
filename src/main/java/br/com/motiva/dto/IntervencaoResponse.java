package br.com.motiva.dto;
import br.com.motiva.model.IntervencaoEntity;
import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TipoIntervencao;
import java.time.LocalDateTime;
public record IntervencaoResponse(Long id, Long idTrecho, Long idEquipe, TipoIntervencao tipoIntervencao, NivelPrioridade nivelPrioridade, double alturaProjetadaCm, String justificativa, LocalDateTime dataIntervencao, String status) {
    public static IntervencaoResponse from(IntervencaoEntity entity) { return new IntervencaoResponse(entity.getId(), entity.getTrecho().getId(), entity.getEquipe().getId(), entity.getTipoIntervencao(), entity.getNivelPrioridade(), entity.getAlturaProjetadaCm(), entity.getJustificativa(), entity.getDataIntervencao(), entity.getStatus()); }
}
