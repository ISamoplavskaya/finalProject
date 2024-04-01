package ua.com.finalproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.com.finalproject.dto.GiftIdeaDTO;
import ua.com.finalproject.entity.GiftIdea;
import ua.com.finalproject.service.FriendService;
import ua.com.finalproject.service.GiftIdeaService;
import ua.com.finalproject.service.JwtService;
import ua.com.finalproject.service.UserService;
import ua.com.finalproject.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GiftIdeaController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class GiftIdeaControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GiftIdeaService giftIdeaService;
    @MockBean
    private FriendService friendService;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetFriendGiftIdea_ReturnsAllGiftIdeas() throws Exception {
        Long friendId = 1L;
        List<GiftIdea> giftIdeas = Arrays.asList(new GiftIdea(), new GiftIdea());
        List<GiftIdeaDTO> giftIdeaDTOS = Arrays.asList(new GiftIdeaDTO(), new GiftIdeaDTO());
        when(giftIdeaService.getFriendGiftIdeas(friendId)).thenReturn(giftIdeas);
        for (int i = 0; i < giftIdeas.size(); i++) {
            when(modelMapper.map(giftIdeas.get(i), GiftIdeaDTO.class)).thenReturn(giftIdeaDTOS.get(i));
        }
        mockMvc.perform(get("/api/v1/friends/{friendId}/giftIdeas", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(giftIdeaDTOS.size()));
    }

    @Test
    public void testAddGiftIdea() throws Exception {
        Long friendId = 1L;
        GiftIdeaDTO giftIdeaDTO = new GiftIdeaDTO();
        giftIdeaDTO.setGiftName("gift1");
        GiftIdea giftIdea = ObjectUtils.getGiftIdea("gift1", null);

        when(modelMapper.map(giftIdeaDTO, GiftIdea.class)).thenReturn(giftIdea);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/friends/{friendId}/giftIdeas", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(giftIdeaDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Gift added successfully"));
    }

    @Test
    public void testUpdateGiftIdea() throws Exception {
        Long friendId = 1L;
        Long giftIdeaId = 2L;
        GiftIdeaDTO updatedGiftIdeaDto = new GiftIdeaDTO();
        updatedGiftIdeaDto.setGiftName("giftIdea1");

        GiftIdea giftIdea = ObjectUtils.getGiftIdea("giftIdea2", null);
        giftIdea.setId(giftIdeaId);
        when(giftIdeaService.getById(giftIdeaId)).thenReturn(giftIdea);
        when(modelMapper.map(giftIdea, GiftIdeaDTO.class)).thenReturn(updatedGiftIdeaDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/friends/{friendId}/giftIdeas/{giftIdeaId}", friendId, giftIdeaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedGiftIdeaDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.giftName").value(updatedGiftIdeaDto.getGiftName()));
    }

    @Test
    void testDeleteFriend() throws Exception {
        Long friendId = 1L;
        Long giftIdeaId = 2L;
        mockMvc.perform(delete("/api/v1/friends/{friendId}/giftIdeas/{giftIdeaId}", friendId, giftIdeaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Gift idea deleted successfully"));
    }
}
