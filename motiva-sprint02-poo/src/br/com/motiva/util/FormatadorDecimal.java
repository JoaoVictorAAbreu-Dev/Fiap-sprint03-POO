package br.com.motiva.util;

import java.text.DecimalFormat;

public final class FormatadorDecimal {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private FormatadorDecimal() {
    }

    public static String formatar(double valor) {
        return DECIMAL_FORMAT.format(valor);
    }
}
