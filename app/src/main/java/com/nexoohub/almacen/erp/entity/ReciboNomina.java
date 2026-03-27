package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nexoohub.almacen.empleados.entity.Empleado;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recibo_nomina")
public class ReciboNomina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false)
    private NominaPeriodo periodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "dias_trabajados", precision = 4, scale = 2, nullable = false)
    private BigDecimal diasTrabajados = new BigDecimal("15.00");

    @Column(name = "total_percepciones", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPercepciones = BigDecimal.ZERO;

    @Column(name = "total_deducciones", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalDeducciones = BigDecimal.ZERO;

    @Column(name = "neto_pagar", precision = 12, scale = 2, nullable = false)
    private BigDecimal netoPagar = BigDecimal.ZERO;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago = "TRANSFERENCIA";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReciboNominaDetalle> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void addDetalle(ReciboNominaDetalle det) {
        detalles.add(det);
        det.setRecibo(this);
        recalcularTotales();
    }

    public void recalcularTotales() {
        this.totalPercepciones = detalles.stream()
                .filter(d -> "PERCEPCION".equals(d.getTipoConcepto()))
                .map(ReciboNominaDetalle::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalDeducciones = detalles.stream()
                .filter(d -> "DEDUCCION".equals(d.getTipoConcepto()))
                .map(ReciboNominaDetalle::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.netoPagar = this.totalPercepciones.subtract(this.totalDeducciones);
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public NominaPeriodo getPeriodo() { return periodo; }
    public void setPeriodo(NominaPeriodo periodo) { this.periodo = periodo; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public BigDecimal getDiasTrabajados() { return diasTrabajados; }
    public void setDiasTrabajados(BigDecimal diasTrabajados) { this.diasTrabajados = diasTrabajados; }
    public BigDecimal getTotalPercepciones() { return totalPercepciones; }
    public void setTotalPercepciones(BigDecimal totalPercepciones) { this.totalPercepciones = totalPercepciones; }
    public BigDecimal getTotalDeducciones() { return totalDeducciones; }
    public void setTotalDeducciones(BigDecimal totalDeducciones) { this.totalDeducciones = totalDeducciones; }
    public BigDecimal getNetoPagar() { return netoPagar; }
    public void setNetoPagar(BigDecimal netoPagar) { this.netoPagar = netoPagar; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<ReciboNominaDetalle> getDetalles() { return detalles; }
}
