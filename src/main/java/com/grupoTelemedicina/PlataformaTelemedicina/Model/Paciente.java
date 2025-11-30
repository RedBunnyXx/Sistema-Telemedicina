package com.grupoTelemedicina.PlataformaTelemedicina.Model;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "Pacientes")
public class Paciente {

    @Id
    @Column(name = "id_paciente")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_paciente")
    private Persona persona;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 1)
    private String sexo;

    public Paciente() {}

    public Paciente(Persona persona, LocalDate fechaNacimiento, String sexo) {
        this.persona = persona;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
    }

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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
}

