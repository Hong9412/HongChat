package com.hong.test.chat.model.service;

import com.hong.test.chat.model.dto.ChatMessage;
import com.hong.test.chat.model.dto.ChatRoomDetails;
import com.hong.test.chat.model.dto.ReadStatus;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ChatService {

    List<ChatRoomDetails> getChatRoomDetails(int roomId);

    int insertChatMessage(ChatMessage chatMessage);

    List<ChatMessage> getPreviousMessages(int roomId, Timestamp roomAt);

    void insertReadStatus(Map<String, Object> params);

    /*
    채팅방 입장 시 안 읽은 메시지 읽음 처리
     */
    void updateReadStatusToRead(int roomId,String joinUser);

    /*
    안 읽은 메시지 갯수 카운트
     */
    int countNotReadByUser(String joinUser);
}
