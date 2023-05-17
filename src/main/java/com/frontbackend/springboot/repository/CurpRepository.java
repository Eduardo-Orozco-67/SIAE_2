package com.frontbackend.springboot.repository;

import com.frontbackend.springboot.entity.ActaEntity;
import com.frontbackend.springboot.entity.CurpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurpRepository extends JpaRepository<CurpEntity, Integer> {

    @Query(value="select a.id, a.content_type, a.data, a.name, a.size, a.id_alumno from curps a inner join tb_Alumno t on t.id = a.id_alumno where t.matricula like ?1", nativeQuery = true)
    Optional<CurpEntity> findByIdAlumno (String mat);
    @Query(value="select * from curps a where id_alumno = ?1", nativeQuery = true)
    Optional<CurpEntity> findAlumnoId(Integer alumnoId);

}
