package ru.hogwarts.school.controller;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTests {
    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    final long id = 1L;
    final String name = "Griaindor";
    final String color = "Red";

    Faculty faculty = new Faculty(id, name, color);

    private String getUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @Test
    void contextLoadsTest() throws Exception {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    public void getAllFacultyTest() {
        Assertions.assertThat(this.restTemplate.getForObject(getUrl() + "/all", String.class)).isNotNull();
    }

    @Test
    public void getFacultyByIdTest() {
        Faculty facultyTest = restTemplate.postForObject(getUrl(), faculty, Faculty.class);

        ResponseEntity<Faculty> responseEntity = restTemplate.getForEntity(getUrl() + "/" + facultyTest.getId(), Faculty.class);

        Assertions
                .assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions
                .assertThat(responseEntity.getBody().getColor()).isEqualTo(color);
        Assertions
                .assertThat(responseEntity.getBody().getName()).isEqualTo(name);
    }

    @Test
    public void getFacultyFilterByColorTest() {
        ResponseEntity<Faculty[]> responseEntity = restTemplate.getForEntity(getUrl() + "/filter/" + color, Faculty[].class);

        Assertions
                .assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        Faculty[] faculties = responseEntity.getBody();
        Assertions
                .assertThat(faculties).isNotNull();
        Assertions
                .assertThat(faculties).anyMatch(faculty -> faculty.getColor().equals(color));
    }

    @Test
    public void editFacultyTest() {
        Faculty updateFaculty = restTemplate.postForObject(getUrl(), faculty, Faculty.class);

        updateFaculty.setName("sssss");
        HttpEntity<Faculty> edit = new HttpEntity<>(updateFaculty);
        ResponseEntity<Faculty> responseEntity = restTemplate.exchange(getUrl(), HttpMethod.PUT, edit, Faculty.class);

        Assertions
                .assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions
                .assertThat(responseEntity.getBody().getColor()).isEqualTo(color);
        Assertions
                .assertThat(responseEntity.getBody().getName()).isEqualTo("sssss");
    }

    @Test
    public void deleteFacultyTest() {
        restTemplate.delete(getUrl() + "/" + faculty.getId());

        ResponseEntity<Faculty> responseEntity = restTemplate.getForEntity(getUrl() + "/" + faculty.getId(), Faculty.class);

        Assertions
                .assertThat(responseEntity.getStatusCode().is2xxSuccessful());
    }
}