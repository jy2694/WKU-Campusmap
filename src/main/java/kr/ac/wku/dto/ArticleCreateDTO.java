package kr.ac.wku.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ArticleCreateDTO {
    private String title;
    private String content;
    private String author;
    private MultipartFile[] files;
}
