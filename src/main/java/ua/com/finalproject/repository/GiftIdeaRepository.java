package ua.com.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.finalproject.entity.GiftIdea;

import java.util.List;

public interface GiftIdeaRepository extends JpaRepository<GiftIdea, Long> {
    List<GiftIdea> findByFriendId(Long id);

}
