package br.com.motiva.service;

import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.TrechoRodovia;

public class ResultadoPrioridade {
    private final TrechoRodovia trecho;
    private final NivelPrioridade nivelPrioridade;
    private final TipoIntervencao tipoIntervencao;
    private final double alturaProjetadaCm;
    private final String justificativa;

    public ResultadoPrioridade(
            TrechoRodovia trecho,
            NivelPrioridade nivelPrioridade,
            TipoIntervencao tipoIntervencao,
            double alturaProjetadaCm,
            String justificativa
    ) {
        this.trecho = trecho;
        this.nivelPrioridade = nivelPrioridade;
        this.tipoIntervencao = tipoIntervencao;
        this.alturaProjetadaCm = alturaProjetadaCm;
        this.justificativa = justificativa;
    }

    public TrechoRodovia getTrecho() {
        return trecho;
    }

    public NivelPrioridade getNivelPrioridade() {
        return nivelPrioridade;
    }

    public TipoIntervencao getTipoIntervencao() {
        return tipoIntervencao;
    }

    public double getAlturaProjetadaCm() {
        return alturaProjetadaCm;
    }

    public String getJustificativa() {
        return justificativa;
    }
}
