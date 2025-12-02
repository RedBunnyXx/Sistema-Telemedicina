package com.grupoTelemedicina.PlataformaTelemedicina.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Especialidades")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String nombre;
    
    @Column(name = "costo",nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    
}
