package com.frontbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.frontbackend.springboot.entity.ActaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActaRepository extends JpaRepository<ActaEntity, Integer> {
    @Query(value="select a.id, a.content_type, a.data, a.name, a.size, a.id_alumno from actas a inner join tb_Alumno t on t.id = a.id_alumno where t.matricula like ?1", nativeQuery = true)
    Optional<ActaEntity> findByIdAlumno (String mat);

    @Query(value="select * from actas a where id_alumno = ?1", nativeQuery = true)
    Optional<ActaEntity> findAlumnoId(Integer alumnoId);
}
