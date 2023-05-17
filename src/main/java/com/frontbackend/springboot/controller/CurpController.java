package com.frontbackend.springboot.controller;

import com.frontbackend.springboot.entity.*;
import com.frontbackend.springboot.exception.ResourceNotFoundException;
import com.frontbackend.springboot.repository.AlumnoRepository;
import com.frontbackend.springboot.repository.CurpRepository;
import com.frontbackend.springboot.service.CurpService;
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
public class CurpController {

    private final CurpService curpService;

    @Autowired
    public CurpController(CurpService curpService) {
        this.curpService = curpService;
    }

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private CurpRepository curpRepository;

    @PostMapping("/curp/{matricula}/guardar")
    public ResponseEntity<String> guardarCurp(@RequestParam("file") MultipartFile file,
                                              @PathVariable(value = "matricula") String matAlumno) {

        // Validar si el alumno existe
        AlumnoEntity alumno = alumnoRepository.findByMatriculaAlumno(matAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un alumno con el ID: " + matAlumno));

        // Validar si ya existe un registro con el mismo id_Alumno en la tabla
        Optional<CurpEntity> existingActa = curpService.findAlumnoId(alumno.getId_alumno());
        if (existingActa.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un registro de acta para el alumno con la matrícula: " + matAlumno);
        }
        try {
            // Guardar el archivo
            curpService.save(file, alumno);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File uploaded successfully: %s", file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not upload the file: %s!", file.getOriginalFilename()));
        }
    }

    @GetMapping("/curp/ver_todas")
    public List<CurpResponse> list() {
        return curpService.getAllFiles()
                          .stream()
                          .map(curpEntity -> {
                              CurpResponse curpResponse = mapToFileResponse(curpEntity);
                              curpResponse.setAlumno(curpEntity.getAlumno());
                              return curpResponse;
                          })
                            .collect(Collectors.toList());
    }


    private CurpResponse mapToFileResponse(CurpEntity curpEntity) {
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                        .path("/files/")
                                                        .path(String.valueOf(curpEntity.getId()))
                                                        .toUriString();
        CurpResponse curpResponse = new CurpResponse();
        curpResponse.setId(String.valueOf(curpEntity.getId()));
        curpResponse.setName(curpEntity.getName());
        curpResponse.setContentType(curpEntity.getContentType());
        curpResponse.setSize(curpEntity.getSize());
        curpResponse.setUrl(downloadURL);

        return curpResponse;
    }

    @GetMapping("/curp/ver_id/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Integer id) {
        Optional<CurpEntity> fileEntityOptional = curpService.getFile(id);

        if (!fileEntityOptional.isPresent()) {
            return ResponseEntity.notFound()
                                 .build();
        }

        CurpEntity curpEntity = fileEntityOptional.get();
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + curpEntity.getName() + "\"")
                             .contentType(MediaType.valueOf(curpEntity.getContentType()))
                             .body(curpEntity.getData());
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

    @GetMapping("/curp/ver_matricula_alumno/{matricula}")
    public ResponseEntity<CurpAlumnoResponse> getFileByAlumnoId(@PathVariable String matricula) {
        Optional<CurpEntity> curpEntityOptional = curpService.findByAlumnoId(matricula);

        if (!curpEntityOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        CurpEntity curpEntity = curpEntityOptional.get();
        AlumnoEntity alumnoEntity = curpEntity.getAlumno();

        // Crear una respuesta combinada que contenga tanto los datos del archivo como los datos del alumno
        CurpAlumnoResponse response = new CurpAlumnoResponse();
        response.setCurp(mapToFileResponse(curpEntity));
        response.setAlumno(mapToAlumnoResponse(alumnoEntity));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/curp/{matricula}/editar")
    public ResponseEntity<String> editarCurp(@RequestParam("file") MultipartFile file,
                                             @PathVariable(value = "matricula") String idAlumno) {

        // Validar si el alumno existe
        AlumnoEntity alumno = alumnoRepository.findByMatriculaAlumno(idAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un alumno con el ID: " + idAlumno));

        // Buscar el archivo existente del alumno
        Optional<CurpEntity> curpEntityOptional = curpService.findByAlumnoId(idAlumno);
        if (!curpEntityOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        CurpEntity curpEntity = curpEntityOptional.get();

        try {
            // Actualizar los datos del archivo
            curpEntity.setName(StringUtils.cleanPath(file.getOriginalFilename()));
            curpEntity.setContentType(file.getContentType());
            curpEntity.setData(file.getBytes());
            curpEntity.setSize(file.getSize());
            curpEntity.setAlumno(alumno);
            curpRepository.save(curpEntity);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File updated successfully: %s", file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not update the file: %s!", file.getOriginalFilename()));
        }
    }
}
