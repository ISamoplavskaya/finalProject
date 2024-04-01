package ua.com.finalproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.entity.Role;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.exeption.AlreadyExistsException;
import ua.com.finalproject.exeption.NotFoundException;
import ua.com.finalproject.exeption.RelatedResourceAccessException;
import ua.com.finalproject.repository.FriendRepository;
import ua.com.finalproject.repository.UserRepository;
import ua.com.finalproject.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendRepository friendRepository;
    @InjectMocks
    private UserService userService;
    private User user;

    @BeforeEach
    public void setUp() {
        user = ObjectUtils.getUser("user1");
    }

    @Test
    public void testSaveUser_Success() {
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals(user, savedUser);
    }

    @Test
    public void testCreateUser_Success() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.create(user);

        assertNotNull(savedUser);
        assertEquals(user, savedUser);
    }

    @Test
    public void testCreateUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> userService.create(user));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> userService.create(user));
    }

    @Test
    public void testGetByUsername_UserExists_Success() {
        String username = "existingUser";
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User foundUser = userService.getByUsername(username);

        assertNotNull(foundUser);
        assertEquals(user, foundUser);
    }

    @Test
    public void testGetByUsername_UserNotFound() {
        String username = "nonExistingUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getByUsername(username));
    }

    @Test
    public void testGetAllUsers() {
        User user2 = ObjectUtils.getUser("user2");
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
    }

    @Test
    public void testGetById_UserExists_Success() {
        Long id = 1L;
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User foundUser = userService.getById(id);

        assertNotNull(foundUser);
        assertEquals(user, foundUser);
    }

    @Test
    public void testGetById_UserNotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(id));
    }

    @Test
    public void testGetPremium_Success() {
        Long id = 1L;
        String role="premium";
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.changeRole(id,role);

        assertEquals(Role.ROLE_PREMIUM, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testAddUserFriend_Success() {
        String username = user.getUsername();
        Friend friend = new Friend();
        friend.setName("Test Friend");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userService.addUserFriend(username, friend);

        assertTrue(user.getFriends().contains(friend));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testAddUserFriend_FriendAlreadyExists() {
        String username = user.getUsername();
        Friend existingFriend = ObjectUtils.getFriend("Existing Friend", user);
        user.addFriend(existingFriend);
        Friend newFriend = ObjectUtils.getFriend("Existing Friend", null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistsException.class, () -> {
            userService.addUserFriend(username, newFriend);
        });
        assertFalse(user.getFriends().contains(newFriend));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(user);

    }

    @Test
    public void testRemoveFriend_Success() {
        String username = "testUser";
        Long friendId = 1L;
        User user = new User();
        user.setUsername(username);
        Friend friend = new Friend();
        friend.setId(friendId);
        user.addFriend(friend);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));

        userService.removeFriend(username, friendId);

        assertFalse(user.getFriends().contains(friend));
        verify(userRepository, times(1)).findByUsername(username);
        verify(friendRepository, times(1)).findById(friendId);
        verify(friendRepository, times(1)).delete(friend);
    }

    @Test
    public void testRemoveFriend_FriendNotFound() {
        String username = "testUser";
        Long friendId = 1L;
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(friendRepository.findById(friendId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.removeFriend(username, friendId));
        verify(userRepository, times(1)).findByUsername(username);
        verify(friendRepository, times(1)).findById(friendId);
        verify(friendRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void testRemoveFriend_NotYourFriend() {
        String username = "testUser";
        Long friendId = 1L;
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(friendRepository.findById(friendId)).thenReturn(Optional.of(new Friend()));

        assertThrows(RelatedResourceAccessException.class, () -> userService.removeFriend(username, friendId));
        verify(userRepository, times(1)).findByUsername(username);
        verify(friendRepository, times(1)).findById(friendId);
        verify(friendRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void testDelete() {
        user.setId(1L);

        userService.delete(user);

        verify(userRepository, times(1)).delete(user);
    }
}
