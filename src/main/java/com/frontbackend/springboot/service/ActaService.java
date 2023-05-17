package com.frontbackend.springboot.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.frontbackend.springboot.entity.AlumnoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.frontbackend.springboot.entity.ActaEntity;
import com.frontbackend.springboot.repository.ActaRepository;

import javax.transaction.Transactional;

@Service
public class ActaService {

    private final ActaRepository actaRepository;

    @Autowired
    public ActaService(ActaRepository actaRepository) {
        this.actaRepository = actaRepository;
    }

    public void save(MultipartFile file, AlumnoEntity alumno) throws IOException {
        ActaEntity actaEntity = new ActaEntity();
        actaEntity.setName(StringUtils.cleanPath(file.getOriginalFilename()));
        actaEntity.setContentType(file.getContentType());
        actaEntity.setData(file.getBytes());
        actaEntity.setSize(file.getSize());
        actaEntity.setAlumno(alumno);
        actaRepository.save(actaEntity);
    }

    public Optional<ActaEntity> getFile(Integer id) {
        return actaRepository.findById(id);
    }

    @Transactional
    public Optional<ActaEntity> findByAlumnoId (String id) {
        return actaRepository.findByIdAlumno(id);
    }

    @Transactional
    public Optional<ActaEntity> findAlumnoId (Integer id) {
        return actaRepository.findAlumnoId(id);
    }

    public List<ActaEntity> getAllFiles() {
        return actaRepository.findAll();
    }
}
