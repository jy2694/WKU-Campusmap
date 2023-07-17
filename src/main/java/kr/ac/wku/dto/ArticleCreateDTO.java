package kr.ac.wku.dto;

import lombok.Data;

@Data
public class ArticleCreateDTO {
    private String title;
    private String content;
    private String author;
}
