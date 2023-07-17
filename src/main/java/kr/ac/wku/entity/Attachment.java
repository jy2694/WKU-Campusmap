package kr.ac.wku.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long articleId;

    @Column(nullable=false)
    private String fileName;

    @Column(nullable=false)
    private String fileExtension;

    @Column(nullable = false)
    private String originPath;

    @Column(nullable = false)
    private Long fileSize;

    @Builder
    public Attachment(Long articleId, String fileName, String fileExtension, String originPath, Long fileSize) {
        this.articleId = articleId;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.originPath = originPath;
        this.fileSize = fileSize;
    }
}
