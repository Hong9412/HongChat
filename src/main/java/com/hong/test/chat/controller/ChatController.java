package com.hong.test.chat.controller;

import com.hong.test.chat.config.WebSocketSessionManager;
import com.hong.test.chat.model.dto.ChatMessage;
import com.hong.test.chat.model.dto.ReadStatus;
import com.hong.test.chat.model.service.ChatService;
import com.hong.test.room.model.dto.Room;
import com.hong.test.room.model.dto.RoomUser;
import com.hong.test.room.model.service.RoomService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final WebSocketSessionManager webSocketSessionManager;

    public ChatController(ChatService chatService, RoomService roomService, SimpMessagingTemplate messagingTemplate, WebSocketSessionManager webSocketSessionManager) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.webSocketSessionManager = webSocketSessionManager;
    }


    // 메세지 보내기
    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessage chatMessage) {

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    // 채팅방에 유저가 입장하면 알림
    @MessageMapping("/chat/enter")
    public void enterChatRoom(ChatMessage chatMessage, StompHeaderAccessor headerAccessor) {

        ChatMessage enterMessage = new ChatMessage();
        enterMessage.setWriter("system");
        enterMessage.setMessage(chatMessage.getWriter() + "님이 채팅방에 들어왔습니다.");

        String userName = chatMessage.getWriter();
        int roomId = chatMessage.getRoomId();

        webSocketSessionManager.userJoined(roomId, userName);

        // WebSocket 세션에 유저 정보 저장
        headerAccessor.getSessionAttributes().put("userName", userName);
        headerAccessor.getSessionAttributes().put("roomId", roomId);

        Set<String> usersInRoom = webSocketSessionManager.getUsersInRoom(roomId);

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), enterMessage);

        // 유저 목록을 최신화
        List<RoomUser> currentUsers = roomService.findByUser(chatMessage.getRoomId());
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId() + "/users", currentUsers);
    }

    // 채팅 메세지 DB에 저장
    @ResponseBody
    @PostMapping("/insertChat")
    public String insertChatMessage(@Valid ChatMessage chatMessage, int roomId, String writer, String message) {

        chatMessage.setRoomId(roomId);
        chatMessage.setWriter(writer);
        chatMessage.setMessage(message);

        int result = chatService.insertChatMessage(chatMessage);
        if (result > 0) {

            // 현재 채팅방을 이용하고 있는 유저 리스트
            Set<String> roomUsers = webSocketSessionManager.getUsersInRoom(roomId);

            // 방에 속해있는 유저들의 상태를 1로 설정
            for (String userName : roomUsers) {
                int readStatus = 1;
                Map<String, Object> params = new HashMap<>();
                params.put("roomId", roomId);
                params.put("userName", userName);
                params.put("readStatus", readStatus);
                chatService.insertReadStatus(params);
            }

            // 채팅방에 속한 유저 리스트
            List<RoomUser> allRoomUsers = roomService.findByUser(roomId);
            Set<String> allUserNames = allRoomUsers.stream()
                    .map(RoomUser::getJoinUser)
                    .collect(Collectors.toSet());

            Set<String> offlineUsers = new HashSet<>(allUserNames);

            // 현재 입장하지 않은 유저 리스트
            offlineUsers.removeAll(roomUsers);

            // 방에 속해있지만 현재 입장하지 않은 유저들의 상태를 0으로 설정
            for (String userName : offlineUsers) {
                int readStatus = 0;
                Map<String, Object> params = new HashMap<>();
                params.put("roomId", roomId);
                params.put("userName", userName);
                params.put("readStatus", readStatus);
                chatService.insertReadStatus(params);
            }
            return "success";
        } else {
            return "fail";
        }
    }

    // 읽지 않은 전체 메시지 갯수 출력
    @ResponseBody
    @GetMapping("/countNotReadMessages")
    public int countNotReadMessagesByUser(HttpSession session) {
        String joinUser = (String) session.getAttribute("userId");
        int count = chatService.countNotReadByUser(joinUser);
        return count;
    }

    // 각 방마다 읽지 않은 메시지 카운트
    // 우선 해당 유저가 속한 방 리스트를 가져온다(rooms)
    // 각 방마다의 읽지 않은 메시지를 카운트한다.
    // Ajax를 통해 카운트 된 값을 전달한다.
    public int countNotReadMessagesByRooms(HttpSession session) {
        return 0;
    }
}