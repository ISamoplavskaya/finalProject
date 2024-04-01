package ua.com.finalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.entity.GiftIdea;
import ua.com.finalproject.exeption.NotFoundException;
import ua.com.finalproject.exeption.RelatedResourceAccessException;
import ua.com.finalproject.repository.FriendRepository;
import ua.com.finalproject.repository.GiftIdeaRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {
    private final FriendRepository friendRepository;
    private final GiftIdeaRepository giftIdeaRepository;

    public Friend save(Friend friend) {
        log.info("Saving friend: {}", friend);
        return friendRepository.save(friend);
    }

    public List<Friend> getUserFriends(String username) {
        log.info("Getting friends for user: {}", username);
        return friendRepository.findByUserUsername(username);
    }

    public Friend getById(Long friendId) {
        log.info("Getting friend by ID: {}", friendId);
        return friendRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found"));
    }

    @Transactional
    public void addFriendGiftIdea(Long friendId, GiftIdea giftIdea) {
        log.info("Adding gift idea to friend with ID {}: {}", friendId, giftIdea);
        Friend friend = getById(friendId);
        friend.addGiftIdea(giftIdea);
        save(friend);
    }

    @Transactional
    public void removeGiftIdea(Long friendId, Long giftIdeaId) {
        log.info("Removing gift idea with ID {} from friend with ID {}", giftIdeaId, friendId);
        Friend friend = getById(friendId);
        GiftIdea giftIdea = giftIdeaRepository.findById(giftIdeaId).orElseThrow(() -> new NotFoundException("Gift idea not found"));
        if (!friend.getGiftIdeas().contains(giftIdea)) {
            throw new RelatedResourceAccessException("Not your giftIdea");
        }
        friend.removeGiftIdea(giftIdea);
        giftIdeaRepository.delete(giftIdea);
    }

    public List<Friend> getFriendsWithBirthdayTomorrowForUser(String username) {
        log.info("Getting friends with birthday tomorrow for user: {}", username);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return friendRepository.findFriendsWithBirthdayTomorrowForUser(username, tomorrow);
    }


}
