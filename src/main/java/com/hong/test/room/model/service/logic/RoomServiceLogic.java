package com.hong.test.room.model.service.logic;



import com.hong.test.chat.model.dto.ChatRoomDetails;
import com.hong.test.room.model.dto.Room;
import com.hong.test.room.model.dto.RoomUser;
import com.hong.test.room.model.mapper.RoomMapper;
import com.hong.test.room.model.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class RoomServiceLogic implements RoomService {

    private final RoomMapper roomMapper;

    public RoomServiceLogic(RoomMapper roomMapper) {
        this.roomMapper = roomMapper;
    }

    @Transactional  // 트랜잭션 적용
    @Override
    public void createRoomAndAssignAdmin(Room chat, String userId) {

        // 1. 채팅방 생성
        int result = roomMapper.createRoom(chat);
        if (result == 0) {
            throw new RuntimeException("채팅방 생성 실패");
        }

        // 2. 채팅방 생성 후 해당 유저에게 admin 권한 부여
        String role = "admin";
        roomMapper.addUserToRoomByUserId(userId, role);
    }

    @Override
    public void deleteRoomAdmin(int roomId) {
        roomMapper.deleteRoomAdmin(roomId);
    }

    @Override
    public void deleteRoomUser(int roomId, String userId) {
        roomMapper.deleteRoomUser(roomId, userId);
    }

    @Override
    public int getCountReadStatus(int roomId, int readStatus, String userId) {
        int count = roomMapper. getCountReadStatus(roomId, readStatus, userId);
        return count;
    }

    @Override
    public int createRoom(Room chat) {
        int result = roomMapper.createRoom(chat);
        return result;
    }

    @Override
    public void addUserToRoomByUserId(String userId, String role) {
        roomMapper.addUserToRoomByUserId(userId, role);
    }

    @Override
    public void addUserToRoomByRoomId(int roomId, String userId, String role) {
        roomMapper.addUserToRoomByRoomId(roomId, userId, role);
    }

    @Override
    public List<Room> RoomByUserId(String userId) {
        List<Room> chat = roomMapper.roomByUserId(userId);
        return chat;
    }

    @Override
    public int countUsersInRoom(int roomId) {
        int count = roomMapper.countUsersInRoom(roomId);
        return count;
    }

    @Override
    public List<Room> findByAllRoom() {
        List<Room> rooms = roomMapper.findByAllRoom();
        return rooms;
    }

    @Override
    public String findCreateRoomUserById(int roomId) {
        Room room = roomMapper.findCreateRoomUserById(roomId);
        return room.getUserId();
    }

    @Override
    public int checkRoomPwById(int roomId) {
        Room room = roomMapper.checkRoomPwById(roomId);
        return room.getRoomPw();
    }

    @Override
    public Room findRoomByRoomId(int roomId) {
        Room room = roomMapper.findRoomByRoomId(roomId);
        return room;
    }

    @Override
    public List<RoomUser> findByUser(int roomId) {
        List<RoomUser> roomUser = roomMapper.findByUser(roomId);
        return roomUser;
    }

    @Override
    public List<ChatRoomDetails> getRoomsByUserId(String userId) {
        List<ChatRoomDetails> rooms = roomMapper.getRoomsByUserId(userId);
        return rooms;
    }

    @Override
    public Timestamp findByRoomAtByUserId(int roomId, String joinUser) {
        Timestamp roomAt = roomMapper.findByRoomAtByUserId(roomId, joinUser);
        return roomAt;
    }

    @Override
    public int checkRoomPassword(int roomId, int password) {
        int checkPw = roomMapper.checkRoomPassword(roomId, password);
        return checkPw;
    }


}
