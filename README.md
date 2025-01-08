# HONG CHAT


#### STOMP방식을 이용한 실시간 채팅 프로그램입니다.

## 개발 환경
Spring Boot 3.4.1, Gradle <br>
Mybatis, Oracle <br>
Websocket, jQuery  
Thymeleaf

## 개발 기간
2024/12/24 ~ 2025.01.09

## ERD 구조도
<img src="\src\main\resources\static\images\ERD구조도.PNG"/>

## 주요 기능

### 유저
기능명 | 상세
-------|-----
채팅방 생성 | 제목, 최대 인원수, 비밀번호를 통해 채팅방을 생성할 수 있습니다.
나의 채팅방 | 현재 입장한 채팅방 리스트를 볼 수 있습니다.
전체 채팅방 | 생성된 전체 채팅방을 조회합니다.

### 채팅방
기능명 | 상세
-------|-----
실시간 채팅 | STOMP방식을 사용한 실시간 채팅 기능 (메시지 DB에 저장)
메시지 확인 | 유저마다 채팅방에 입장한 시점부터 저장된 메시지를 볼 수 있습니다.
유저 리스트 | 현재 채팅방에 속한 유저 리스트를 볼 수 있습니다.
유저 강퇴 | 방 생성자라면 채팅방에 속한 유저를 강퇴시킬 수 있습니다.
방삭제(생성자) | 방 생성자라면 채팅방을 삭제할 수 있습니다. (방에 관한 모든 데이터 삭제 CASECADE)
나기기(참여자) | 참여자라면 해당 채팅방에서 나갈 수 있습니다. (채팅룸, 메시지 CASECADE)

### 읽음여부
기능명 | 상세
-------|-----
전체 조회 | 사이드바에서 현재 속한 방들의 전체 읽지 않은 메시지 갯수를 확인할 수 있습니다.
개별 조회 | 나의 채팅방에서 각 방마다 읽지 않은 메시지 갯수를 확인할 수 있습니다. 

# 구현 화면

## 회원가입 검증
<img src="\src\main\resources\static\images\회원검증.PNG"/>

검증1 : @vaild 유효성 검사 진행  
검증2 : 회원 DB 조회 후 이미 존재하는지 여부 확인  

## 로그인 검증
<img src="\src\main\resources\static\images\로긴검증.PNG"/>

검증 : 회원 DB 조회 후 아이디 / 패스워드 검증

## 채팅방 생성 검증
<img src="\src\main\resources\static\images\채팅방검증.PNG"/>

검증1 : 클릭 시 로그인 체크(Interceptor) -> 로그인 성공 시 지정한 targetURL로 이동  
검증2 : @Vaild 유효성 검사  
비밀번호 설정 : 체크박스 선택 시 비밀번호 설정 가능(Default 0)

## 전체 채팅방 조회
<img src="\src\main\resources\static\images\전체 채팅방.PNG"/>

## 채팅방 입장 검증
<img src="\src\main\resources\static\images\채팅방비밀번호검증.PNG"/>

인원수 체크 : 해당 채팅방 인원수 초과 시(alert창 생성)  
비밀번호 검증 : 해당 채팅방에 비밀번호가 있다면 모달창 생성  
성공 -> 채팅방 입장  
실패 -> alert창 생성

## 실시간 채팅
<img src="\src\main\resources\static\images\채팅방기능.PNG"/>

유저 리스트 : 채팅방에 속한 유저를 확인할 수 있음  
실시간 채팅 : 입장 시 Websocket 생성 -> 실시간 채팅 가능(STOMP)  
메시지 저장 : 입력한 메시지는 DB에 저장(Ajax)  
메시지 확인 : 나가지 않는 이상 채팅방 입장 시 기존에 작성했던 메시지를 확인할 수 있음.

## 강퇴기능
<img src="\src\main\resources\static\images\강퇴기능스.PNG"/>

생성자 : 유저 클릭 시 Confirm창 생성 -> 강퇴 DELETE (ROOM_USER_TBL)  
사용자 : 권한 없음 alert창 생성

## 방삭제 / 나가기
<img src="\src\main\resources\static\images\방나가기삭제.PNG"/>

생성자 : 해당 채팅방 삭제 (CHAT_ROOM)  
-> DELETE CASCADE (ROOM_USER_TBL, CHAT_MESSAGE, MESSAGE_READ_STATUS)    

사용자 : 해당 채팅방 나가기 (ROOM_USER_TBL)  
-> DELETE CASCADE (CHAT_MESSAGE, MESSAGE_READ_STATUS)

## 읽음 상태 
<img src="\src\main\resources\static\images\읽음상태.PNG"/>

전체 메시지 : 사이드바에서 현재 읽지 않은 메시지 갯수 확인  
방 메시지 : 나의 채팅방에서 각 방마다 읽지 않은 메시지 갯수 확인





