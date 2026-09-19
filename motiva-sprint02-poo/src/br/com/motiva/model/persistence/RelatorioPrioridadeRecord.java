package br.com.motiva.model.persistence;

import java.time.LocalDateTime;
import java.util.Objects;

public record RelatorioPrioridadeRecord(
        Long id,
        LocalDateTime dataGeracao,
        int qtNormal,
        int qtBaixa,
        int qtMedia,
        int qtAlta,
        int qtCritica,
        String resumo
) {
    public RelatorioPrioridadeRecord {
        validarId(id);
        dataGeracao = Objects.requireNonNull(dataGeracao, "A data de geracao e obrigatoria.");
        validarContagem(qtNormal, "normal");
        validarContagem(qtBaixa, "baixa");
        validarContagem(qtMedia, "media");
        validarContagem(qtAlta, "alta");
        validarContagem(qtCritica, "critica");
        if (resumo == null || resumo.isBlank()) {
            throw new IllegalArgumentException("O resumo do relatorio e obrigatorio.");
        }
        resumo = resumo.trim();
    }

    public int totalTrechos() {
        return qtNormal + qtBaixa + qtMedia + qtAlta + qtCritica;
    }

    private static void validarId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("O ID do relatorio deve ser positivo.");
        }
    }

    private static void validarContagem(int quantidade, String prioridade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de prioridade " + prioridade + " nao pode ser negativa."
            );
        }
    }
}
