package br.com.motiva.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity @Table(name = "TB_RELATORIO_PRIORIDADE")
public class RelatorioEntity {
    @Id @JdbcTypeCode(SqlTypes.NUMERIC) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relatorio_seq")
    @SequenceGenerator(name = "relatorio_seq", sequenceName = "SEQ_RELATORIO_PRIORIDADE", allocationSize = 1)
    @Column(name = "ID_RELATORIO") private Long id;
    @Column(name = "DATA_GERACAO", nullable = false) private LocalDateTime dataGeracao;
    @JdbcTypeCode(SqlTypes.NUMERIC) @Column(name = "QT_NORMAL", nullable = false, precision = 10, scale = 0) private Integer qtNormal;
    @JdbcTypeCode(SqlTypes.NUMERIC) @Column(name = "QT_BAIXA", nullable = false, precision = 10, scale = 0) private Integer qtBaixa;
    @JdbcTypeCode(SqlTypes.NUMERIC) @Column(name = "QT_MEDIA", nullable = false, precision = 10, scale = 0) private Integer qtMedia;
    @JdbcTypeCode(SqlTypes.NUMERIC) @Column(name = "QT_ALTA", nullable = false, precision = 10, scale = 0) private Integer qtAlta;
    @JdbcTypeCode(SqlTypes.NUMERIC) @Column(name = "QT_CRITICA", nullable = false, precision = 10, scale = 0) private Integer qtCritica;
    @Column(name = "RESUMO", nullable = false, length = 2000) private String resumo;
    protected RelatorioEntity() {}
    public RelatorioEntity(LocalDateTime data, int normal, int baixa, int media, int alta, int critica, String resumo) { this.dataGeracao=data; this.qtNormal=normal; this.qtBaixa=baixa; this.qtMedia=media; this.qtAlta=alta; this.qtCritica=critica; this.resumo=resumo; }
    public Long getId() { return id; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public int getQtNormal() { return qtNormal; }
    public int getQtBaixa() { return qtBaixa; }
    public int getQtMedia() { return qtMedia; }
    public int getQtAlta() { return qtAlta; }
    public int getQtCritica() { return qtCritica; }
    public String getResumo() { return resumo; }
}
