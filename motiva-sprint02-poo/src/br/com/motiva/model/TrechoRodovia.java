package br.com.motiva.model;

import java.util.Objects;

/**
 * Representa um trecho de rodovia analisado pelo motor de regras.
 */
public class TrechoRodovia {
    private final int quilometro;
    private final String sentido;
    private double alturaVegetacaoCm;
    private final TipoCrescimento tipoCrescimento;
    private final boolean areaSensivel;

    public TrechoRodovia(
            int quilometro,
            String sentido,
            double alturaVegetacaoCm,
            TipoCrescimento tipoCrescimento,
            boolean areaSensivel
    ) {
        validarQuilometro(quilometro);
        validarAlturaVegetacao(alturaVegetacaoCm);
        this.quilometro = quilometro;
        this.sentido = Objects.requireNonNull(sentido, "O sentido do trecho e obrigatorio.");
        this.alturaVegetacaoCm = alturaVegetacaoCm;
        this.tipoCrescimento = Objects.requireNonNull(tipoCrescimento, "O tipo de crescimento e obrigatorio.");
        this.areaSensivel = areaSensivel;
    }

    public int getQuilometro() {
        return quilometro;
    }

    public String getSentido() {
        return sentido;
    }

    public double getAlturaVegetacaoCm() {
        return alturaVegetacaoCm;
    }

    public TipoCrescimento getTipoCrescimento() {
        return tipoCrescimento;
    }

    public boolean isAreaSensivel() {
        return areaSensivel;
    }

    public void atualizarAlturaVegetacaoCm(double novaAlturaVegetacaoCm) {
        validarAlturaVegetacao(novaAlturaVegetacaoCm);
        this.alturaVegetacaoCm = novaAlturaVegetacaoCm;
    }

    public double calcularAlturaProjetadaCm() {
        return alturaVegetacaoCm * tipoCrescimento.getFatorCrescimento();
    }

    private void validarQuilometro(int quilometro) {
        if (quilometro < 0) {
            throw new IllegalArgumentException("O quilometro nao pode ser negativo.");
        }
    }

    private void validarAlturaVegetacao(double alturaVegetacaoCm) {
        if (alturaVegetacaoCm < 0) {
            throw new IllegalArgumentException("A altura da vegetacao nao pode ser negativa.");
        }
    }
}
