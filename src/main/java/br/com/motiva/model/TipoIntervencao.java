package br.com.motiva.model;

/**
 * Tipos de intervencao possiveis no processo de conservacao da vegetacao.
 */
public enum TipoIntervencao {
    SEM_INTERVENCAO("Sem intervencao imediata", null),
    ROCADA_MANUAL("Rocada manual", "ROCADA MANUAL"),
    ROCADA_MECANIZADA("Rocada mecanizada", "ROCADA MECANIZADA"),
    PULVERIZACAO("Pulverizacao preventiva", "PULVERIZACAO PREVENTIVA");

    private final String descricao;
    private final String especialidadeEquipe;

    TipoIntervencao(String descricao, String especialidadeEquipe) {
        this.descricao = descricao;
        this.especialidadeEquipe = especialidadeEquipe;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Valor de ESPECIALIDADE exigido para executar este tipo de intervencao. */
    public String getEspecialidadeEquipe() {
        return especialidadeEquipe;
    }
}
