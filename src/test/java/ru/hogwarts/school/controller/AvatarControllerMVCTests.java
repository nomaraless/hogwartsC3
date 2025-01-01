package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvatarController.class)
public class AvatarControllerMVCTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvatarService avatarService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void uploadAvatarTest() throws Exception {
        Long studentId = 1L;
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "Без названия.png", MediaType.IMAGE_PNG_VALUE, "Без названия.png".getBytes()
        );

        mockMvc.perform(multipart("/avatar/{studentId}", studentId)
                        .file(mockFile))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void downloadAvatarPreviewTest() throws Exception {
        Long studentId = 1L;
        Avatar avatar = new Avatar();
        avatar.setPreview("Без названия.png".getBytes());
        avatar.setMediaType(MediaType.IMAGE_PNG_VALUE);

        when(avatarService.findAvatar(studentId)).thenReturn(avatar);

        mockMvc.perform(get("/avatar/{studentId}/preview", studentId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes("Без названия.png".getBytes()));
    }

    @Test
    void downloadAvatarTest() throws Exception {
        Long studentId = 1L;

        String filePath = new ClassPathResource("TestAvatar/Без названия.png").getFile().getPath();
        Avatar avatar = new Avatar();
        avatar.setFilePath(filePath);
        avatar.setFileSize(Files.size(Path.of(filePath)));
        avatar.setMediaType(MediaType.IMAGE_PNG_VALUE);

        when(avatarService.findAvatar(studentId)).thenReturn(avatar);

        mockMvc.perform(get("/avatar/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_PNG_VALUE));
    }
}
