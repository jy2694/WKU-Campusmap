package kr.ac.wku.entity;

import jakarta.persistence.*;
import kr.ac.wku.dto.ArticleCreateDTO;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate createAt;

    @Column(nullable = false)
    private String author;

    @Builder
    public Article(Long id, String title, String content, LocalDate createAt, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createAt = createAt;
        this.author = author;
    }

    public Article(ArticleCreateDTO dto){
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.author = dto.getAuthor();
        this.createAt = LocalDate.now();
    }
}
