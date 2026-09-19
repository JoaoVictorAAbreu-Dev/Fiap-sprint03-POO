package br.com.motiva.model.persistence;

import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TipoIntervencao;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public record IntervencaoOperacionalRecord(
        Long id,
        long idTrecho,
        long idEquipe,
        TipoIntervencao tipoIntervencao,
        NivelPrioridade nivelPrioridade,
        double alturaProjetadaCm,
        String justificativa,
        LocalDateTime dataIntervencao,
        String status
) {
    private static final Set<String> STATUS_VALIDOS = Set.of(
            "PLANEJADA",
            "EM_ANDAMENTO",
            "CONCLUIDA",
            "CANCELADA"
    );

    public IntervencaoOperacionalRecord {
        validarId(id);
        if (idTrecho <= 0) {
            throw new IllegalArgumentException("O ID do trecho deve ser positivo.");
        }
        if (idEquipe <= 0) {
            throw new IllegalArgumentException("O ID da equipe deve ser positivo.");
        }
        tipoIntervencao = Objects.requireNonNull(
                tipoIntervencao,
                "O tipo de intervencao e obrigatorio."
        );
        nivelPrioridade = Objects.requireNonNull(
                nivelPrioridade,
                "O nivel de prioridade e obrigatorio."
        );
        if (!Double.isFinite(alturaProjetadaCm) || alturaProjetadaCm < 0) {
            throw new IllegalArgumentException(
                    "A altura projetada deve ser um numero finito e nao negativo."
            );
        }
        justificativa = validarTextoObrigatorio(
                justificativa,
                "A justificativa da intervencao e obrigatoria."
        );
        dataIntervencao = Objects.requireNonNull(
                dataIntervencao,
                "A data da intervencao e obrigatoria."
        );
        status = validarTextoObrigatorio(status, "O status da intervencao e obrigatorio.")
                .toUpperCase(Locale.ROOT);
        if (!STATUS_VALIDOS.contains(status)) {
            throw new IllegalArgumentException(
                    "Status invalido. Use PLANEJADA, EM_ANDAMENTO, CONCLUIDA ou CANCELADA."
            );
        }
    }

    private static void validarId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("O ID da intervencao deve ser positivo.");
        }
    }

    private static String validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor.trim();
    }
}
