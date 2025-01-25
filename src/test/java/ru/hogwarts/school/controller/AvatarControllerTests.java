package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import ru.hogwarts.school.model.Avatar;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AvatarControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getUrl() {
        return "http://localhost:" + port + "/avatar";
    }

    @Test
    public void uploadAvatarTest() throws IOException {
        Long studentId = 10L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ClassPathResource fileResource = new ClassPathResource("TestAvatar/AvatarTest.png");
        HttpEntity<ClassPathResource> requestEntity = new HttpEntity<>(fileResource, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getUrl() + "/" + studentId,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void downloadAvatarPreviewTest() {
        Long studentId = 1L;

        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                getUrl() + "/" + studentId + "/preview",
                byte[].class
        );

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful());
        Assertions.assertThat(response.getHeaders().getContentType()).isNotNull();
        Assertions.assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    public void downloadAvatarTest() throws IOException {
        Long studentId = 1L;

        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                getUrl() + "/" + studentId,
                byte[].class
        );

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful());
        Assertions.assertThat(response.getHeaders().getContentType()).isNotNull();
        Assertions.assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetAvatarsByPage() {
        int page = 1;
        int size = 5;

        ResponseEntity<java.util.Map<String, Object>> response = restTemplate.exchange(
                "/avatar/page?page=" + page + "&size=" + size,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(((List<?>) response.getBody().get("content")).size()).isLessThanOrEqualTo(size);
    }
}
