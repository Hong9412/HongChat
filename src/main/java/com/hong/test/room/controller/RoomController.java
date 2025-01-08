package com.hong.test.room.controller;

import com.hong.test.chat.model.dto.ChatMessage;
import com.hong.test.chat.model.dto.ChatRoomDetails;
import com.hong.test.chat.model.service.ChatService;
import com.hong.test.room.model.dto.Room;
import com.hong.test.room.model.dto.RoomUser;
import com.hong.test.room.model.service.RoomService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RoomController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public RoomController(RoomService roomService, SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    // 채팅방 생성 폼
    @GetMapping("/createRoom")
    public String createRoomForm(HttpSession session, Model model) {
        String loginCheck = (String) session.getAttribute("userId");

        if(loginCheck == null) {
            model.addAttribute("error", "로그인 후 이용가능합니다.");
        }
        return "/chat/create";
    }

    // 채팅방 생성 로직
    @PostMapping("/createRoom")
    public String createRoom(@Valid Room chat, BindingResult bindingResult, HttpSession session, Model model) {

        String userId = (String) session.getAttribute("userId");
        chat.setUserId(userId);

        // 유효성 검사 오류 처리
        if (bindingResult.hasErrors()) {
            // 오류 메시지를 모델에 추가하고 폼으로 돌아가도록 처리
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "/chat/create";
        }

        // 채팅방을 생성하고 ROOM_USER 테이블에 생성자를 admin 권한으로 넣는 로직
        // 트랜잭셔널 어노테이션을 통해 하나의 작업 단위로 묶음
        roomService.createRoomAndAssignAdmin(chat, userId);

        return "redirect:/";
    }

    // 나의 채팅방
    @GetMapping("/myRoom")
    public String myRoomForm(HttpSession session, Model model) {

        Map<Integer, Integer> roomUserCounts = new HashMap<>();
        String userId = (String) session.getAttribute("userId");

        if(userId == null) {
            model.addAttribute("error", "로그인 후 이용가능합니다.");
        }

        // 참여 채팅방 목록
        List<ChatRoomDetails> rooms = roomService.getRoomsByUserId(userId);

        for(ChatRoomDetails room : rooms) {

            // 채팅방 인원수 체크 로직
            int currentUsers = roomService.countUsersInRoom(room.getRoomId());
            roomUserCounts.put(room.getRoomId(), currentUsers);
        }
        model.addAttribute("rooms", rooms);
        model.addAttribute("roomUserCounts", roomUserCounts);

        return "/chat/myRoom";
    }

    // 전체 채팅방 조회
    @GetMapping("/allRoom")
    public String allRoom(Model model, HttpSession session) {

        String loginId = (String) session.getAttribute("userId");

        if(loginId == null) {
            model.addAttribute("error", "로그인 후 이용가능합니다.");
        }

        Map<Integer, Integer> roomUserCounts = new HashMap<>();
        List<Room> rooms = roomService.findByAllRoom();

        for(Room room : rooms) {

            // 채팅방 인원수 체크 로직
            int currentUsers = roomService.countUsersInRoom(room.getRoomId());
            roomUserCounts.put(room.getRoomId(), currentUsers);
        }

        model.addAttribute("loginId", loginId);
        model.addAttribute("rooms", rooms);
        model.addAttribute("roomUserCounts", roomUserCounts);

        return "chat/allRoom";
    }

    // 채팅방 입장
    @GetMapping("/chat/room/{roomId}")
    public String enterChatRoom(@PathVariable("roomId") int roomId, Model model, HttpSession session) throws ParseException {

        // 로그인 유저 정보
        String joinUser = (String) session.getAttribute("userId");

        // 유저가 입장한 시간
        Timestamp roomAt = roomService.findByRoomAtByUserId(roomId ,joinUser);

        // 유저가 입장한 시간부터 DB에 저장된 메세지 출력
        List<ChatMessage> previousMessages = chatService.getPreviousMessages(roomId, roomAt);

        // 채팅방 입장 시 메시지 읽음 처리
        chatService.updateReadStatusToRead(roomId, joinUser);

        // 채팅방 정보와 유저 목록
        Room room = roomService.findRoomByRoomId(roomId);

        // 채팅방 유저 정보
        List<RoomUser> roomUsers = roomService.findByUser(roomId);

        // 방 생성자 확인
        String createRoomUser = room.getUserId();

        // 입장 유저가 방 생성자인지 확인
        if (!joinUser.equals(createRoomUser)) {

            boolean isUserInRoom = roomUsers.stream().anyMatch(user -> user.getJoinUser().equals(joinUser));

            // 해당 채팅방에 인원수 체크
            if (!isUserInRoom && roomService.countUsersInRoom(roomId) >= room.getMaxUser()) {
                return "redirect:/allRoom?error=The chat room is full.";
            }

            // 해당 채팅룸에 존재하지 않으면 입장하는 로직(권한 : user)
            if (!isUserInRoom) {
                roomService.addUserToRoomByRoomId(roomId, joinUser, "user");
            }
        }

        // 채팅방 유저 권한 찾기
        String userRole = null;
        for (RoomUser roomUser : roomUsers) {
            if (roomUser.getJoinUser().equals(joinUser)) {
                userRole = roomUser.getUserRole();
                break;
            }
        }

        model.addAttribute("room", room);
        model.addAttribute("userRole", userRole);
        model.addAttribute("previousMessages",previousMessages);
        model.addAttribute("roomUsers", roomUsers);
        model.addAttribute("userName", session.getAttribute("userId"));
        model.addAttribute("joinUser", joinUser);

        return "chat/chatRoom";
    }

    // 채팅방 비밀번호 체크 로직
    @PostMapping("/checkPassword")
    @ResponseBody
    public Map<String, Object> verifyPassword(@RequestParam int roomId, @RequestParam int password) {
        Map<String, Object> response = new HashMap<>();
        int isPasswordCorrect = roomService.checkRoomPassword(roomId, password);

        if (isPasswordCorrect == password) {
            response.put("success", true);
        } else {
            response.put("success", false);
        }

        return response;
    }

    // 채팅방에 입장한 유저 리스트 반환 로직
    @ResponseBody
    @GetMapping("/chat/room/{roomId}/users")
    public List<RoomUser> getUsersInRoom(@PathVariable("roomId") int roomId) {
        return roomService.findByUser(roomId);
    }

    // 채팅방 나가기 로직
    // 생성자라면 해당 방의 모든 정보를 삭제
    // user 권한이라면 해당 채팅방에서 나가기
    @DeleteMapping("/chat/room/{roomId}/delete")
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable("roomId") int roomId, HttpSession session, ChatMessage chatMessage) {
        String userId = (String) session.getAttribute("userId");
        Room room = roomService.findRoomByRoomId(roomId);

        Map<String, String> response = new HashMap<>();

        // 방 생성자인지 체크
        if (room.getUserId().equals(userId)) {
            // 방 생성자라면 방을 삭제하는 로직 수행
            roomService.deleteRoomAdmin(roomId);

            // 방장이 방을 나갔다는 메시지를 모든 유저에게 전송
            ChatMessage deleteMessage = new ChatMessage();
            deleteMessage.setWriter("system");
            deleteMessage.setMessage("방장이 방을 나갔습니다.");
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, deleteMessage);

            response.put("status", "success");
        } else {
            // 방 사용자라면 ROOM_USER 테이블에서 해당 유저 삭제
            roomService.deleteRoomUser(roomId, userId);

            ChatMessage deleteMessage = new ChatMessage();
            deleteMessage.setWriter("system");
            deleteMessage.setMessage(userId + "님이 방을 나갔습니다.");
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, deleteMessage);

            // 유저 목록 최신화
            List<RoomUser> currentUsers = roomService.findByUser(chatMessage.getRoomId());
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId() + "/users", currentUsers);

            response.put("status", "success");
        }

        return ResponseEntity.ok(response);
    }

    // 유저 강퇴 로직
    @DeleteMapping("/chat/room/{roomId}/kick/{userName}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("roomId") int roomId,
                                                          @PathVariable("userName") String userName,
                                                          ChatMessage chatMessage) {

        Map<String, String> response = new HashMap<>();

        // 강퇴 로직
        roomService.deleteRoomUser(roomId, userName);

        // 전체 사용자에게 알림 메시지 전송
        ChatMessage deleteMessage = new ChatMessage();
        deleteMessage.setWriter("system");
        deleteMessage.setMessage(userName + "님이 강퇴당했습니다.");
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, deleteMessage);

        // 강퇴된 사용자에게 강퇴 메시지 전송
        ChatMessage kickMessage = new ChatMessage();
        kickMessage.setWriter("system");
        kickMessage.setMessage("방에서 강퇴당했습니다.");
        System.out.println("Sending kick message to: /sub/chat/room/" + roomId + "/kick/" + userName);
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId + "/kick/" + userName, kickMessage);

        // 유저 목록 갱신
        List<RoomUser> currentUsers = roomService.findByUser(chatMessage.getRoomId());
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId() + "/users", currentUsers);

        response.put("status", "success");
        return ResponseEntity.ok(response);
    }


    // 각 방마다 읽지 않은 메시지 카운트
    @ResponseBody
    @GetMapping("/api/readStatus/{roomId}")
    public ResponseEntity<Integer> getUnreadMessageCount(@PathVariable int roomId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        int readStatus = 0;
        int unreadCount = roomService.getCountReadStatus(roomId, readStatus, userId);
        return ResponseEntity.ok(unreadCount);
    }
}
