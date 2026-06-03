package br.com.motiva.model;

/**
 * Representa o comportamento estimado de crescimento da vegetacao em um trecho.
 */
public enum TipoCrescimento {
    SECO(0.80, "Trecho seco: crescimento reduzido"),
    NORMAL(1.00, "Trecho normal: crescimento dentro do esperado"),
    UMIDO(1.40, "Trecho umido: crescimento acelerado");

    private final double fatorCrescimento;
    private final String descricao;

    TipoCrescimento(double fatorCrescimento, String descricao) {
        this.fatorCrescimento = fatorCrescimento;
        this.descricao = descricao;
    }

    public double getFatorCrescimento() {
        return fatorCrescimento;
    }

    public String getDescricao() {
        return descricao;
    }
}
