package com.grupoTelemedicina.PlataformaTelemedicina.dto;

import java.time.LocalDate;

public class PacienteDTO {

    private Integer id_paciente;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;
    private String telefono;
    private String password;

    private LocalDate fechaNacimiento;
    private String sexo;


    public PacienteDTO() {
    }

    public PacienteDTO(Integer id_paciente, String nombres, String apellidos, String dni, String correo, String telefono, String password, LocalDate fechaNacimiento, String sexo) {
        this.id_paciente = id_paciente;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.correo = correo;
        this.telefono = telefono;
        this.password = password;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
    }

    public Integer getId_paciente() {return id_paciente;}

    public void setId_paciente(Integer id_paciente) {
        this.id_paciente = id_paciente;
    }

    public String getNombres() {return nombres;}

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

