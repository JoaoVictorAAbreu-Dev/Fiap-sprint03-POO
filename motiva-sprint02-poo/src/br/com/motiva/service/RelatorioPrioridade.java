package br.com.motiva.service;

import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.util.FormatadorDecimal;

public class RelatorioPrioridade {

    public void imprimir(ResultadoPrioridade[] resultados) {
        imprimirCabecalho();

        for (ResultadoPrioridade resultado : resultados) {
            imprimirResultado(resultado);
        }

        imprimirResumo(resultados);
    }

    private void imprimirCabecalho() {
        System.out.println("============================================================");
        System.out.println("        RELATORIO DE PRIORIDADE OPERACIONAL - MOTIVA        ");
        System.out.println("============================================================");
    }

    private void imprimirResultado(ResultadoPrioridade resultado) {
        System.out.println();
        System.out.println("KM: " + resultado.getTrecho().getQuilometro());
        System.out.println("Sentido: " + resultado.getTrecho().getSentido());
        System.out.println("Altura atual: " + FormatadorDecimal.formatar(resultado.getTrecho().getAlturaVegetacaoCm()) + " cm");
        System.out.println("Altura projetada: " + FormatadorDecimal.formatar(resultado.getAlturaProjetadaCm()) + " cm");
        System.out.println("Crescimento: " + resultado.getTrecho().getTipoCrescimento().getDescricao());
        System.out.println("Area sensivel: " + (resultado.getTrecho().isAreaSensivel() ? "Sim" : "Nao"));
        System.out.println("Prioridade: " + resultado.getNivelPrioridade());
        System.out.println("Intervencao indicada: " + resultado.getTipoIntervencao().getDescricao());
        System.out.println("Justificativa: " + resultado.getJustificativa());
    }

    private void imprimirResumo(ResultadoPrioridade[] resultados) {
        int semIntervencao = 0;
        int manual = 0;
        int mecanizada = 0;
        int pulverizacao = 0;

        for (ResultadoPrioridade resultado : resultados) {
            if (resultado.getTipoIntervencao() == TipoIntervencao.SEM_INTERVENCAO) {
                semIntervencao++;
            } else if (resultado.getTipoIntervencao() == TipoIntervencao.ROCADA_MANUAL) {
                manual++;
            } else if (resultado.getTipoIntervencao() == TipoIntervencao.ROCADA_MECANIZADA) {
                mecanizada++;
            } else if (resultado.getTipoIntervencao() == TipoIntervencao.PULVERIZACAO) {
                pulverizacao++;
            }
        }

        System.out.println();
        System.out.println("============================================================");
        System.out.println("RESUMO OPERACIONAL");
        System.out.println("Sem intervencao: " + semIntervencao);
        System.out.println("Pulverizacao preventiva: " + pulverizacao);
        System.out.println("Rocada manual: " + manual);
        System.out.println("Rocada mecanizada: " + mecanizada);
        System.out.println("============================================================");
    }
}
