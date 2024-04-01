package ua.com.finalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.com.finalproject.dto.FriendDTO;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.service.FriendService;
import ua.com.finalproject.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
@Slf4j
public class FriendController {
    private final FriendService friendService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Object> getUserFriends(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Retrieving friends for user: {}", userDetails.getUsername());
        List<Friend> friends = friendService.getUserFriends(userDetails.getUsername());
        List<FriendDTO> friendsDTO = friends.stream()
                .map(friend -> modelMapper.map(friend, FriendDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(friendsDTO);
    }

    @PostMapping
    public ResponseEntity<Object> addFriend(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid FriendDTO friendDTO) {
        log.info("Adding friend for user: {}", userDetails.getUsername());
        Friend friend = modelMapper.map(friendDTO, Friend.class);
        userService.addUserFriend(userDetails.getUsername(), friend);
        return ResponseEntity.ok("Friend added successfully");
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<FriendDTO> updateFriend(@PathVariable Long friendId, @RequestBody @Valid FriendDTO updatedFriendDTO) {
        log.info("Updating friend with ID: {}", friendId);
        Friend friend = friendService.getById(friendId);
        updatedFriendDTO.setId(friendId);
        modelMapper.map(updatedFriendDTO, friend);
        friendService.save(friend);
        return ResponseEntity.ok(modelMapper.map(friend, FriendDTO.class));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Object> deleteFriend(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long friendId) {
        log.info("Deleting friend with ID: {} for user: {}", friendId, userDetails.getUsername());
        userService.removeFriend(userDetails.getUsername(), friendId);
        return ResponseEntity.ok("Friend deleted successfully");
    }
}