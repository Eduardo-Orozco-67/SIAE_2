package com.frontbackend.springboot.entity;

import javax.persistence.*;

@Entity
@Table(name = "COMPROBANTES")
public class ComprobanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String contentType;

    private Long size;

    @Lob
    private byte[] data;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn (name = "Id_alumno", referencedColumnName = "Id")
    private AlumnoEntity alumno;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public AlumnoEntity getAlumno() {
        return alumno;
    }

    public void setAlumno(AlumnoEntity alumno) {
        this.alumno = alumno;
    }
}
