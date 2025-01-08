package com.hong.test.room.model.mapper;

import com.hong.test.chat.model.dto.ChatRoomDetails;
import com.hong.test.room.model.dto.Room;
import com.hong.test.room.model.dto.RoomUser;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface RoomMapper {

    int createRoom(Room chat);

    void addUserToRoomByUserId(String userId, String role);
    void addUserToRoomByRoomId(int roomId, String userId, String role);

    List<Room> roomByUserId(String userId);

    int countUsersInRoom(int roomId);

    List<Room> findByAllRoom();

    Room findCreateRoomUserById(int roomId);

    Room checkRoomPwById(int roomId);

    Room findRoomByRoomId(int roomId);

    List<RoomUser> findByUser(int roomId);

    List<ChatRoomDetails> getRoomsByUserId(String userId);

    Timestamp findByRoomAtByUserId(int roomId, String joinUser);

    int checkRoomPassword(int roomId, int password);

    void deleteRoomAdmin(int roomId);

    void deleteRoomUser(int roomId, String userId);

    int getCountReadStatus(int roomId, int readStatus, String userId);
}
