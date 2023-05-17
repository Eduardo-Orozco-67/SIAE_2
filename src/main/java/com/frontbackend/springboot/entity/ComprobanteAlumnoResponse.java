package com.frontbackend.springboot.entity;

public class ComprobanteAlumnoResponse {

    private ComprobanteResponse comprobante;
    private AlumnoResponse alumno;

    public ComprobanteResponse getComprobante() {
        return comprobante;
    }

    public void setComprobante(ComprobanteResponse comprobante) {
        this.comprobante = comprobante;
    }

    public AlumnoResponse getAlumno() {
        return alumno;
    }

    public void setAlumno(AlumnoResponse alumno) {
        this.alumno = alumno;
    }
}
