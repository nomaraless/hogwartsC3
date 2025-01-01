package ru.hogwarts.school.model;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Objects;

@Entity
public class Avatar {
    @Id
    @GeneratedValue
    private long id;
    @Value(value = "${path.to.avatars.folder}")
    private String filePath;
    private long fileSize;
   private String mediaType;
   @Lob
    private byte[] preview;

    @OneToOne
    @JoinColumn(name = "student_id")
    Student student;

    public Avatar(long id, String filePath, long fileSize, String mediaType, byte[] preview, Student student) {
        this.id = id;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
        this.preview = preview;
        this.student = student;
    }

    public Avatar() {

    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getPreview() {
        return preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Avatar avatar = (Avatar) object;
        return getId() == avatar.getId() && getFileSize() == avatar.getFileSize() && Objects.equals(getFilePath(), avatar.getFilePath()) && Objects.equals(getMediaType(), avatar.getMediaType()) && Arrays.equals(getPreview(), avatar.getPreview()) && Objects.equals(getStudent(), avatar.getStudent());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getFilePath(), getFileSize(), getMediaType(), getStudent());
        result = 31 * result + Arrays.hashCode(getPreview());
        return result;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                ", preview=" + Arrays.toString(preview) +
                ", student=" + student +
                '}';
    }
}


