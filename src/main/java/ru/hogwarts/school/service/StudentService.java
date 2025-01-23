package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.Interface.StudentInterface;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StudentService implements StudentInterface {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public StudentService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for creating student: ", student);
        return studentRepository.save(student);
    }

    public Collection<Student> filterByAge(int age) {
        logger.info("Was invoked method to filter students by age: ", age);
        return studentRepository.findByAge(age);
    }

    public Student findStudent(Long id) {
        logger.info("Was invoked method to find student by id: ", id);
        return studentRepository.findById(id).orElseThrow(() -> {
            logger.error("There is no student with id = ", id);
            return new NoSuchElementException("Not found");
        });
    }

    public Collection<Student> getAllStudent() {
        logger.info("Was invoked method to get all students");
        return studentRepository.findAll();
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method to edit student: ", student);
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(long id) {
        logger.info("Was invoked method to delete student by id: ", id);
        if (avatarRepository.findByStudentId(id) != null) {
            logger.debug("Avatar found for student id = {}, deleting avatar", id);
            avatarRepository.deleteByStudentId(id);
        } else {
            logger.warn("No avatar found for student id: ", id);
        }
        studentRepository.deleteById(id);
    }

    public Integer getCountStudents() {
        logger.info("Was invoked method to get count of students");
        return studentRepository.getCountStudents();
    }

    public Double getAvgOfStudents() {
        logger.info("Was invoked method to get average age of students");
        return studentRepository.getAvgOfStudents();
    }

    public Collection<Student> getLastFiveStudents() {
        logger.info("Was invoked method to get the last five students");
        return studentRepository.getLastFiveStudents();
    }

    public List<String> getNameStudentStartWithA() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    public double getAvgAge() {
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0);
    }

    public void printStudentsInParallel() {
        List<Student> students = studentRepository.findAll().stream().toList();

        System.out.println("Оснвоной поток");
        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        Thread thread = new Thread(() -> {
            System.out.println("Первый поток");
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        });

        Thread thread1 = new Thread(() -> {
            System.out.println("Второй поток");
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        });

        thread.start();
        thread1.start();
    }

    public void printStudentsInSynchronized() {
        List<Student> students = studentRepository.findAll().stream().toList();

        synchronized (this) {
            System.out.println(students.get(0).getName());
            System.out.println(students.get(1).getName());
        }

        Thread thread = new Thread(() -> {
            synchronized (this) {
                System.out.println(students.get(2).getName());
                System.out.println(students.get(3).getName());
            }
        });

        Thread thread1 = new Thread(() -> {
            synchronized (this) {
                System.out.println(students.get(4).getName());
                System.out.println(students.get(5).getName());
            }
        });

        thread.start();
        thread1.start();
    }
}
