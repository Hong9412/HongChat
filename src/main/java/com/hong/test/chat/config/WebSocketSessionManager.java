package com.hong.test.chat.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;

@Component
public class WebSocketSessionManager {

    private Map<Integer, Set<String>> roomUsers = new HashMap<>(); // 방 별로 유저 추적

    // 유저 입장 처리
    public void userJoined(int roomId, String userName) {
        roomUsers.putIfAbsent(roomId, new HashSet<>());
        roomUsers.get(roomId).add(userName);
    }

    // 유저 퇴장 처리
    public void userLeft(int roomId, String userName) {
        if (roomUsers.containsKey(roomId)) {
            roomUsers.get(roomId).remove(userName);
        }
    }

    // 특정 방에 있는 유저 목록 반환
    public Set<String> getUsersInRoom(int roomId) {
        return roomUsers.getOrDefault(roomId, Collections.emptySet());
    }

    // WebSocket 세션 종료 시 유저 제거
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userName = (String) headerAccessor.getSessionAttributes().get("userName");

        if (userName != null) {
            // 세션이 종료되었을 때, 유저 리스트에서 제거
            Integer roomId = (Integer) headerAccessor.getSessionAttributes().get("roomId");
            if (roomId != null) {
                userLeft(roomId, userName);
            }
        }
    }
}