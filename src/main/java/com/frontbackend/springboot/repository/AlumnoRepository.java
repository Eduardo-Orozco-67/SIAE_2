package com.frontbackend.springboot.repository;

import com.frontbackend.springboot.entity.ActaEntity;
import com.frontbackend.springboot.entity.AlumnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<AlumnoEntity, Integer> {
    @Query(value="select * from tb_alumno where matricula like ?1", nativeQuery = true)
    Optional<AlumnoEntity> findByMatriculaAlumno (String mat);

    @Query(value="delete from tb_alumno where matricula like ?1", nativeQuery = true)
    Optional<AlumnoEntity> deleteByMatriculaAlumno (String mat);
}
