package com.frontbackend.springboot.service;

import com.frontbackend.springboot.entity.ActaEntity;
import com.frontbackend.springboot.entity.AlumnoEntity;
import com.frontbackend.springboot.entity.CurpEntity;
import com.frontbackend.springboot.repository.CurpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CurpService {

    private final CurpRepository curpRepository;

    @Autowired
    public CurpService(CurpRepository curpRepository) {
        this.curpRepository = curpRepository;
    }

    public void save(MultipartFile file,  AlumnoEntity alumno) throws IOException {
        CurpEntity curpEntity = new CurpEntity();
        curpEntity.setName(StringUtils.cleanPath(file.getOriginalFilename()));
        curpEntity.setContentType(file.getContentType());
        curpEntity.setData(file.getBytes());
        curpEntity.setSize(file.getSize());
        curpEntity.setAlumno(alumno);
        curpRepository.save(curpEntity);
    }

    public Optional<CurpEntity> getFile(Integer id) {
        return curpRepository.findById(id);
    }

    public List<CurpEntity> getAllFiles() {
        return curpRepository.findAll();
    }

    @Transactional
    public Optional<CurpEntity> findByAlumnoId (String id) {
        return curpRepository.findByIdAlumno(id);
    }

    @Transactional
    public Optional<CurpEntity> findAlumnoId (Integer id) {
        return curpRepository.findAlumnoId(id);
    }
}
