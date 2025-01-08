package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.Interface.StudentInterface;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class StudentService implements StudentInterface {
    @Autowired
    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public StudentService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Collection<Student> filterByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Student findStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not found"));
    }

    public Collection<Student> getAllStudent() {
        return studentRepository.findAll();
    }

    public Student editStudent(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(long id) {
        if (avatarRepository.findByStudentId(id) != null) {
            avatarRepository.deleteByStudentId(id);
        }
        studentRepository.deleteById(id);
    }

    public Integer getCountStudents() {
        return studentRepository.getCountStudents();
    }

    public Double getAvgOfStudents() {
        return studentRepository.getAvgOfStudents();
    }

    public Collection<Student> getLastFiveStudents() {
        return studentRepository.getLastFiveStudents();
    }
}