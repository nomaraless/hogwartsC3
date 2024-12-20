package ru.hogwarts.school.model;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

@Entity
public class Avatar {
    @Id
    @GeneratedValue
    Long id;
    @Value(value = "${path.to.avatars.folder}")
    String filePath;
    Long fileSize;
    String mediaType;
    byte[] data;

    @OneToOne
    @JoinColumn(name = "student_id")
    Student student;

}


