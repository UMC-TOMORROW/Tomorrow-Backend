/**
 * 커리어톡 채팅 관련 API 컨트롤러
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.chatting.dto.request.CreateChatMessageRequestDTO;
import com.umc.tomorrow.domain.chatting.dto.response.CreateChatMessageResponseDTO;
import com.umc.tomorrow.domain.chatting.dto.response.GetChatMessageListResponseDTO;
import com.umc.tomorrow.domain.chatting.entity.Message;
import com.umc.tomorrow.domain.chatting.service.command.ChatBroadcastService;
import com.umc.tomorrow.domain.chatting.service.command.ChatCommandService;
import com.umc.tomorrow.domain.chatting.service.query.ChatQueryService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Chatting", description = "커리어톡 채팅 관련 API")
@Validated
@RestController
@RequestMapping("/api/v1/careertalks")
@RequiredArgsConstructor
public class ChatController {

    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;
    private final ChatBroadcastService chatBroadcastService;

   @MessageMapping("/chats/send")
    public void sendMessage(
            Principal principal,
            CreateChatMessageRequestDTO request
    ) {
        if (principal instanceof UsernamePasswordAuthenticationToken authentication) {
            Object principalObj = authentication.getPrincipal();
            if (principalObj instanceof CustomOAuth2User user) {
                Long userId = user.getUserDTO().getId();

                // 1. 저장 (동기)
                Message message = chatCommandService.saveMessage(request, userId);

                // 2. 응답 DTO 생성
                CreateChatMessageResponseDTO response = CreateChatMessageResponseDTO.builder()
                        .messageId(message.getId())
                        .build();

                // 3. 비동기 broadcast
                chatBroadcastService.broadcast(request.getChattingRoomId(), response);
            } else {
                throw new IllegalStateException("Principal is not CustomOAuth2User");
            }
        } else {
            throw new IllegalStateException("Principal is not UsernamePasswordAuthenticationToken");
        }
    }


    @PostMapping("{chattingRoomId}/join")
    @Operation(summary = "채팅방 참여", description = "유저가 채팅룸 id에 해당하는 채팅방에 참여합니다.")
    @ApiResponse(responseCode = "201", description = "채팅방 참여 성공")
    public ResponseEntity<BaseResponse<Void>> joinChatRoom(
        @NotNull @Positive @PathVariable Long chattingRoomId,
        @AuthenticationPrincipal CustomOAuth2User user
    ){
        Long userId = user.getUserDTO().getId();
        chatCommandService.joinChatRoom(chattingRoomId, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccessCreate(null));
    }

    @GetMapping("/{chattingRoomId}/messages")
    @Operation(summary = "채팅방 메시지 조회 (무한 스크롤)", description = "채팅방 메시지를 무한 스크롤 방식으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "채팅 메시지 조회 성공")
    public ResponseEntity<BaseResponse<GetChatMessageListResponseDTO>> getChatMessages(
            @NotNull @Positive @PathVariable Long chattingRoomId,
            @NotNull @Positive @RequestParam(required = false) Long cursor,
            @NotNull @Positive @RequestParam(defaultValue = "20") int size
    ) {
        GetChatMessageListResponseDTO response = chatQueryService.getMessages(chattingRoomId, cursor, size);
        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }

}
