package com.hong.test.chat.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatus {
    private int chatNo;
    private int roomId;
    private String userName;
    private int readStatus;
}
