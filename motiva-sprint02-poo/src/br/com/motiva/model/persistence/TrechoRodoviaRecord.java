package br.com.motiva.model.persistence;

import br.com.motiva.model.TipoCrescimento;
import br.com.motiva.model.TrechoMonitoradoIoT;
import br.com.motiva.model.TrechoRodovia;

import java.util.Objects;

public record TrechoRodoviaRecord(
        Long id,
        int quilometro,
        String sentido,
        double alturaVegetacaoCm,
        TipoCrescimento tipoCrescimento,
        boolean areaSensivel,
        boolean monitoradoIot,
        String codigoSensor
) {
    public TrechoRodoviaRecord {
        validarId(id);
        if (quilometro < 0) {
            throw new IllegalArgumentException("O quilometro nao pode ser negativo.");
        }
        sentido = validarTextoObrigatorio(sentido, "O sentido do trecho e obrigatorio.");
        validarNumeroNaoNegativo(
                alturaVegetacaoCm,
                "A altura da vegetacao deve ser um numero finito e nao negativo."
        );
        tipoCrescimento = Objects.requireNonNull(
                tipoCrescimento,
                "O tipo de crescimento e obrigatorio."
        );

        if (monitoradoIot) {
            codigoSensor = validarTextoObrigatorio(
                    codigoSensor,
                    "O codigo do sensor e obrigatorio para trechos monitorados via IoT."
            );
        } else if (codigoSensor != null) {
            throw new IllegalArgumentException(
                    "Um trecho sem monitoramento IoT nao pode possuir codigo de sensor."
            );
        }
    }

    public TrechoRodovia toDomain() {
        if (monitoradoIot) {
            return new TrechoMonitoradoIoT(
                    quilometro,
                    sentido,
                    alturaVegetacaoCm,
                    tipoCrescimento,
                    areaSensivel,
                    codigoSensor
            );
        }

        return new TrechoRodovia(
                quilometro,
                sentido,
                alturaVegetacaoCm,
                tipoCrescimento,
                areaSensivel
        );
    }

    public static TrechoRodoviaRecord fromDomain(TrechoRodovia trecho) {
        return fromDomain(null, trecho);
    }

    public static TrechoRodoviaRecord fromDomain(Long id, TrechoRodovia trecho) {
        Objects.requireNonNull(trecho, "O trecho de dominio e obrigatorio.");

        boolean monitoradoIot = trecho instanceof TrechoMonitoradoIoT;
        String codigoSensor = monitoradoIot
                ? ((TrechoMonitoradoIoT) trecho).getCodigoSensor()
                : null;

        return new TrechoRodoviaRecord(
                id,
                trecho.getQuilometro(),
                trecho.getSentido(),
                trecho.getAlturaVegetacaoCm(),
                trecho.getTipoCrescimento(),
                trecho.isAreaSensivel(),
                monitoradoIot,
                codigoSensor
        );
    }

    private static void validarId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("O ID do trecho deve ser positivo.");
        }
    }

    private static void validarNumeroNaoNegativo(double valor, String mensagem) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private static String validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor.trim();
    }
}
