package ru.hogwarts.school.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Map<Long, Faculty> facultyMap = new HashMap<>();
    private Long lastId = 0L;


    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++lastId);
        facultyMap.put(lastId, faculty);
        return faculty;
    }

    public ResponseEntity<Collection<Faculty>> filterByColor(String color) {
        Collection<Faculty> faculties = facultyMap.values().stream().filter(faculty -> faculty.getColor().equals(color)).collect(Collectors.toList());
        if (faculties.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(faculties);
    }

    public ResponseEntity<Faculty> findFaculty(long id) {
        if (facultyMap.get(id) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(facultyMap.get(id));
    }

    public ResponseEntity<Collection<Faculty>> getAllFaculty() {
        return ResponseEntity.ok(facultyMap.values());
    }


    public ResponseEntity<Faculty> editFaculty(Faculty faculty) {
        if (facultyMap.get(faculty.getId()) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(facultyMap.put(faculty.getId(), faculty));
    }

    public Faculty deleteFaculty(long id) {
        return facultyMap.remove(id);
    }
}
