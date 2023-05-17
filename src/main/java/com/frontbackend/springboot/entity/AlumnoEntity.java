package com.frontbackend.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Date;


@Entity
@Table(name = "tb_alumno")
public class AlumnoEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="Id")
        private int id_alumno;

        @Column(name="Matricula")
        private String matricula;

        @Column(name="Nombres")
        private String nombre_u;

        @Column(name="Apellido_Paterno")
        private String apellidos_p;

        @Column(name="Apellido_Materno")
        private String apellidos_m;

        @Column(name="Email")
        private String email;

        @Column(name="Fecha_nac")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        private Date fecha_nac;

    public AlumnoEntity() {
        super();
    }

    public AlumnoEntity(int id_alumno, String matricula, String nombre_u, String apellidos_p, String apellidos_m, String email, Date fecha_nac) {
        this.id_alumno = id_alumno;
        this.matricula = matricula;
        this.nombre_u = nombre_u;
        this.apellidos_p = apellidos_p;
        this.apellidos_m = apellidos_m;
        this.email = email;
        this.fecha_nac = fecha_nac;
    }

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