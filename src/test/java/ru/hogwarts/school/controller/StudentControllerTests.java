package ru.hogwarts.school.controller;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Student;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTests {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    final int age = 100;
    final String name = "random";

    Student student = new Student(null, name, age);

    private String getUrl() {
        return "http://localhost:" + port + "/student";
    }

    @Test
    void contextLoadsTest() throws Exception {
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    public void getAllStudentTest() {
        Assertions.assertThat(this.restTemplate.getForObject(getUrl() + "/all", String.class)).isNotNull();
    }

    @Test
    public void create() {
        Student student1 = new Student(null, "fa", 5);
        ResponseEntity<Student> responseEntity = restTemplate.postForEntity("/student", student1, Student.class);

        Assertions.assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody().getId()).isNotNull();
        Assertions.assertThat(responseEntity.getBody().getName()).isEqualTo("fa");
        Assertions.assertThat(responseEntity.getBody().getAge()).isEqualTo(5);
    }

    @Test
    public void getStudentByIdTest() {
        Student studentTest = restTemplate.postForObject(getUrl(), student, Student.class);

        ResponseEntity<Student> responseEntity = restTemplate.getForEntity(getUrl() + "/" + studentTest.getId(), Student.class);

        Assertions
                .assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions
                .assertThat(responseEntity.getBody()).isNotNull();
        Assertions
                .assertThat(responseEntity.getBody().getAge()).isEqualTo(age);
        Assertions
                .assertThat(responseEntity.getBody().getName()).isEqualTo(name);
    }

    @Test
    public void getStudentFilterByAgeTest() {
        ResponseEntity<List<Student>> responseEntity = restTemplate.exchange(getUrl() + "/filter/" + student.getAge(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {
        });

        Assertions
                .assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();

        List<Student> students = responseEntity.getBody();
        Assertions
                .assertThat(students).isNotNull();
        Student student1 = students.get(0);
        Assertions
                .assertThat(student1.getAge()).isEqualTo(age);
        Assertions
                .assertThat(student1.getName()).isEqualTo(name);
    }

    @Test
    public void editStudentTest() {
        Student updateStudent = restTemplate.postForObject(getUrl(), student, Student.class);

        updateStudent.setName("sssss");
        HttpEntity<Student> edit = new HttpEntity<>(updateStudent);
        ResponseEntity<Student> responseEntity = restTemplate.exchange(getUrl(), HttpMethod.PUT, edit, Student.class);

        Assertions
                .assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions
                .assertThat(responseEntity.getBody().getAge()).isEqualTo(age);
        Assertions
                .assertThat(responseEntity.getBody().getName()).isEqualTo("sssss");
    }

    @Test
    public void deleteStudentTest() {
        Student studentTest = restTemplate.postForObject(getUrl(), student, Student.class);
        ResponseEntity<Void> delete = restTemplate.exchange(getUrl() + "/" + studentTest.getId(), HttpMethod.DELETE, null, void.class);
        ResponseEntity<Student> responseEntity = restTemplate.getForEntity(getUrl() + "/" + studentTest.getId(), Student.class);

        Assertions
                .assertThat(delete.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions
                .assertThat(responseEntity.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    public void getLastFiveStudentsTest() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/student/getLastFiveStudents", Student[].class);

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    public void getCountStudents() {
        ResponseEntity<Integer> response = restTemplate.getForEntity("/student/getCountStudents", Integer.class);

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void getAvgOgStudents() {
        ResponseEntity<Double> response = restTemplate.getForEntity("/student/getAvgOfStudents", Double.class);

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isGreaterThanOrEqualTo(0);
    }
}