package br.com.motiva.model;

/**
 * Tipos de intervencao possiveis no processo de conservacao da vegetacao.
 */
public enum TipoIntervencao {
    SEM_INTERVENCAO("Sem intervencao imediata"),
    ROCADA_MANUAL("Rocada manual"),
    ROCADA_MECANIZADA("Rocada mecanizada"),
    PULVERIZACAO("Pulverizacao preventiva");

    private final String descricao;

    TipoIntervencao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
