package com.frontbackend.springboot.service;

import com.frontbackend.springboot.entity.AlumnoEntity;
import com.frontbackend.springboot.exception.ResourceNotFoundException;
import com.frontbackend.springboot.repository.AlumnoRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    public List<AlumnoEntity> obtenerTodosLosAlumnos() {
        List<AlumnoEntity> alumnos = alumnoRepository.findAll();
        if (alumnos.isEmpty()) {
            throw new ResourceNotFoundException("No hay alumnos registrados en la BD");
        }
        return alumnos;
    }

    public ResponseEntity<AlumnoEntity> obtenerAlumnoPorId(int idAlumno) {
        AlumnoEntity alumno = alumnoRepository.findById(idAlumno).orElseThrow(() ->
                new ResourceNotFoundException("No existe un alumno con el ID: " + idAlumno));
        return ResponseEntity.ok(alumno);
    }

    public ResponseEntity<AlumnoEntity> obtenerAlumnoPorMatricula(String mat) {
        AlumnoEntity alumno = alumnoRepository.findByMatriculaAlumno(mat).orElseThrow(() ->
                new ResourceNotFoundException("No existe un alumno con la matricula: " + mat));
        return ResponseEntity.ok(alumno);
    }

    public ResponseEntity<Map<String, String>> agregarAlumno(AlumnoEntity alumno) {
        // Generar el número aleatorio de cinco dígitos
        Random random = new Random();
        int numeroAleatorio = random.nextInt(900000) + 100000; // Genera un número entre 10000 y 99999

        // Generar la matrícula utilizando la letra "B" seguida del número aleatorio
        String matricula = "B" + numeroAleatorio;
        alumno.setMatricula(matricula);

        // Generar el número aleatorio de dos dígitos para el email
        int numeroAleatorioEmail = random.nextInt(90) + 10; // Genera un número entre 10 y 99

        // Obtener el nombre para generar el email
        String nombre = alumno.getNombre_u().toLowerCase();
        String apellido = alumno.getApellidos_p().toLowerCase();

        // Verificar si el alumno tiene dos nombres
        String[] nombres = nombre.split(" ");
        if (nombres.length > 1) {
            // Utilizar solo el primer nombre
            nombre = nombres[0];
        }

        // Generar el email utilizando el nombre, apellido y el número aleatorio para el email
        String email = nombre + "." + apellido + numeroAleatorioEmail + "@unach.mx";
        alumno.setEmail(email);

        // Guardar el alumno en el repositorio
        alumnoRepository.save(alumno);

        // Preparar la respuesta
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("message", "El alumno se ha registrado correctamente");
        respuesta.put("status", HttpStatus.CREATED.toString());
        respuesta.put("matricula", matricula);
        respuesta.put("email", email);

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    public ResponseEntity<Map<String, String>> actualizarAlumno(AlumnoEntity alumno, String idAlumno) {
        AlumnoEntity alumnoActualizado = alumnoRepository.findByMatriculaAlumno(idAlumno).map(a -> {
            a.setMatricula(alumno.getMatricula());
            a.setNombre_u(alumno.getNombre_u());
            a.setApellidos_p(alumno.getApellidos_p());
            a.setApellidos_m(alumno.getApellidos_m());
            a.setEmail(alumno.getEmail());
            a.setFecha_nac(alumno.getFecha_nac());
            return alumnoRepository.save(a);
        }).orElseThrow(() -> new ResourceNotFoundException("No existe un alumno con el ID: " + idAlumno));

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("message", "El alumno se ha actualizado correctamente");
        respuesta.put("status", HttpStatus.OK.toString());
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> eliminarAlumno(String idAlumno) {
        Optional<AlumnoEntity> alumnoOptional = alumnoRepository.findByMatriculaAlumno(idAlumno);
        AlumnoEntity alumno = alumnoOptional.orElseThrow(() -> new ResourceNotFoundException("No existe un alumno con la matrícula: " + idAlumno));

        alumnoRepository.delete(alumno);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("message", "El alumno se ha eliminado correctamente");
        respuesta.put("status", HttpStatus.OK.toString());
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
