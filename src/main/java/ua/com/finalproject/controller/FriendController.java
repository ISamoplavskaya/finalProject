package ua.com.finalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.com.finalproject.dto.FriendDto;
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
        List<FriendDto> friendsDto = friends.stream()
                .map(friend -> modelMapper.map(friend, FriendDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(friendsDto);
    }

    @PostMapping
    public ResponseEntity<Object> addFriend(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid FriendDto friendDto) {
        log.info("Adding friend for user: {}", userDetails.getUsername());
        Friend friend = modelMapper.map(friendDto, Friend.class);
        userService.addUserFriend(userDetails.getUsername(), friend);
        return ResponseEntity.ok("Friend added successfully");
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<FriendDto> updateFriend(@PathVariable Long friendId, @RequestBody @Valid FriendDto updatedFriendDto) {
        log.info("Updating friend with ID: {}", friendId);
        Friend friend = friendService.getById(friendId);
        updatedFriendDto.setId(friendId);
        modelMapper.map(updatedFriendDto, friend);
        friendService.save(friend);
        return ResponseEntity.ok(modelMapper.map(friend, FriendDto.class));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Object> deleteFriend(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long friendId) {
        log.info("Deleting friend with ID: {} for user: {}", friendId, userDetails.getUsername());
        userService.removeFriend(userDetails.getUsername(), friendId);
        return ResponseEntity.ok("Friend deleted successfully");
    }
}