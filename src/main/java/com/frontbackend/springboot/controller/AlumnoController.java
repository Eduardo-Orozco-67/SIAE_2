package com.frontbackend.springboot.controller;


import com.frontbackend.springboot.entity.AlumnoEntity;
import com.frontbackend.springboot.service.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
@RequestMapping("files")
public class AlumnoController {

        @Autowired
        private AlumnoService alumnoService;

    @GetMapping("/ver")
    public List<AlumnoEntity> obtenerTodosLosAlumnos() {
        return alumnoService.obtenerTodosLosAlumnos();
    }

    @GetMapping("/ver/{matricula}")
    public ResponseEntity<AlumnoEntity> obtenerAlumnoPorMatricula(@PathVariable("matricula") String mat) {
        return alumnoService.obtenerAlumnoPorMatricula(mat);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Map<String, String>> agregarAlumno(@RequestBody AlumnoEntity alumno) {
        return alumnoService.agregarAlumno(alumno);
    }

    @PutMapping("/actualizar/{matricula}")
    public ResponseEntity<Map<String, String>> actualizarAlumno(@RequestBody AlumnoEntity alumno, @PathVariable("matricula") String idAlumno) {
        return alumnoService.actualizarAlumno(alumno, idAlumno);
    }

    @DeleteMapping("/eliminar/{matricula}")
    public ResponseEntity<Map<String, String>> eliminarAlumno(@PathVariable("matricula") String idAlumno) {
        return alumnoService.eliminarAlumno(idAlumno);
    }
}

