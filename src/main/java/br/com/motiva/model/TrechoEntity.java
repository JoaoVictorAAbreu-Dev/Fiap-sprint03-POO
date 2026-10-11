package br.com.motiva.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;

@Entity @Table(name = "TB_TRECHO_RODOVIA")
public class TrechoEntity {
    @Id @JdbcTypeCode(SqlTypes.NUMERIC) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trecho_seq")
    @SequenceGenerator(name = "trecho_seq", sequenceName = "SEQ_TRECHO_RODOVIA", allocationSize = 1)
    @Column(name = "ID_TRECHO") private Long id;
    @JdbcTypeCode(SqlTypes.NUMERIC) @Column(name = "QUILOMETRO", nullable = false, precision = 6, scale = 0) private Integer quilometro;
    @Column(name = "SENTIDO", nullable = false, length = 20) private String sentido;
    @Column(name = "ALTURA_VEGETACAO_CM", nullable = false, precision = 8, scale = 2) private BigDecimal alturaVegetacaoCm;
    @Enumerated(EnumType.STRING) @Column(name = "TIPO_CRESCIMENTO", nullable = false, length = 10) private TipoCrescimento tipoCrescimento;
    @Convert(converter = FlagConverter.class) @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "AREA_SENSIVEL", nullable = false, length = 1) private Boolean areaSensivel;
    @Convert(converter = FlagConverter.class) @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "MONITORADO_IOT", nullable = false, length = 1) private Boolean monitoradoIot;
    @Column(name = "CODIGO_SENSOR", length = 50) private String codigoSensor;
    protected TrechoEntity() {}
    public TrechoEntity(int quilometro, String sentido, double alturaVegetacaoCm, TipoCrescimento tipoCrescimento, boolean areaSensivel, boolean monitoradoIot, String codigoSensor) { atualizar(quilometro, sentido, alturaVegetacaoCm, tipoCrescimento, areaSensivel, monitoradoIot, codigoSensor); }
    public void atualizar(int quilometro, String sentido, double alturaVegetacaoCm, TipoCrescimento tipoCrescimento, boolean areaSensivel, boolean monitoradoIot, String codigoSensor) { this.quilometro = quilometro; this.sentido = sentido; this.alturaVegetacaoCm = BigDecimal.valueOf(alturaVegetacaoCm); this.tipoCrescimento = tipoCrescimento; this.areaSensivel = areaSensivel; this.monitoradoIot = monitoradoIot; this.codigoSensor = codigoSensor; }
    public Long getId() { return id; }
    public int getQuilometro() { return quilometro; }
    public String getSentido() { return sentido; }
    public double getAlturaVegetacaoCm() { return alturaVegetacaoCm.doubleValue(); }
    public TipoCrescimento getTipoCrescimento() { return tipoCrescimento; }
    public boolean isAreaSensivel() { return Boolean.TRUE.equals(areaSensivel); }
    public boolean isMonitoradoIot() { return Boolean.TRUE.equals(monitoradoIot); }
    public String getCodigoSensor() { return codigoSensor; }
    public TrechoRodovia toDomain() { return isMonitoradoIot() ? new TrechoMonitoradoIoT(quilometro, sentido, getAlturaVegetacaoCm(), tipoCrescimento, isAreaSensivel(), codigoSensor) : new TrechoRodovia(quilometro, sentido, getAlturaVegetacaoCm(), tipoCrescimento, isAreaSensivel()); }
}
