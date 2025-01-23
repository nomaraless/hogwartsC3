package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.Interface.FacultyInterface;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

@Service
public class FacultyService implements FacultyInterface {
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    @Autowired
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method to create faculty: ", faculty);
        return facultyRepository.save(faculty);
    }

    public Collection<Faculty> filterByColor(String color) {
        logger.info("Was invoked method to filter faculties by color: ", color);
        return facultyRepository.findFacultyByColor(color);
    }

    public Faculty findFaculty(long id) {
        logger.info("Was invoked method to find faculty by id: ", id);
        return facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("Faculty with id {} not found", id);
            return new NoSuchElementException("Faculty not found");
        });
    }

    public Collection<Faculty> getAllFaculty() {
        logger.info("Was invoked method to get all faculties");
        return facultyRepository.findAll();
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method to edit faculty: ", faculty);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method to delete faculty with id: ", id);
        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id);
            logger.info("Faculty with id {} deleted successfully", id);
        } else {
            logger.warn("No faculty found with id {} to delete", id);
        }
    }

    public String getLongestFacultyName() {
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("Faculty not found");
    }

    public int sum() {
//        int a = 1_000_000;
//        return a * (a + 1 / 2);
        return IntStream.rangeClosed(0, 1_000_000).sum();
    }
}
