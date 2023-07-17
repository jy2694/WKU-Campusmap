package kr.ac.wku.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.wku.configuration.SessionManager;
import kr.ac.wku.configuration.WonkwangAPI;
import kr.ac.wku.dto.ArticleCreateDTO;
import kr.ac.wku.entity.Article;
import kr.ac.wku.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@AllArgsConstructor
@RestController
public class ArticleController {

    private final SessionManager sessionManager;
    private final ArticleService articleService;
    private final WonkwangAPI wonkwangAPI;

    @PostMapping("/article/post")
    public ResponseEntity<Object> createArticle(ArticleCreateDTO dto, HttpServletRequest request) throws IOException {
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        dto.setAuthor(wonkwangAPI.getName(cookies) + "(" + wonkwangAPI.getStudentNumber(cookies) + ")");
        Article article = articleService.createArticle(dto);
        return ResponseEntity.ok().body(article);
    }

    @PostMapping("/article/delete")
    public ResponseEntity<Object> deleteArticle(Long id, HttpServletRequest request) throws IOException {
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        String author = wonkwangAPI.getName(cookies) + "(" + wonkwangAPI.getStudentNumber(cookies) + ")";
        if(!articleService.isAuthor(id, author)) return ResponseEntity.status(403).body("권한이 없습니다.");
        return ResponseEntity.accepted().body("삭제되었습니다.");
    }

    @GetMapping("/article/view")
    public ResponseEntity<Object> getArticle(Long id, HttpServletRequest request) throws IOException{
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        Optional<Article> optionalArticle = articleService.getArticle(id);
        return optionalArticle.<ResponseEntity<Object>>map(article ->
                ResponseEntity.ok().body(article)).orElseGet(() ->
                ResponseEntity.status(404).body("존재하지 않는 게시글입니다."));
    }
}
