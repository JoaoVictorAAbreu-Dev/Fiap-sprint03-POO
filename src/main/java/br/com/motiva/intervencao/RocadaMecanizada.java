package br.com.motiva.intervencao;

import br.com.motiva.model.TrechoRodovia;

public class RocadaMecanizada extends IntervencaoOperacional {

    public RocadaMecanizada(String nomeEquipe) {
        super(nomeEquipe);
    }

    @Override
    public String executarServico(TrechoRodovia trecho) {
        return "Roçada mecanizada executada no KM " + trecho.getQuilometro()
                + " (" + trecho.getSentido() + ") pela equipe " + getNomeEquipe() + ".";
    }
}
