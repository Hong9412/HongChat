package com.hong.test.chat.model.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class ChatMessage {

    private int chatNo;
    private int roomId;
    private String writer;

    @Size(max = 1000, message = "메시지 범위를 초과했습니다.")
    private String message;

    private Timestamp chatDate;

}