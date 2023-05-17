package com.frontbackend.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


public class AlumnoResponse {

    private int id_alumno;

    private String matricula;

    private String nombre_u;

    private String apellidos_p;

    private String apellidos_m;

    private String email;

    private Date fecha_nac;

    public int getId_alumno() {
        return id_alumno;
    }

    public void setId_alumno(int id_alumno) {
        this.id_alumno = id_alumno;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre_u() {
        return nombre_u;
    }

    public void setNombre_u(String nombre_u) {
        this.nombre_u = nombre_u;
    }

    public String getApellidos_p() {
        return apellidos_p;
    }

    public void setApellidos_p(String apellidos_p) {
        this.apellidos_p = apellidos_p;
    }

    public String getApellidos_m() {
        return apellidos_m;
    }

    public void setApellidos_m(String apellidos_m) {
        this.apellidos_m = apellidos_m;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFecha_nac() {
        return fecha_nac;
    }

    public void setFecha_nac(Date fecha_nac) {
        this.fecha_nac = fecha_nac;
    }
}
