package br.com.motiva.model;

import jakarta.persistence.*;

@Entity @Table(name = "TB_EQUIPE_MANUTENCAO")
public class EquipeManutencao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "ID_EQUIPE")
    private Long id;
    @Column(name = "NOME", nullable = false, length = 100) private String nome;
    @Column(name = "ESPECIALIDADE", nullable = false, length = 100) private String especialidade;
    @Convert(converter = FlagConverter.class) @Column(name = "ATIVA", nullable = false, length = 1) private Boolean ativa;
    protected EquipeManutencao() {}
    public EquipeManutencao(String nome, String especialidade, boolean ativa) { this.nome = nome; this.especialidade = especialidade; this.ativa = ativa; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
    public boolean isAtiva() { return Boolean.TRUE.equals(ativa); }
    public void atualizar(String nome, String especialidade, boolean ativa) { this.nome = nome; this.especialidade = especialidade; this.ativa = ativa; }
}
