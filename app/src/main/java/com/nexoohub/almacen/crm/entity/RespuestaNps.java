package com.nexoohub.almacen.crm.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "respuesta_nps")
public class RespuestaNps extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "encuesta_id", insertable = false, updatable = false)
    private Integer encuestaId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encuesta_id")
    private EncuestaNps encuesta;

    private Integer score;

    private String clasificacion;

    private String comentarios;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getEncuestaId() { return encuestaId; }
    public void setEncuestaId(Integer encuestaId) { this.encuestaId = encuestaId; }
    
    public EncuestaNps getEncuesta() { return encuesta; }
    public void setEncuesta(EncuestaNps encuesta) { this.encuesta = encuesta; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    
    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }
    
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}
