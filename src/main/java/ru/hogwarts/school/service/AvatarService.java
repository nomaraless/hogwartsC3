package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;

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

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Method uploadAvatar invoked for studentId: ", studentId);

        Student student = studentService.findStudent(studentId);
        if (student == null) {
            logger.error("Student with ID {} not found", studentId);
            throw new NoSuchElementException("Student with ID " + studentId + " not found");
        }

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        logger.debug("Generated file path for avatar: ", filePath);

        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {

            bis.transferTo(bos);
            logger.info("Avatar uploaded for studentId: ", studentId);
        }

        Avatar avatar = avatarRepository.findById(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setPreview(generateImagePreview(filePath));

        avatarRepository.save(avatar);
        logger.info("Avatar saved for studentId: ", studentId);
    }

    public Avatar findAvatar(Long studentId) {
        logger.info("Method findAvatar invoked for studentId: ", studentId);
        return avatarRepository.findByStudentId(studentId).orElseThrow(() -> {
            logger.error("Avatar not found for studentId: ", studentId);
            return new NoSuchElementException("Avatar not found for studentId: " + studentId);
        });
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        logger.debug("Generating preview for file: ", filePath);

        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                logger.error("Failed to read image from file: ", filePath);
                throw new IOException("Failed to read image from file: " + filePath);
            }

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image, 0, 0, 100, height, null);
            graphics2D.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            logger.debug("Preview generated for file: ", filePath);

            return baos.toByteArray();
        }
    }

    private String getExtension(String fileName) {
        logger.debug("Extracting file extension for: ", fileName);

        if (fileName.lastIndexOf(".") == -1 || fileName.lastIndexOf(".") == fileName.length() - 1) {
            logger.error("Invalid file name: ", fileName);
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public byte[] getAvatarPreview(Long studentId) {
        logger.info("Method getAvatarPreview invoked for studentId: ", studentId);
        return findAvatar(studentId).getPreview();
    }

    public void avatarToResponse(Long studentId, OutputStream os) throws IOException {
        logger.info("Method avatarToResponse invoked for studentId: ", studentId);

        Avatar avatar = findAvatar(studentId);
        Path path = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream(path)) {
            is.transferTo(os);
            logger.info("Avatar streamed to response for studentId: ", studentId);
        }
    }

    public Page<Avatar> getAvatarsByPage(int page, int size) {
        logger.info("Method getAvatarsByPage invoked for page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        return avatarRepository.findAll(pageable);
    }
}
