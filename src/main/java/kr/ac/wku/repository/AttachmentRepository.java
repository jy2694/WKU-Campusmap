package kr.ac.wku.repository;

import kr.ac.wku.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findByOriginPath(String originPath);

    List<Attachment> findByArticleId(Long articleId);
}
