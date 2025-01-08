package com.hong.test.room.model.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomUser {

    private int roomId;
    private String userRole;
    private String joinUser;
    private Timestamp roomAt;
}
