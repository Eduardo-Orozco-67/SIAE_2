package com.frontbackend.springboot.controller;

import com.frontbackend.springboot.entity.*;
import com.frontbackend.springboot.exception.ResourceNotFoundException;
import com.frontbackend.springboot.repository.AlumnoRepository;
import com.frontbackend.springboot.repository.ComprobanteRepository;
import com.frontbackend.springboot.service.ComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("files")
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    @Autowired
    public ComprobanteController(ComprobanteService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private ComprobanteRepository comprobanteRepository;

    @PostMapping("/comprobante/{matricula}/guardar")
    public ResponseEntity<String> guardarComprobante(@RequestParam("file") MultipartFile file,
                                              @PathVariable(value = "matricula") String matAlumno) {

        // Validar si el alumno existe
        AlumnoEntity alumno = alumnoRepository.findByMatriculaAlumno(matAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un alumno con el ID: " + matAlumno));

        // Validar si ya existe un registro con el mismo id_Alumno en la tabla
        Optional<ComprobanteEntity> existingActa = comprobanteService.findAlumnoId(alumno.getId_alumno());
        if (existingActa.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un registro de acta para el alumno con la matrícula: " + matAlumno);
        }

        try {
            // Guardar el archivo
            comprobanteService.save(file, alumno);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File uploaded successfully: %s", file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not upload the file: %s!", file.getOriginalFilename()));
        }
    }

    @GetMapping("/comprobante/ver_todas")
    public List<ComprobanteResponse> list() {
        return comprobanteService.getAllFiles()
                          .stream()
                          .map(curpEntity -> {
                              ComprobanteResponse comprobanteResponse = mapToFileResponse(curpEntity);
                              comprobanteResponse.setAlumno(curpEntity.getAlumno());
                               return comprobanteResponse;
                          })
                          .collect(Collectors.toList());
    }

    private ComprobanteResponse mapToFileResponse(ComprobanteEntity comprobanteEntity) {
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                        .path("/files/")
                                                        .path(String.valueOf(comprobanteEntity.getId()))
                                                        .toUriString();
        ComprobanteResponse comprobanteResponse = new ComprobanteResponse();
        comprobanteResponse.setId(String.valueOf(comprobanteEntity.getId()));
        comprobanteResponse.setName(comprobanteEntity.getName());
        comprobanteResponse.setContentType(comprobanteEntity.getContentType());
        comprobanteResponse.setSize(comprobanteEntity.getSize());
        comprobanteResponse.setUrl(downloadURL);

        return comprobanteResponse;
    }

    @GetMapping("/comprobante/ver_id/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Integer id) {
        Optional<ComprobanteEntity> fileEntityOptional = comprobanteService.getFile(id);

        if (!fileEntityOptional.isPresent()) {
            return ResponseEntity.notFound()
                                 .build();
        }

        ComprobanteEntity comprobanteEntity = fileEntityOptional.get();
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + comprobanteEntity.getName() + "\"")
                             .contentType(MediaType.valueOf(comprobanteEntity.getContentType()))
                             .body(comprobanteEntity.getData());
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

    @GetMapping("/comprobante/ver_matricula_alumno/{matricula}")
    public ResponseEntity<ComprobanteAlumnoResponse> getFileByAlumnoId(@PathVariable(value = "matricula") String id) {
        Optional<ComprobanteEntity> comprobanteEntityOptional = comprobanteService.findByAlumnoId(id);

        if (!comprobanteEntityOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ComprobanteEntity comprobanteEntity = comprobanteEntityOptional.get();
        AlumnoEntity alumnoEntity = comprobanteEntity.getAlumno();

        // Crear una respuesta combinada que contenga tanto los datos del archivo como los datos del alumno
        ComprobanteAlumnoResponse response = new ComprobanteAlumnoResponse();
        response.setComprobante(mapToFileResponse(comprobanteEntity));
        response.setAlumno(mapToAlumnoResponse(alumnoEntity));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/comprobante/{matricula}/editar")
    public ResponseEntity<String> editarComprobante(@RequestParam("file") MultipartFile file,
                                             @PathVariable(value = "matricula") String idAlumno) {

        // Validar si el alumno existe
        AlumnoEntity alumno = alumnoRepository.findByMatriculaAlumno(idAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un alumno con el ID: " + idAlumno));

        // Buscar el archivo existente del alumno
        Optional<ComprobanteEntity> comprobanteEntityOptional = comprobanteService.findByAlumnoId(idAlumno);
        if (!comprobanteEntityOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ComprobanteEntity comprobanteEntity = comprobanteEntityOptional.get();

        try {
            // Actualizar los datos del archivo
            comprobanteEntity.setName(StringUtils.cleanPath(file.getOriginalFilename()));
            comprobanteEntity.setContentType(file.getContentType());
            comprobanteEntity.setData(file.getBytes());
            comprobanteEntity.setSize(file.getSize());
            comprobanteEntity.setAlumno(alumno);
            comprobanteRepository.save(comprobanteEntity);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File updated successfully: %s", file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not update the file: %s!", file.getOriginalFilename()));
        }
    }
}
