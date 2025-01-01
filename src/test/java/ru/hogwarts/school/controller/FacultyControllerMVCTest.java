package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerMVCTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    FacultyRepository repository;

    @SpyBean
    FacultyService service;

    @InjectMocks
    FacultyController controller;

    private final long id = 120L;
    private final String color = "red";
    private final String name = "random";
    private final Faculty faculty = new Faculty(id, name, color);

    @Test
    public void getAllFacultyTest() throws Exception {
        when(service.getAllFaculty()).thenReturn(Arrays.asList(faculty));

        mockMvc.perform(get("/faculty/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is((int) id)))
                .andExpect(jsonPath("$[0].name", is(name)))
                .andExpect(jsonPath("$[0].color", is(color)));
    }

    @Test
    public void getFacultyByIdTest() throws Exception {
        when(repository.findById(id)).thenReturn(Optional.of(faculty));
        when(service.findFaculty(id)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)id)))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.color", is(color)));
    }

    @Test
    public void getFacultyFilterByColor() throws Exception {
        when(service.filterByColor(color)).thenReturn(Arrays.asList(faculty));

        mockMvc.perform(get("/faculty/filter/{color}", color))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is((int) id)))
                .andExpect(jsonPath("$[0].name", is(name)))
                .andExpect(jsonPath("$[0].color", is(color)));
    }

    @Test
    public void editFacultyTest() throws Exception {
        Faculty faculty1 = new Faculty(id, "newName", color);
        when(service.editFaculty(Mockito.any(Faculty.class))).thenReturn(faculty1);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("newName")))
                .andExpect(jsonPath("$.color", is(color)));
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        Mockito.doNothing().when(service).deleteFaculty(id);

        mockMvc.perform(delete("/faculty/{id}", id))
                .andExpect(status().isOk());
    }
}

