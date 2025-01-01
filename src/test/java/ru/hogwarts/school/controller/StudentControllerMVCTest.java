package ru.hogwarts.school.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerMVCTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StudentRepository repository;

    @SpyBean
    StudentService service;

    @InjectMocks
    StudentController controller;

    private long id = 100L;
    private String name = "random";
    private int age = 15;
    private final Student student = new Student(id, name,age);

    @Test
    public void getAllStudentTest() throws Exception {
        when(service.getAllStudent()).thenReturn(Arrays.asList(student));

        mockMvc.perform(get("/student/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(name)))
                .andExpect(jsonPath("$[0].age", is(age)));
    }

    @Test
    public void getStudentByIdTest() throws Exception {
        when(repository.findById(id)).thenReturn(Optional.of(student));
        when(service.findStudent(id)).thenReturn(student);

        mockMvc.perform(get("/student/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.age", is(age)));
    }

    @Test
    public void getStudentFilterByAge() throws Exception {
        when(service.filterByAge(age)).thenReturn(Arrays.asList(student));

        mockMvc.perform(get("/student/filter/{age}", age))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(name)))
                .andExpect(jsonPath("$[0].age", is(age)));
    }

    @Test
    public void editStudentTest() throws Exception {
        Student student1 = new Student(id, "newName", age);
        when(service.editStudent(any(Student.class))).thenReturn(student1);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("newName")))
                .andExpect(jsonPath("$.age", is(age)));
    }

    @Test
    public void deleteStudentTest() throws Exception {
        Mockito.doNothing().when(service).deleteStudent(id);

        mockMvc.perform(delete("/student/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
