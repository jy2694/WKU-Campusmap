package kr.ac.wku.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.wku.configuration.SessionManager;
import kr.ac.wku.configuration.WonkwangAPI;
import kr.ac.wku.dto.ArticleCreateDTO;
import kr.ac.wku.entity.Article;
import kr.ac.wku.entity.Attachment;
import kr.ac.wku.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
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

    @PostMapping("/article/update")
    public ResponseEntity<Object> updateArticle(Long id, ArticleCreateDTO dto, String[] exist){
        Optional<Article> optionalArticle = articleService.getArticle(id);
        if(optionalArticle.isEmpty()) return ResponseEntity.notFound().build();
        Optional<Article> updated = articleService.updateById(id, dto, exist);
        return updated.<ResponseEntity<Object>>map(article ->
                ResponseEntity.ok().body(article))
                .orElseGet(() ->
                        ResponseEntity.internalServerError().body("게시글을 업로드 하는 중 오류가 발생했습니다."));
    }

    @PostMapping("/article/delete")
    public ResponseEntity<Object> deleteArticle(Long id, HttpServletRequest request) throws IOException {
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        String author = wonkwangAPI.getName(cookies) + "(" + wonkwangAPI.getStudentNumber(cookies) + ")";
        if(!articleService.isAuthor(id, author)) return ResponseEntity.status(403).body("권한이 없습니다.");
        return ResponseEntity.accepted().body("삭제되었습니다.");
    }

    @PostMapping("/article/attachment")
    public ResponseEntity<Object> getAttachment(Long id, HttpServletRequest request){
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        List<Attachment> attachmentList = articleService.findAttachmentByArticleId(id);
        return ResponseEntity.ok().body(attachmentList.toArray(Attachment[]::new));
    }

    @GetMapping("/article/view")
    public ResponseEntity<Object> getArticle(Long id, HttpServletRequest request) throws IOException{
        String cookies = (String) sessionManager.getSession(request);
        if(cookies == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        Optional<Article> optionalArticle = articleService.getArticle(id);
        return optionalArticle.<ResponseEntity<Object>>map(article ->
                ResponseEntity.ok().body(article)).orElseGet(() ->
                ResponseEntity.notFound().build());
    }
}
