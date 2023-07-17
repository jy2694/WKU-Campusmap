package kr.ac.wku.service;

import kr.ac.wku.configuration.StorageProperties;
import kr.ac.wku.dto.ArticleCreateDTO;
import kr.ac.wku.entity.Article;
import kr.ac.wku.entity.Attachment;
import kr.ac.wku.repository.ArticleRepository;
import kr.ac.wku.repository.AttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final AttachmentRepository attachmentRepository;
    private final StorageProperties storageProperties;

    public Article createArticle(ArticleCreateDTO dto){
        try{
            Article article = new Article(dto);
            articleRepository.save(article);
            if(dto.getFiles() != null) uploadAttachments(dto.getFiles(), article);
            return article;
        } catch(IOException e){
            return null;
        }
    }

    public Optional<Article> getArticle(Long id){
        return articleRepository.findById(id);
    }

    public List<Article> getArticles(String author){
        return articleRepository.findByAuthor(author);
    }

    public void deleteArticle(Long id){
        articleRepository.deleteById(id);
    }

    public boolean isAuthor(Long id, String author){
        Optional<Article> optionalArticle = articleRepository.findById(id);
        return optionalArticle.map(article -> article.getAuthor().equalsIgnoreCase(author)).orElse(false);
    }

    private void uploadAttachments(MultipartFile[] files, Article article) throws IOException {
        for(MultipartFile file : files){
            if(file.isEmpty()) continue;
            String fileName = file.getOriginalFilename();
            String fileExtension = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));
            String originPath = UUID.randomUUID().toString().replaceAll("-","") + fileExtension;
            Long fileSize = file.getSize();

            Path destinationFile = Paths.get(storageProperties.getLocation()).resolve(
                            Paths.get(originPath))
                    .normalize().toAbsolutePath();
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
            attachmentRepository.save(new Attachment(
                    article.getId(),
                    fileName,
                    fileExtension,
                    originPath,
                    fileSize
            ));
        }
    }

    public List<Attachment> findAttachmentByArticleId(Long articleId){
        return attachmentRepository.findByArticleId(articleId);
    }

    public Optional<Article> updateById(Long id, ArticleCreateDTO dto, String[] exists){
        Optional<Article> optionalArticle = articleRepository.findById(id);
        optionalArticle.ifPresent(article -> {
            if(dto.getTitle() != null){
                article.setTitle(dto.getTitle());
            }
            if(dto.getContent() != null){
                article.setContent(dto.getContent());
            }
            articleRepository.save(article);
        });

        List<Attachment> now = attachmentRepository.findByArticleId(id);
        for(Attachment attachment : now){
            if(exists != null){
                if(!isContained(exists, attachment))
                    attachmentRepository.deleteById(attachment.getId());
            } else attachmentRepository.deleteById(attachment.getId());
        }
        if(dto.getFiles() != null){
            if(optionalArticle.isEmpty()) return Optional.empty();
            try{
                uploadAttachments(dto.getFiles(), optionalArticle.get());
            } catch(IOException e){
                return Optional.empty();
            }
        }
        return optionalArticle;
    }

    public Optional<Attachment> findByOriginPath(String path){
        return attachmentRepository.findByOriginPath(path);
    }

    private boolean isContained(String[] exist, Attachment attachment){
        for(String line : exist)
            if(Integer.parseInt(line) == attachment.getId())
                return true;
        return false;
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = Paths.get(storageProperties.getLocation()).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
