package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {
    private StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getFaculty(@PathVariable long id) {
        return service.findStudent(id);
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudent() {
        return service.getAllStudent();
    }

    @GetMapping("/filter/{age}")
    public ResponseEntity<Collection<Student>> filterByAge(@PathVariable int age) {
        return service.filterByAge(age);
    }
    @PostMapping
    public Student createFaculty(Student student) {
        return service.createStudent(student);
    }

    @DeleteMapping("{id}")
    public Student deleteFaculty(long id) {
        return service.deleteStudent(id);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        return service.editStudent(student);
    }
}
