package com.frontbackend.springboot.service;

import com.frontbackend.springboot.entity.ActaEntity;
import com.frontbackend.springboot.entity.AlumnoEntity;
import com.frontbackend.springboot.entity.ComprobanteEntity;
import com.frontbackend.springboot.entity.CurpEntity;
import com.frontbackend.springboot.repository.ComprobanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ComprobanteService {

    private final ComprobanteRepository comprobanteRepository;

    @Autowired
    public ComprobanteService(ComprobanteRepository comprobanteRepository) {
        this.comprobanteRepository = comprobanteRepository;
    }

    public void save(MultipartFile file,  AlumnoEntity alumno) throws IOException {
        ComprobanteEntity comprobanteEntity = new ComprobanteEntity();
        comprobanteEntity.setName(StringUtils.cleanPath(file.getOriginalFilename()));
        comprobanteEntity.setContentType(file.getContentType());
        comprobanteEntity.setData(file.getBytes());
        comprobanteEntity.setSize(file.getSize());
        comprobanteEntity.setAlumno(alumno);
        comprobanteRepository.save(comprobanteEntity);
    }

    public Optional<ComprobanteEntity> getFile(Integer id) {
        return comprobanteRepository.findById(id);
    }

    public List<ComprobanteEntity> getAllFiles() {
        return comprobanteRepository.findAll();
    }

    @Transactional
    public Optional<ComprobanteEntity> findByAlumnoId (String id) {
        return comprobanteRepository.findByIdAlumno(id);
    }

    @Transactional
    public Optional<ComprobanteEntity> findAlumnoId (Integer id) {
        return comprobanteRepository.findAlumnoId(id);
    }
}
