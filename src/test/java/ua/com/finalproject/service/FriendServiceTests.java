package ua.com.finalproject.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.entity.GiftIdea;
import ua.com.finalproject.exeption.NotFoundException;
import ua.com.finalproject.exeption.RelatedResourceAccessException;
import ua.com.finalproject.repository.FriendRepository;
import ua.com.finalproject.repository.GiftIdeaRepository;
import ua.com.finalproject.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTests {
    @Mock
    private FriendRepository friendRepository;
    @Mock
    private GiftIdeaRepository giftIdeaRepository;
    @InjectMocks
    private FriendService friendService;
    private Friend friend;

    @BeforeEach
    public void setUp() {
        friend = ObjectUtils.getFriend("friendName", null);
    }

    @Test
    public void testSaveFriend_Successfully() {
        when(friendRepository.save(friend)).thenReturn(friend);

        Friend savedFriend = friendService.save(friend);

        assertNotNull(savedFriend);
        assertEquals(friend, savedFriend);
    }

    @Test
    public void testGetUserFriends_ReturnsEmptyListWhenNoFriendsFound() {
        String username = "testUser";
        when(friendRepository.findByUserUsername(username)).thenReturn(new ArrayList<>());

        List<Friend> friends = friendService.getUserFriends(username);

        assertEquals(0, friends.size());
    }

    @Test
    public void testGetUserFriends_ReturnsListOfFriendsWhenFound() {
        String username = "testUser";
        Friend friend2 = ObjectUtils.getFriend("friendName2", null);
        List<Friend> expectedFriends = new ArrayList<>();
        expectedFriends.add(friend);
        expectedFriends.add(friend2);
        when(friendRepository.findByUserUsername(username)).thenReturn(expectedFriends);

        List<Friend> actualFriends = friendService.getUserFriends(username);

        assertEquals(expectedFriends.size(), actualFriends.size());
        assertEquals(expectedFriends.get(0).getName(), actualFriends.get(0).getName());
        assertEquals(expectedFriends.get(1).getName(), actualFriends.get(1).getName());
    }

    @Test
    public void testGetById_ReturnsFriendWhenFound() {
        Long friendId = 1L;
        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));

        Friend actualFriend = friendService.getById(friendId);

        assertEquals(friend, actualFriend);
    }

    @Test
    public void testGetById_ThrowsNotFoundExceptionWhenNotFound() {
        Long friendId = 1L;
        when(friendRepository.findById(friendId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> friendService.getById(friendId));
    }

    @Test
    public void testAddFriendGiftIdea_Successfully() {
        Long friendId = 1L;
        friend.setId(friendId);
        GiftIdea giftIdea = ObjectUtils.getGiftIdea("giftName", null);
        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));

        friendService.addFriendGiftIdea(friendId, giftIdea);

        assertTrue(friend.getGiftIdeas().contains(giftIdea));
        verify(friendRepository, times(1)).findById(friendId);
        verify(friendRepository, times(1)).save(friend);
    }

    @Test
    public void testRemoveGiftIdea_Successfully() {
        Long friendId = 1L;
        Long giftIdeaId = 2L;
        GiftIdea giftIdea = ObjectUtils.getGiftIdea("giftName", null);

        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(giftIdeaRepository.findById(giftIdeaId)).thenReturn(Optional.of(giftIdea));

        friend.addGiftIdea(giftIdea);
        friendService.removeGiftIdea(friendId, giftIdeaId);

        assertFalse(friend.getGiftIdeas().contains(giftIdea));
        verify(friendRepository, times(1)).findById(friendId);
        verify(giftIdeaRepository, times(1)).findById(giftIdeaId);
        verify(giftIdeaRepository, times(1)).delete(giftIdea);
    }

    @Test
    public void testRemoveGiftIdea_GiftIdeaNotFound() {
        Long friendId = 1L;
        Long giftIdeaId = 2L;

        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(giftIdeaRepository.findById(giftIdeaId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> friendService.removeGiftIdea(friendId, giftIdeaId));

        verify(friendRepository, times(1)).findById(friendId);
        verify(giftIdeaRepository, times(1)).findById(giftIdeaId);
        verify(giftIdeaRepository, never()).delete(any());
    }

    @Test
    public void testRemoveGiftIdea_NotYourGiftIdea() {
        Long friendId = 1L;
        Long giftIdeaId = 2L;

        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(giftIdeaRepository.findById(giftIdeaId)).thenReturn(Optional.of(new GiftIdea()));

        Assertions.assertThrows(RelatedResourceAccessException.class, () -> friendService.removeGiftIdea(friendId, giftIdeaId));
        verify(friendRepository, times(1)).findById(friendId);
        verify(giftIdeaRepository, times(1)).findById(giftIdeaId);
        verify(giftIdeaRepository, never()).delete(any());
    }

    @Test
    public void testGetFriendsWithBirthdayTomorrowForUser() {
        String username = "testUser";
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Friend> friends = new ArrayList<>();
        friend.setBirthday(tomorrow);
        friends.add(friend);

        when(friendRepository.findFriendsWithBirthdayTomorrowForUser(username, tomorrow)).thenReturn(friends);

        List<Friend> result = friendService.getFriendsWithBirthdayTomorrowForUser(username);

        assertEquals(1, result.size());
        assertEquals(friend, result.get(0));
    }

    @Test
    public void testGetFriendsWithBirthdayTomorrowForUser_NoFriends() {
        String username = "testUser";
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        when(friendRepository.findFriendsWithBirthdayTomorrowForUser(username, tomorrow)).thenReturn(new ArrayList<>());

        List<Friend> result = friendService.getFriendsWithBirthdayTomorrowForUser(username);

        assertEquals(0, result.size());
    }
}
