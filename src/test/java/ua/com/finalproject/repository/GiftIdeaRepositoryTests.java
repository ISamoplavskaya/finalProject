package ua.com.finalproject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.entity.GiftIdea;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.util.ObjectUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class GiftIdeaRepositoryTests {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    GiftIdeaRepository giftIdeaRepository;
    private User user;
    private Friend friend;

    @BeforeEach
    public void setUp() {
        user = ObjectUtils.getUser("user1");
        userRepository.save(user);
        friend = ObjectUtils.getFriend("friend1", user);
        friendRepository.save(friend);
    }

    @Test
    public void giftIdeaRepository_findByFriendId() {
        GiftIdea giftIdea1 = ObjectUtils.getGiftIdea("Gift idea 1", friend);
        GiftIdea giftIdea2 = ObjectUtils.getGiftIdea("Gift idea 2", friend);
        giftIdeaRepository.save(giftIdea1);
        giftIdeaRepository.save(giftIdea2);

        List<GiftIdea> foundGiftIdeas = giftIdeaRepository.findByFriendId(friend.getId());

        assertEquals(2, foundGiftIdeas.size());

        assertTrue(foundGiftIdeas.stream().allMatch(giftIdea -> giftIdea.getFriend().getId().equals(friend.getId())));
    }

}
