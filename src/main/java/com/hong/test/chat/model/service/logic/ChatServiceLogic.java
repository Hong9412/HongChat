package com.hong.test.chat.model.service.logic;

import com.hong.test.chat.model.dto.ChatMessage;
import com.hong.test.chat.model.dto.ChatRoomDetails;
import com.hong.test.chat.model.dto.ReadStatus;
import com.hong.test.chat.model.mapper.ChatMapper;
import com.hong.test.chat.model.service.ChatService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceLogic implements ChatService {

    private final ChatMapper chatMapper;

    public ChatServiceLogic(ChatMapper chatMapper) {
        this.chatMapper = chatMapper;
    }

    @Override
    public List<ChatRoomDetails> getChatRoomDetails(int roomId) {
        return chatMapper.getChatRoomDetails(roomId);
    }

    @Override
    public int insertChatMessage(ChatMessage chatMessage) {
        return chatMapper.insertChatMessage(chatMessage);
    }

    @Override
    public List<ChatMessage> getPreviousMessages(int roomId, Timestamp roomAt) {
        return chatMapper.getPreviousMessages(roomId, roomAt);
    }

    @Override
    public void insertReadStatus(Map<String, Object> params) {
        chatMapper.insertReadStatus(params);
    }

    @Override
    public void updateReadStatusToRead(int roomId, String joinUser) {
        List<ReadStatus> readStatuses = chatMapper.getChatMessageStatus(roomId, joinUser);

        if (readStatuses != null && !readStatuses.isEmpty()) {
            List<Integer> chatNos = readStatuses.stream()
                    .map(ReadStatus::getChatNo)
                    .collect(Collectors.toList());

            chatMapper.updateReadStatusToRead(chatNos, roomId, joinUser);
        }
    }

    @Override
    public int countNotReadByUser(String joinUser) {
        return chatMapper.countNotReadByUser(joinUser);
    }

    // 채팅방에 입장한 유저들의 상태를 읽음으로 처리
    public void updateReadStatusForRoomUsers(int roomId, Set<String> roomUsers) {
        for (String userName : roomUsers) {
            int readStatus = 1;
            Map<String, Object> params = new HashMap<>();
            params.put("roomId", roomId);
            params.put("userName", userName);
            params.put("readStatus", readStatus);
            chatMapper.insertReadStatus(params);
        }
    }

    // 채팅방에 속한 유저들 중 현재 입장하지 않은 유저들의 상태를 0으로 설정
    public void updateOfflineUsersReadStatus(int roomId, Set<String> roomUsers, Set<String> allUserNames) {
        Set<String> offlineUsers = new HashSet<>(allUserNames);
        offlineUsers.removeAll(roomUsers);

        for (String userName : offlineUsers) {
            int readStatus = 0;
            Map<String, Object> params = new HashMap<>();
            params.put("roomId", roomId);
            params.put("userName", userName);
            params.put("readStatus", readStatus);
            chatMapper.insertReadStatus(params);
        }
    }
}
