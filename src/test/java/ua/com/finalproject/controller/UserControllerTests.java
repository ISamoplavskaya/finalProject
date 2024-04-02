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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.finalproject.dto.UserDto;
import ua.com.finalproject.dto.UserUpdateRequest;
import ua.com.finalproject.entity.Role;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.service.JwtService;
import ua.com.finalproject.service.UserService;
import ua.com.finalproject.util.ObjectUtils;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser")
    public void testGetCurrentUser_ReturnsCurrentUserDetails() throws Exception {
        User user = ObjectUtils.getUser("testUser");
        UserDto userDTO = new UserDto(1L,"testUser", "test@example.com", Role.ROLE_USER);

        when(userService.getByUsername(any())).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDTO);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdateUser_SuccessfulUpdate_ReturnsOk() throws Exception {
        String username = "testUser";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";
        String email = "testUser@example.com";
        UserUpdateRequest updateRequest = new UserUpdateRequest(username, email, newPassword, confirmPassword);

        User existingUser = ObjectUtils.getUser(username);

        when(userService.getByUsername(username)).thenReturn(existingUser);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User updated successfully."));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdateUser_PasswordsMismatch_ReturnsBadRequest() throws Exception {
        String username = "testUser";
        User existingUser = ObjectUtils.getUser(username);
        UserUpdateRequest updateRequest = new UserUpdateRequest("testUser", "testUser@example.com", "newPassword", "differentPassword");

        when(userService.getByUsername(username)).thenReturn(existingUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Password confirmation does not match"));
    }
}

