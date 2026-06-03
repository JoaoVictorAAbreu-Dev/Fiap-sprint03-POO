package br.com.motiva.intervencao;

import br.com.motiva.model.TrechoRodovia;

/**
 * Classe abstrata que representa o conceito base de uma intervencao operacional.
 * Nao deve ser instanciada diretamente, pois cada servico real possui regras proprias.
 */
public abstract class IntervencaoOperacional {
    private final String nomeEquipe;

    protected IntervencaoOperacional(String nomeEquipe) {
        if (nomeEquipe == null || nomeEquipe.isBlank()) {
            throw new IllegalArgumentException("O nome da equipe e obrigatorio.");
        }
        this.nomeEquipe = nomeEquipe;
    }

    public String getNomeEquipe() {
        return nomeEquipe;
    }

    public abstract String executarServico(TrechoRodovia trecho);
}
