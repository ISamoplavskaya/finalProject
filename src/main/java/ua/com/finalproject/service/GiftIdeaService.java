package ua.com.finalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.finalproject.entity.GiftIdea;
import ua.com.finalproject.exeption.NotFoundException;
import ua.com.finalproject.repository.GiftIdeaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftIdeaService {
    private final GiftIdeaRepository giftIdeaRepository;

    public GiftIdea save(GiftIdea giftIdea) {
        log.info("Saving gift idea: {}", giftIdea);
        return giftIdeaRepository.save(giftIdea);
    }

    public List<GiftIdea> getFriendGiftIdeas(Long friendId) {
        log.info("Getting gift ideas for friend with ID: {}", friendId);
        return giftIdeaRepository.findByFriendId(friendId);
    }

    public GiftIdea getById(Long giftIdeaId) {
        log.info("Getting gift idea by ID: {}", giftIdeaId);
        return giftIdeaRepository.findById(giftIdeaId)
                .orElseThrow(() -> new NotFoundException("Entity not found"));
    }

}
