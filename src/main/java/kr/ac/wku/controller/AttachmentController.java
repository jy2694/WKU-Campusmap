package kr.ac.wku.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.wku.entity.Attachment;
import kr.ac.wku.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@AllArgsConstructor
@RestController
public class AttachmentController {

    private final ArticleService articleService;

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request, @PathVariable String filename) {
        Optional<Attachment> optionalAttachment = articleService.findByOriginPath(filename);
        if(optionalAttachment.isEmpty()) return ResponseEntity.notFound().build();
        Attachment attachments = optionalAttachment.get();
        Resource file = articleService.loadAsResource(filename);
        String userAgent = request.getHeader("User-Agent");
        if(userAgent.contains("Trident"))
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + URLEncoder.encode(attachments.getFileName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20") + "\"").body(file);
        else
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + new String(attachments.getFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"").body(file);
    }

}
