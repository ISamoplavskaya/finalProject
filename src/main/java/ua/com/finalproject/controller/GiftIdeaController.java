package ua.com.finalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.finalproject.dto.GiftIdeaDTO;
import ua.com.finalproject.entity.GiftIdea;
import ua.com.finalproject.service.FriendService;
import ua.com.finalproject.service.GiftIdeaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends/{friendId}/giftIdeas")
@Slf4j
public class GiftIdeaController {
    private final FriendService friendService;
    private final GiftIdeaService giftIdeaService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Object> getFriendGiftIdea(@PathVariable Long friendId) {
        log.info("Getting gift ideas for friend with id {}", friendId);
        List<GiftIdea> giftIdeas = giftIdeaService.getFriendGiftIdeas(friendId);
        List<GiftIdeaDTO> giftIdeasDTO = giftIdeas.stream()
                .map(giftIdea -> modelMapper.map(giftIdea, GiftIdeaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(giftIdeasDTO);
    }

    @PostMapping
    public ResponseEntity<Object> addGiftIdea(@PathVariable Long friendId, @RequestBody @Valid GiftIdeaDTO giftIdeaDTO) {
        GiftIdea giftIdea = modelMapper.map(giftIdeaDTO, GiftIdea.class);
        friendService.addFriendGiftIdea(friendId, giftIdea);
        return ResponseEntity.ok("Gift added successfully");
    }

    @PutMapping("/{giftIdeaId}")
    public ResponseEntity<GiftIdeaDTO> updateGiftIdea(@PathVariable Long friendId, @PathVariable Long giftIdeaId, @RequestBody @Valid GiftIdeaDTO updatedGiftIdeaDTO) {
        log.info("Adding gift idea for friend with id {}", friendId);
        GiftIdea giftIdea = giftIdeaService.getById(giftIdeaId);
        updatedGiftIdeaDTO.setId(giftIdeaId);
        modelMapper.map(updatedGiftIdeaDTO, giftIdea);
        giftIdeaService.save(giftIdea);
        return ResponseEntity.ok(modelMapper.map(giftIdea, GiftIdeaDTO.class));
    }

    @DeleteMapping("/{giftIdeaId}")
    public ResponseEntity<Object> deleteGiftIdea(@PathVariable Long friendId, @PathVariable @Valid Long giftIdeaId) {
        log.info("Deleting gift idea with id {}", giftIdeaId);
        friendService.removeGiftIdea(friendId, giftIdeaId);
        return ResponseEntity.ok("Gift idea deleted successfully");
    }
}
