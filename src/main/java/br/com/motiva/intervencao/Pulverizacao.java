package br.com.motiva.intervencao;

import br.com.motiva.model.TrechoRodovia;

public class Pulverizacao extends IntervencaoOperacional {

    public Pulverizacao(String nomeEquipe) {
        super(nomeEquipe);
    }

    @Override
    public String executarServico(TrechoRodovia trecho) {
        return "Pulverizacao preventiva executada no KM " + trecho.getQuilometro()
                + " (" + trecho.getSentido() + ") pela equipe " + getNomeEquipe() + ".";
    }
}
