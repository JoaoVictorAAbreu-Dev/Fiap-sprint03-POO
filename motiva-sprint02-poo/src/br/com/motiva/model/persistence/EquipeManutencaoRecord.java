package br.com.motiva.model.persistence;

public record EquipeManutencaoRecord(
        Long id,
        String nome,
        String especialidade,
        boolean ativa
) {
    public EquipeManutencaoRecord {
        validarId(id);
        nome = validarTextoObrigatorio(nome, "O nome da equipe e obrigatorio.");
        especialidade = validarTextoObrigatorio(
                especialidade,
                "A especialidade da equipe e obrigatoria."
        );
    }

    private static void validarId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("O ID da equipe deve ser positivo.");
        }
    }

    private static String validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor.trim();
    }
}
