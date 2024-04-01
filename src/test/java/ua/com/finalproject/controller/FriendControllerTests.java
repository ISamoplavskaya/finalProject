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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.com.finalproject.dto.FriendDTO;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.service.FriendService;
import ua.com.finalproject.service.JwtService;
import ua.com.finalproject.service.UserService;
import ua.com.finalproject.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class FriendControllerTests {
    @Autowired
    private MockMvc mockMvc;
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
    @WithMockUser(username = "testUser")
    public void testGetUserFriends_ReturnsAllFriends() throws Exception {
        List<Friend> friends = Arrays.asList(new Friend(), new Friend());
        List<FriendDTO> friendDTOS = Arrays.asList(new FriendDTO(), new FriendDTO());
        when(friendService.getUserFriends(any())).thenReturn(friends);
        for (int i = 0; i < friends.size(); i++) {
            when(modelMapper.map(friends.get(i), FriendDTO.class)).thenReturn(friendDTOS.get(i));
        }
        mockMvc.perform(get("/api/v1/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(friendDTOS.size()));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testAddFriend() throws Exception {
        FriendDTO friendDTO = new FriendDTO();
        friendDTO.setName("friend1");
        Friend friend = ObjectUtils.getFriend("friend1", null);
        when(modelMapper.map(friendDTO, Friend.class)).thenReturn(friend);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend added successfully"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdateFriend() throws Exception {
        Long friendId = 1L;
        FriendDTO updatedFriendDTO = new FriendDTO();
        updatedFriendDTO.setName("friend2");

        Friend friend = ObjectUtils.getFriend("friend1", null);
        friend.setId(friendId);
        when(friendService.getById(friendId)).thenReturn(friend);
        when(modelMapper.map(friend, FriendDTO.class)).thenReturn(updatedFriendDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/friends/{friendId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFriendDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(updatedFriendDTO.getName()));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testDeleteFriend() throws Exception {
        Long friendId = 1L;
        mockMvc.perform(delete("/api/v1/friends/{friendId}", friendId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend deleted successfully"));
    }

}
