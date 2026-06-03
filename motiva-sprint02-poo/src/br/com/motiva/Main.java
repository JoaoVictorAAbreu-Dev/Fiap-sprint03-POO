package br.com.motiva;

import br.com.motiva.intervencao.IntervencaoOperacional;
import br.com.motiva.intervencao.Pulverizacao;
import br.com.motiva.intervencao.RocadaManual;
import br.com.motiva.intervencao.RocadaMecanizada;
import br.com.motiva.model.TipoCrescimento;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.TrechoMonitoradoIoT;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.service.MotorRegrasPrioridade;
import br.com.motiva.service.RelatorioPrioridade;
import br.com.motiva.service.ResultadoPrioridade;

public class Main {

    public static void main(String[] args) {
        TrechoRodovia[] trechos = criarTrechosSimulados();

        MotorRegrasPrioridade motor = new MotorRegrasPrioridade();
        ResultadoPrioridade[] resultados = motor.gerarRelatorioPrioridade(trechos);

        RelatorioPrioridade relatorio = new RelatorioPrioridade();
        relatorio.imprimir(resultados);

        executarIntervencoesIndicadas(resultados);
    }

    private static TrechoRodovia[] criarTrechosSimulados() {
        return new TrechoRodovia[] {
                new TrechoRodovia(10, "Norte", 28.0, TipoCrescimento.SECO, false),
                new TrechoRodovia(11, "Norte", 42.0, TipoCrescimento.NORMAL, false),
                new TrechoRodovia(12, "Sul", 58.0, TipoCrescimento.NORMAL, true),
                new TrechoRodovia(13, "Sul", 68.0, TipoCrescimento.UMIDO, false),
                new TrechoMonitoradoIoT(14, "Leste", 75.0, TipoCrescimento.UMIDO, true, "IOT-014"),
                new TrechoMonitoradoIoT(15, "Oeste", 44.0, TipoCrescimento.UMIDO, false, "IOT-015")
        };
    }

    private static void executarIntervencoesIndicadas(ResultadoPrioridade[] resultados) {
        System.out.println();
        System.out.println("EXECUCAO SIMULADA DAS INTERVENCOES");
        System.out.println("------------------------------------------------------------");

        for (ResultadoPrioridade resultado : resultados) {
            IntervencaoOperacional intervencao = criarIntervencao(resultado.getTipoIntervencao());

            if (intervencao == null) {
                System.out.println("KM " + resultado.getTrecho().getQuilometro() + ": nenhuma equipe acionada.");
            } else {
                System.out.println(intervencao.executarServico(resultado.getTrecho()));
            }
        }
    }

    private static IntervencaoOperacional criarIntervencao(TipoIntervencao tipoIntervencao) {
        return switch (tipoIntervencao) {
            case ROCADA_MECANIZADA -> new RocadaMecanizada("Equipe Alpha");
            case ROCADA_MANUAL -> new RocadaManual("Equipe Beta");
            case PULVERIZACAO -> new Pulverizacao("Equipe Preventiva");
            case SEM_INTERVENCAO -> null;
        };
    }
}
