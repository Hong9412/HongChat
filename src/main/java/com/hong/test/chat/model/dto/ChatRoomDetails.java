package com.hong.test.chat.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDetails {

    private int roomId;
    private String joinUser;
    private int maxUser;
    private String roomName;
    private String userRole;
}
