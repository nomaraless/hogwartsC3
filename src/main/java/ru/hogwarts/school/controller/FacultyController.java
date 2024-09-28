package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id) {
        return service.findFaculty(id);
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculty() {
        return service.getAllFaculty();
    }

    @GetMapping("/filter/{color}")
    public ResponseEntity<Collection<Faculty>> filterByColor(@PathVariable String color) {
        return service.filterByColor(color);
    }

    @PostMapping

    public Faculty createFaculty(Faculty faculty) {
        return service.createFaculty(faculty);
    }

    @DeleteMapping("{id}")
    public Faculty deleteFaculty(long id) {
        return service.deleteFaculty(id);
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        return service.editFaculty(faculty);
    }
}
