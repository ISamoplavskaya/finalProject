package ua.com.finalproject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.util.ObjectUtils;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    public void setUp() {
        user = ObjectUtils.getUser("user1");
        userRepository.save(user);
    }

    @Test
    @Transactional
    public void UserRepository_findByUsername() {
        User foundUser = userRepository.findByUsername(user.getUsername()).orElse(null);

        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
    }

    @Test
    @Transactional
    public void UserRepository_existsByUsername() {
        String existingUsername = user.getUsername();
        String nonExistingUsername = "user2";

        boolean isExist = userRepository.existsByUsername(existingUsername);
        boolean nonExist = userRepository.existsByUsername(nonExistingUsername);

        assertTrue(isExist);
        assertFalse(nonExist);
    }

    @Test
    @Transactional
    public void UserRepository_existsByEmail() {
        String existingEmail = user.getEmail();
        String nonExistingEmail = "user2@gmail.com";

        boolean isExist = userRepository.existsByEmail(existingEmail);
        boolean nonExist = userRepository.existsByEmail(nonExistingEmail);

        assertTrue(isExist);
        assertFalse(nonExist);
    }
}
