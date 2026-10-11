package br.com.motiva.service;

import br.com.motiva.iot.MonitoravelViaIoT;
import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.TrechoRodovia;

/**
 * Motor de regras responsavel por analisar trechos e indicar a intervencao mais adequada.
 */
public class MotorRegrasPrioridade {
    private static final double LIMITE_PULVERIZACAO_CM = 40.0;
    private static final double LIMITE_ROCADA_MANUAL_CM = 60.0;
    private static final double LIMITE_ROCADA_MECANIZADA_CM = 90.0;

    public ResultadoPrioridade analisarTrecho(TrechoRodovia trecho) {
        double alturaBase = obterAlturaBase(trecho);
        double alturaProjetada = alturaBase * trecho.getTipoCrescimento().getFatorCrescimento();

        if (trecho.isAreaSensivel()) {
            alturaProjetada += 10.0;
        }

        if (alturaProjetada >= LIMITE_ROCADA_MECANIZADA_CM) {
            return new ResultadoPrioridade(
                    trecho,
                    NivelPrioridade.CRITICA,
                    TipoIntervencao.ROCADA_MECANIZADA,
                    alturaProjetada,
                    "Vegetacao projetada em nivel critico. Indica risco operacional e exige roçada mecanizada."
            );
        }

        if (alturaProjetada >= LIMITE_ROCADA_MANUAL_CM) {
            return new ResultadoPrioridade(
                    trecho,
                    NivelPrioridade.ALTA,
                    TipoIntervencao.ROCADA_MANUAL,
                    alturaProjetada,
                    "Vegetacao acima do limite de atencao. Recomendada roçada manual."
            );
        }

        if (alturaProjetada >= LIMITE_PULVERIZACAO_CM) {
            return new ResultadoPrioridade(
                    trecho,
                    NivelPrioridade.MEDIA,
                    TipoIntervencao.PULVERIZACAO,
                    alturaProjetada,
                    "Vegetacao em crescimento inicial. Recomendada pulverizacao preventiva."
            );
        }

        return new ResultadoPrioridade(
                trecho,
                NivelPrioridade.NORMAL,
                TipoIntervencao.SEM_INTERVENCAO,
                alturaProjetada,
                "Trecho dentro do limite operacional. Nao ha necessidade de intervencao imediata."
        );
    }

    public ResultadoPrioridade[] gerarRelatorioPrioridade(TrechoRodovia[] trechos) {
        ResultadoPrioridade[] resultados = new ResultadoPrioridade[trechos.length];

        for (int i = 0; i < trechos.length; i++) {
            resultados[i] = analisarTrecho(trechos[i]);
        }

        return resultados;
    }

    private double obterAlturaBase(TrechoRodovia trecho) {
        if (trecho instanceof MonitoravelViaIoT monitoravel) {
            return monitoravel.transmitirDadosSensor();
        }

        return trecho.getAlturaVegetacaoCm();
    }
}
