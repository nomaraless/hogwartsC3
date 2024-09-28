package ru.hogwarts.school.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Long, Student> studentMap = new HashMap<>();
    private Long lastId = 0L;

    public Student createStudent(Student student) {
        student.setId(++lastId);
        studentMap.put(lastId, student);
        return student;
    }

    public ResponseEntity<Collection<Student>> filterByAge(int age) {
        Collection<Student> students = studentMap.values().stream().filter(student -> student.getAge() == age).collect(Collectors.toList());
        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(students);
    }

    public ResponseEntity<Student> findStudent(long id) {
        if (studentMap.get(id) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(studentMap.get(id));
    }

    public ResponseEntity<Collection<Student>> getAllStudent() {
        return ResponseEntity.ok(studentMap.values());
    }

    public ResponseEntity<Student> editStudent(Student student) {
        if (studentMap.get(student.getId()) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(studentMap.put(student.getId(), student));
    }

    public Student deleteStudent(long id) {
        return studentMap.remove(id);
    }

}
