package com.grupoTelemedicina.PlataformaTelemedicina.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "Medicos")
public class Medico {

    @Id
    @Column(name = "id_medico")
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_medico")
    private Persona persona;

    @Column(nullable = false, length = 15)
    private String cmp;

    @ManyToOne
    @JoinColumn(name = "id_especialidad", nullable = false)
    private Especialidad especialidad;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getCmp() {
        return cmp;
    }

    public void setCmp(String cmp) {
        this.cmp = cmp;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
}
