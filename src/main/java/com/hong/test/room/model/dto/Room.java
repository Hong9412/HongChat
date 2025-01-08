package com.hong.test.room.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    private int roomId;
    private String userId;

    @NotBlank(message = "채팅방 이름을 입력해주세요.")
    private String roomName;

    @Min(value = 1, message = "최대 인원 수는 1명 이상이어야 합니다.")
    @Max(value = 20, message = "최대 인원 수는 20명 이하이어야 합니다.")
    private int maxUser;

    private int roomPw;
}
