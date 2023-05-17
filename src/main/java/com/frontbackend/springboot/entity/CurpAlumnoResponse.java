package com.frontbackend.springboot.entity;

public class CurpAlumnoResponse {
    private CurpResponse curp;
    private AlumnoResponse alumno;

    public CurpResponse getCurp() {
        return curp;
    }

    public void setCurp(CurpResponse curp) {
        this.curp = curp;
    }

    public AlumnoResponse getAlumno() {
        return alumno;
    }

    public void setAlumno(AlumnoResponse alumno) {
        this.alumno = alumno;
    }

}
