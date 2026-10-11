package br.com.motiva.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "TB_INTERVENCAO_OPERACIONAL")
public class IntervencaoEntity {
    @Id @JdbcTypeCode(SqlTypes.NUMERIC) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "intervencao_seq")
    @SequenceGenerator(name = "intervencao_seq", sequenceName = "SEQ_INTERVENCAO_OPERACIONAL", allocationSize = 1)
    @Column(name = "ID_INTERVENCAO") private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "ID_TRECHO") private TrechoEntity trecho;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "ID_EQUIPE") private EquipeManutencao equipe;
    @Enumerated(EnumType.STRING) @Column(name = "TIPO_INTERVENCAO", nullable = false, length = 30) private TipoIntervencao tipoIntervencao;
    @Enumerated(EnumType.STRING) @Column(name = "NIVEL_PRIORIDADE", nullable = false, length = 10) private NivelPrioridade nivelPrioridade;
    @Column(name = "ALTURA_PROJETADA_CM", nullable = false, precision = 8, scale = 2) private BigDecimal alturaProjetadaCm;
    @Column(name = "JUSTIFICATIVA", nullable = false, length = 1000) private String justificativa;
    @Column(name = "DATA_INTERVENCAO", nullable = false) private LocalDateTime dataIntervencao;
    @Column(name = "STATUS", nullable = false, length = 20) private String status;
    protected IntervencaoEntity() {}
    public IntervencaoEntity(TrechoEntity trecho, EquipeManutencao equipe, TipoIntervencao tipo, NivelPrioridade nivel, double altura, String justificativa, LocalDateTime data, String status) { atualizar(trecho,equipe,tipo,nivel,altura,justificativa,data,status); }
    public void atualizar(TrechoEntity trecho, EquipeManutencao equipe, TipoIntervencao tipo, NivelPrioridade nivel, double altura, String justificativa, LocalDateTime data, String status) { this.trecho=trecho; this.equipe=equipe; this.tipoIntervencao=tipo; this.nivelPrioridade=nivel; this.alturaProjetadaCm=BigDecimal.valueOf(altura); this.justificativa=justificativa; this.dataIntervencao=data; this.status=status; }
    public Long getId() { return id; }
    public TrechoEntity getTrecho() { return trecho; }
    public EquipeManutencao getEquipe() { return equipe; }
    public TipoIntervencao getTipoIntervencao() { return tipoIntervencao; }
    public NivelPrioridade getNivelPrioridade() { return nivelPrioridade; }
    public double getAlturaProjetadaCm() { return alturaProjetadaCm.doubleValue(); }
    public String getJustificativa() { return justificativa; }
    public LocalDateTime getDataIntervencao() { return dataIntervencao; }
    public String getStatus() { return status; }
}
