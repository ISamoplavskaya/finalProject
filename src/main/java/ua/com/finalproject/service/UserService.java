package ua.com.finalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.entity.Role;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.exeption.AlreadyExistsException;
import ua.com.finalproject.exeption.NotFoundException;
import ua.com.finalproject.exeption.RelatedResourceAccessException;
import ua.com.finalproject.repository.FriendRepository;
import ua.com.finalproject.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public User save(User user) {
        log.info("Saving user: {}", user);
        return userRepository.save(user);
    }

    public User create(User user) {
        log.info("Creating user: {}", user);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AlreadyExistsException("A user with the specified username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistsException("A user with the specified email already exists");
        }
        return save(user);
    }

    public User getByUsername(String username) {
        log.info("Getting user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

    }

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    public UserDetailsService userDetailsService() {
        log.info("Getting user details service");
        return this::getByUsername;
    }

    public User getById(Long id) {
        log.info("Getting user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void changeRole(Long id, String role) {
        log.info("Setting user with ID {} to role {}", id, role);
        var user = getById(id);
        if (role.equalsIgnoreCase("premium")) {
            user.setRole(Role.ROLE_PREMIUM);
        } else if (role.equalsIgnoreCase("user")) {
            user.setRole(Role.ROLE_USER);
        } else {
            throw new IllegalArgumentException("Invalid role value. Valid values: premium, user");
        }
        save(user);
    }

    @Transactional
    public void addUserFriend(String username, Friend friend) {
        log.info("Adding friend {} for user {}", friend, username);
        User user = getByUsername(username);
        if (user.getFriends().stream().noneMatch(existingFriend -> existingFriend.getName().equals(friend.getName()))) {
            user.addFriend(friend);
            save(user);
        } else {
            throw new AlreadyExistsException("Friend with the same name already exists for this user");
        }
    }

    @Transactional
    public void removeFriend(String username, Long friendId) {
        log.info("Removing friend with ID {} for user {}", friendId, username);
        User user = getByUsername(username);
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new NotFoundException("Friend not found"));
        if (!user.getFriends().contains(friend)) {
            throw new RelatedResourceAccessException("Not your friend");
        }
        user.removeFriend(friend);
        friendRepository.delete(friend);
    }

    public void delete(User user) {
        log.info("Deleting user: {}", user);
        userRepository.delete(user);
    }

}
