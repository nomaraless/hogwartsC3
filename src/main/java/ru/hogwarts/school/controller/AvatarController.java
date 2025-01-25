package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }
        avatarService.uploadAvatar(studentId, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{studentId}/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long studentId) {
        byte[] preview = avatarService.getAvatarPreview(studentId);
        Avatar avatar = avatarService.findAvatar(studentId);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        httpHeaders.setContentLength(avatar.getPreview().length);

        return ResponseEntity.ok().headers(httpHeaders).body(preview);
    }

    @GetMapping(value = "/{studentId}")
    public void downloadAvatar(@PathVariable Long studentId, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatar(studentId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(avatar.getMediaType());
        response.setContentLength((int) avatar.getFileSize());

        try (OutputStream os = response.getOutputStream()) {
            avatarService.avatarToResponse(studentId, os);
        }
    }

    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getAvatarsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Avatar> avatarPage = avatarService.getAvatarsByPage(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", avatarPage.getContent());
        response.put("currentPage", avatarPage.getNumber());
        response.put("totalItems", avatarPage.getTotalElements());
        response.put("totalPages", avatarPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

}