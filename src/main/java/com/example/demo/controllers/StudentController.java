package com.example.demo.controllers;

import com.example.demo.entities.Student;
import com.example.demo.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "Enregistrer un étudiant")
    @PostMapping("/save")
    public ResponseEntity<Student> save(@RequestBody Student student) {
        Student savedStudent = studentService.save(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @Operation(summary = "Supprimer un étudiant par son identifiant")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        boolean deleted = studentService.delete(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Récupérer tous les étudiants")
    @GetMapping("/all")
    public ResponseEntity<List<Student>> findAll() {
        return new ResponseEntity<>(studentService.findAll(), HttpStatus.OK);
    }

    @Operation(summary = "Compter les étudiants")
    @GetMapping("/count")
    public ResponseEntity<Long> countStudent() {
        return new ResponseEntity<>(studentService.countStudents(), HttpStatus.OK);
    }

    @Operation(summary = "Nombre d'étudiants par année de naissance")
    @GetMapping("/byYear")
    public ResponseEntity<Collection<Object[]>> findByYear() {
        return new ResponseEntity<>(studentService.findNbrStudentByYear(), HttpStatus.OK);
    }
}
