package br.com.motiva.intervencao;

import br.com.motiva.model.TrechoRodovia;

public class RocadaManual extends IntervencaoOperacional {

    public RocadaManual(String nomeEquipe) {
        super(nomeEquipe);
    }

    @Override
    public String executarServico(TrechoRodovia trecho) {
        return "Roçada manual executada no KM " + trecho.getQuilometro()
                + " (" + trecho.getSentido() + ") pela equipe " + getNomeEquipe() + ".";
    }
}
