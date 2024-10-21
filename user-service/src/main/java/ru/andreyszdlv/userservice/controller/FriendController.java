package ru.andreyszdlv.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controller.UserFriendResponseDTO;
import ru.andreyszdlv.userservice.service.FriendService;
import ru.andreyszdlv.userservice.service.LocalizationService;
import ru.andreyszdlv.userservice.service.TempFriendService;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/friends")
public class FriendController {

    private final LocalizationService localizationService;

    private final TempFriendService tempFriendService;

    private final FriendService friendService;

    @GetMapping
    public List<UserFriendResponseDTO> getFriendsByEmail(@RequestHeader("X-User-Email") String email){
        return friendService.getFriendsByEmail(email);
    }

    @PostMapping("/create-request/{friendId}")
    public ResponseEntity<String> createRequestFriend(@RequestHeader("X-User-Email") String userEmail,
                                                      @PathVariable Long friendId){
        Locale locale = Locale.getDefault();

        tempFriendService.createRequestFriend(userEmail, friendId);

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
    public ResponseEntity<String> confirmRequestFriend(@PathVariable Long userId,
                                                       @RequestHeader("X-User-Email") String friendEmail){
        Locale locale = Locale.getDefault();

        friendService.confirmRequestFriend(userId, friendEmail);

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
    public ResponseEntity<Void> deleteMyRequest(@RequestHeader("X-User-Email") String userEmail,
                                                @PathVariable Long friendId){

        tempFriendService.deleteMyRequest(userEmail, friendId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-request/{userId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long userId,
                                              @RequestHeader("X-User-Email") String friendEmail){

        tempFriendService.deleteRequest(userId, friendEmail);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-friend/{friendId}")
    public ResponseEntity<Void> deleteFriend(@RequestHeader("X-User-Email") String userEmail,
                                             @PathVariable Long friendId){

        friendService.deleteFriend(userEmail, friendId);

        return ResponseEntity.noContent().build();
    }
}
