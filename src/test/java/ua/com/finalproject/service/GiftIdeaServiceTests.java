package ua.com.finalproject.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.finalproject.entity.GiftIdea;
import ua.com.finalproject.exeption.NotFoundException;
import ua.com.finalproject.repository.GiftIdeaRepository;
import ua.com.finalproject.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftIdeaServiceTests {
    @Mock
    private GiftIdeaRepository giftIdeaRepository;
    @InjectMocks
    private GiftIdeaService giftIdeaService;

    @Test
    void testSaveGiftIdea() {
        GiftIdea giftIdea = ObjectUtils.getGiftIdea("giftName", null);
        when(giftIdeaRepository.save(giftIdea)).thenReturn(giftIdea);

        GiftIdea savedGiftIdea = giftIdeaService.save(giftIdea);

        assertEquals(giftIdea, savedGiftIdea);
        verify(giftIdeaRepository, times(1)).save(giftIdea);
    }

    @Test
    void testGetFriendGiftIdeas() {
        Long friendId = 1L;
        List<GiftIdea> expectedGiftIdeas = new ArrayList<>();
        when(giftIdeaRepository.findByFriendId(friendId)).thenReturn(expectedGiftIdeas);

        List<GiftIdea> actualGiftIdeas = giftIdeaService.getFriendGiftIdeas(friendId);

        assertEquals(expectedGiftIdeas, actualGiftIdeas);
        verify(giftIdeaRepository, times(1)).findByFriendId(friendId);
    }

    @Test
    void testGetByIdWithExistingId() {
        Long giftIdeaId = 1L;
        GiftIdea expectedGiftIdea = new GiftIdea();
        when(giftIdeaRepository.findById(giftIdeaId)).thenReturn(Optional.of(expectedGiftIdea));

        GiftIdea actualGiftIdea = giftIdeaService.getById(giftIdeaId);

        assertEquals(expectedGiftIdea, actualGiftIdea);
        verify(giftIdeaRepository, times(1)).findById(giftIdeaId);
    }

    @Test
    void testGetByIdWithNonExistingId() {
        Long nonExistingId = 999L;
        when(giftIdeaRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> giftIdeaService.getById(nonExistingId));
        verify(giftIdeaRepository, times(1)).findById(nonExistingId);
    }
}
