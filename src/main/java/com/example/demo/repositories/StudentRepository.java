package com.example.demo.repositories;

import com.example.demo.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Collection;

// exported = false : évite que Spring Data REST n'expose /students et n'entre en conflit avec notre contrôleur
@RepositoryRestResource(exported = false)
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // Recherche d'un étudiant par son identifiant
    Student findById(int id);

    // Nombre d'étudiants par année de naissance
    @Query("SELECT YEAR(s.dateNaissance), COUNT(s) FROM Student s GROUP BY YEAR(s.dateNaissance)")
    Collection<Object[]> findNbrStudentByYear();
}
