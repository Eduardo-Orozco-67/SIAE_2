package com.frontbackend.springboot.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.frontbackend.springboot.entity.*;
import com.frontbackend.springboot.exception.ResourceNotFoundException;
import com.frontbackend.springboot.repository.ActaRepository;
import com.frontbackend.springboot.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.frontbackend.springboot.service.ActaService;

@RestController
@RequestMapping("files")
public class ActaController {

    private final ActaService actaService;

    @Autowired
    public ActaController(ActaService actaService) {
        this.actaService = actaService;
    }

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private ActaRepository actaRepository;


    @PostMapping("/actas/{matricula}/guardar")
    public ResponseEntity<String> guardarActa(@RequestParam("file") MultipartFile file,
                                              @PathVariable(value = "matricula") String matAlumno) {

        // Validar si el alumno existe
        AlumnoEntity alumno = alumnoRepository.findByMatriculaAlumno(matAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un alumno con la matrícula: " + matAlumno));

        // Validar si ya existe un registro con el mismo id_Alumno en la tabla
        Optional<ActaEntity> existingActa = actaService.findAlumnoId(alumno.getId_alumno());
        if (existingActa.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un registro de acta para el alumno con la matrícula: " + matAlumno);
        }

        try {
            // Guardar el archivo
            actaService.save(file, alumno);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File uploaded successfully: %s", file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not upload the file: %s!", file.getOriginalFilename()));
        }
    }


    @GetMapping("/actas/ver_todas")
    public List<ActaResponse> list() {
        return actaService.getAllFiles()
                .stream()
                .map(actaEntity -> {
                    ActaResponse actaResponse = mapToFileResponse(actaEntity);
                    actaResponse.setAlumno(actaEntity.getAlumno());
                    return actaResponse;
                })
                .collect(Collectors.toList());
    }

    private ActaResponse mapToFileResponse(ActaEntity actaEntity) {
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                        .path("/files/")
                                                        .path(String.valueOf(actaEntity.getId()))
                                                        .toUriString();
        ActaResponse actaResponse = new ActaResponse();
        actaResponse.setId(String.valueOf(actaEntity.getId()));
        actaResponse.setName(actaEntity.getName());
        actaResponse.setContentType(actaEntity.getContentType());
        actaResponse.setSize(actaEntity.getSize());
        actaResponse.setUrl(downloadURL);

        return actaResponse;
    }

    @GetMapping("/actas/ver_id/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Integer id) {
        Optional<ActaEntity> fileEntityOptional = actaService.getFile(id);

        if (!fileEntityOptional.isPresent()) {
            return ResponseEntity.notFound()
                                 .build();
        }

        ActaEntity actaEntity = fileEntityOptional.get();
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + actaEntity.getName() + "\"")
                             .contentType(MediaType.valueOf(actaEntity.getContentType()))
                             .body(actaEntity.getData());
    }

    private AlumnoResponse mapToAlumnoResponse(AlumnoEntity alumnoEntity) {
        AlumnoResponse alumnoResponse = new AlumnoResponse();
        alumnoResponse.setId_alumno(alumnoEntity.getId_alumno());
        alumnoResponse.setApellidos_m(alumnoEntity.getApellidos_m());
        alumnoResponse.setApellidos_p(alumnoEntity.getApellidos_p());
        alumnoResponse.setEmail(alumnoEntity.getEmail());
        alumnoResponse.setFecha_nac(alumnoEntity.getFecha_nac());
        alumnoResponse.setMatricula(alumnoEntity.getMatricula());
        alumnoResponse.setNombre_u(alumnoEntity.getNombre_u());
        return alumnoResponse;
    }

    @GetMapping("/actas/ver_id_alumno/{matricula}")
    public ResponseEntity<ActaAlumnoResponse> getFileByAlumnoId(@PathVariable(value = "matricula") String matAlum) {
        Optional<ActaEntity> actaEntityOptional = actaService.findByAlumnoId(matAlum);

        if (!actaEntityOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ActaEntity actaEntity = actaEntityOptional.get();
        AlumnoEntity alumnoEntity = actaEntity.getAlumno();

        // Crear una respuesta combinada que contenga tanto los datos del archivo como los datos del alumno
        ActaAlumnoResponse response = new ActaAlumnoResponse();
        response.setActa(mapToFileResponse(actaEntity));
        response.setAlumno(mapToAlumnoResponse(alumnoEntity));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/actas/{matricula}/editar")
    public ResponseEntity<String> editarActa(@RequestParam("file") MultipartFile file,
                                             @PathVariable(value = "matricula")  String idAlumno) {

        // Validar si el alumno existe
        AlumnoEntity alumno = alumnoRepository.findByMatriculaAlumno(idAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un alumno con el ID: " + idAlumno));

        // Buscar el archivo existente del alumno
        Optional<ActaEntity> actaEntityOptional = actaService.findByAlumnoId(idAlumno);
        if (!actaEntityOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ActaEntity actaEntity = actaEntityOptional.get();

        try {
            // Actualizar los datos del archivo
            actaEntity.setName(StringUtils.cleanPath(file.getOriginalFilename()));
            actaEntity.setContentType(file.getContentType());
            actaEntity.setData(file.getBytes());
            actaEntity.setSize(file.getSize());
            actaEntity.setAlumno(alumno);
            actaRepository.save(actaEntity);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File updated successfully: %s", file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not update the file: %s!", file.getOriginalFilename()));
        }
    }

}
