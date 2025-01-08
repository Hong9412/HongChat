package com.hong.test.chat.model.mapper;

import com.hong.test.chat.model.dto.ChatMessage;
import com.hong.test.chat.model.dto.ChatRoomDetails;
import com.hong.test.chat.model.dto.ReadStatus;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper
public interface ChatMapper {
    List<ChatRoomDetails> getChatRoomDetails(int roomId);

    int insertChatMessage(ChatMessage chatMessage);

    List<ChatMessage> getPreviousMessages(int roomId, Timestamp roomAt);

    void insertReadStatus(Map<String, Object> params);

    List<ReadStatus> getChatMessageStatus(int roomId, String joinUser);

    void updateReadStatusToRead(List<Integer> chatNos, int roomId, String joinUser);

    int countNotReadByUser(String joinUser);
}
