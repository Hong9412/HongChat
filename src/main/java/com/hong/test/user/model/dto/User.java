package com.hong.test.user.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Size(min = 4, max = 20, message = "사용자 ID는 4자 이상 20자 이하로 입력해야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "사용자 ID는 영어와 숫자만 입력 가능합니다.")
    private String userId;

    @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다.")
    private String userPw;
}
