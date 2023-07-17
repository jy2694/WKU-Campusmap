package kr.ac.wku.service;

import kr.ac.wku.dto.ArticleCreateDTO;
import kr.ac.wku.entity.Article;
import kr.ac.wku.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public Article createArticle(ArticleCreateDTO dto){
        Article article = new Article(dto);
        articleRepository.save(article);
        return article;
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
}
