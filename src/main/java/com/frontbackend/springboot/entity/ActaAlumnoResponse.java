package com.frontbackend.springboot.entity;

import java.util.Date;

public class ActaAlumnoResponse {
    private ActaResponse acta;
    private AlumnoResponse alumno;

    public ActaResponse getActa() {
        return acta;
    }

    public void setActa(ActaResponse acta) {
        this.acta = acta;
    }

    public AlumnoResponse getAlumno() {
        return alumno;
    }

    public void setAlumno(AlumnoResponse alumno) {
        this.alumno = alumno;
    }

}
