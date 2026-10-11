package br.com.motiva.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TB_RELATORIO_PRIORIDADE")
public class RelatorioEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "ID_RELATORIO") private Long id;
    @Column(name = "DATA_GERACAO", nullable = false) private LocalDateTime dataGeracao;
    @Column(name = "QT_NORMAL", nullable = false, precision = 10, scale = 0) private Integer qtNormal;
    @Column(name = "QT_BAIXA", nullable = false, precision = 10, scale = 0) private Integer qtBaixa;
    @Column(name = "QT_MEDIA", nullable = false, precision = 10, scale = 0) private Integer qtMedia;
    @Column(name = "QT_ALTA", nullable = false, precision = 10, scale = 0) private Integer qtAlta;
    @Column(name = "QT_CRITICA", nullable = false, precision = 10, scale = 0) private Integer qtCritica;
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
