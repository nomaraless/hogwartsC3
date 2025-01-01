package ru.hogwarts.school.service;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {

    @Value("$.path.to.avatars.folder")
    private String avatarsDir;
    private final StudentService service;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService service, AvatarRepository avatarRepository) {
        this.service = service;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = service.findStudent(studentId);
        if (student == null) {
            throw new NoSuchElementException("Student with ID " + studentId + " not found");
        }

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);) {
            bis.transferTo(bos);
        }
        Avatar avatar = avatarRepository.findById(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setPreview(generateImagePrewiew(filePath));

        avatarRepository.save(avatar);
    }

    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseThrow();
    }

private byte[] generateImagePrewiew(Path filePath) throws IOException {
    try (InputStream is = Files.newInputStream(filePath);
         BufferedInputStream bis = new BufferedInputStream(is, 1024);
         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        BufferedImage image = ImageIO.read(bis);
        if (image == null) {
            throw new IOException("Filed to read image from file: " + filePath);
        }

        int height = image.getHeight() / (image.getWidth() / 100);
        BufferedImage preview = new BufferedImage(100, height, image.getType());
        Graphics2D graphics2D = preview.createGraphics();
        graphics2D.drawImage(image, 0, 0, 100, height, null);
        graphics2D.dispose();

        ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
        return baos.toByteArray();
    }
}

    private String getExtension(String fileName) {
        if (fileName.lastIndexOf(".") == -1 ||
                fileName.lastIndexOf(".") == fileName.length() - 1) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
