package ua.com.finalproject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class FriendRepositoryTests {
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    UserRepository userRepository;
    private User user;
    private Friend friend1;
    private Friend friend2;

    @BeforeEach
    public void setUp() {
        user = ObjectUtils.getUser("user1");
        userRepository.save(user);
        friend1 = ObjectUtils.getFriend("friend1", user);
        friend2 = ObjectUtils.getFriend("friend2", user);
        friendRepository.save(friend1);
        friendRepository.save(friend2);

    }

    @Test
    public void friendRepository_findByUserUsername() {
        List<Friend> friends = friendRepository.findByUserUsername(user.getUsername());

        assertEquals(2, friends.size());
    }

    @Test
    public void friendRepository_findFriendsWithBirthdayTomorrowForUser() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        friend1.setBirthday(tomorrow);
        friend2.setBirthday(tomorrow.plusDays(1));

        List<Friend> friendsWithBirthdayTomorrow = friendRepository.findFriendsWithBirthdayTomorrowForUser(user.getUsername(), tomorrow);

        assertEquals(1, friendsWithBirthdayTomorrow.size());
        assertEquals(friend1.getBirthday(), friendsWithBirthdayTomorrow.get(0).getBirthday());
    }
}
