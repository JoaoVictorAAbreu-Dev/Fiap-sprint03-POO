package br.com.motiva.model;

import br.com.motiva.iot.MonitoravelViaIoT;

/**
 * Trecho com sensor instalado, capaz de transmitir automaticamente a altura da vegetacao.
 */
public class TrechoMonitoradoIoT extends TrechoRodovia implements MonitoravelViaIoT {
    private final String codigoSensor;

    public TrechoMonitoradoIoT(
            int quilometro,
            String sentido,
            double alturaVegetacaoCm,
            TipoCrescimento tipoCrescimento,
            boolean areaSensivel,
            String codigoSensor
    ) {
        super(quilometro, sentido, alturaVegetacaoCm, tipoCrescimento, areaSensivel);
        this.codigoSensor = codigoSensor;
    }

    public String getCodigoSensor() {
        return codigoSensor;
    }

    @Override
    public double transmitirDadosSensor() {
        return getAlturaVegetacaoCm();
    }
}
