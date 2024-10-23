package ru.andreyszdlv.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controller.FriendResponseDTO;
import ru.andreyszdlv.userservice.service.FriendService;
import ru.andreyszdlv.userservice.service.LocalizationService;
import ru.andreyszdlv.userservice.service.TempFriendService;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/friends")
@Slf4j
public class FriendController {

    private final LocalizationService localizationService;

    private final TempFriendService tempFriendService;

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<FriendResponseDTO>> getFriends(@RequestHeader("X-User-Id") long userId){
        log.info("Executing getFriends for userId: {}", userId);
        return ResponseEntity.ok(friendService.getFriendsByUserId(userId));
    }

    @PostMapping("/create-request/{friendId}")
    public ResponseEntity<String> createRequestFriend(@RequestHeader("X-User-Id") long userId,
                                                      @PathVariable long friendId){
        log.info("Executing createRequestFriend for userId: {}, friendId: {}", userId, friendId);
        Locale locale = Locale.getDefault();

        log.info("Creating request friend for userId: {}, friendId: {}", userId, friendId);
        tempFriendService.createRequestFriend(userId, friendId);

        log.info("Successfully created request for userId: {}, friendId: {}", userId, friendId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                                "message.ok.create_request_friend",
                                locale
                        )
                );
    }

    @PostMapping("/confirm-request/{userId}")
    public ResponseEntity<String> confirmRequestFriend(@PathVariable long userId,
                                                       @RequestHeader("X-User-Id") long friendId){
        log.info("Executing confirmRequestFriend for userId: {}, friendId: {}", userId, friendId);
        Locale locale = Locale.getDefault();

        friendService.confirmRequestFriend(userId, friendId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                                "message.ok.confirm_request",
                                locale
                        )
                );
    }

    @DeleteMapping("/delete-my-request/{friendId}")
    public ResponseEntity<Void> deleteMyRequest(@RequestHeader("X-User-Id") long userId,
                                                @PathVariable long friendId){
        log.info("Executing deleteMyRequest for userId: {}, friendId: {}", userId, friendId);

        tempFriendService.deleteMyRequest(userId, friendId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-request/{userId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable long userId,
                                              @RequestHeader("X-User-Id") long friendId){
        log.info("Executing deleteRequest for userId: {}, friendId: {}", userId, friendId);

        tempFriendService.deleteRequest(userId, friendId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-friend/{friendId}")
    public ResponseEntity<Void> deleteFriend(@RequestHeader("X-User-Id") long userId,
                                             @PathVariable long friendId){
        log.info("Executing deleteFriend for userId: {}, friendId: {}", userId, friendId);

        friendService.deleteFriend(userId, friendId);

        return ResponseEntity.noContent().build();
    }
}
