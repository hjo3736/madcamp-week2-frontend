# madcamp-week2-frontend
몰입캠프 2주차 프론트엔드

* app name : 몰입오목
* GIST 현재오 [hjo3736] ("https://github.com/hjo3736")
* KAIST 이준영 [leejy12] ("https://github.com/leejy12")

## 로그인 화면
* 구글 로그인 API인 OAuth를 통해 로그인을 구현
* 로그인 시 email을 HTTP서버에 보내 회원가입을 해야할지, 혹은 그냥 진행해도 될지 판단
![google sign in](https://user-images.githubusercontent.com/80434457/178444318-65bf8e0a-3169-46d7-983e-d117b68af842.png)

## 회원가입 화면
* 로그인 할때의 gmail을 그대로 불러옴
* 본인의 닉네임/ 소속 학교를 입력해 서버로 전송, DB에 저장
* 만약 이미 DB에 존재하는 닉네임의 경우 Toast를 통해 알림
![signup](https://user-images.githubusercontent.com/80434457/178444391-a867c3e7-aeb4-48d5-bbf1-d8ede0e44500.png)


## 메인 화면
* 좌상단의 아이콘 클릭 시 개인 정보 확인 가능
* 매칭 버튼을 통해 게임 플레이 가능
* 랭킹 버튼을 통해 현재 DB상의 모든 플레이어들의 리더보드 확인 가능
![main](https://user-images.githubusercontent.com/80434457/178444416-86d7bb2b-b672-45a6-b0c6-d1a1bd3c4ab7.png)


## 개인 정보
* 본인의 닉네임, 소속, Elo-rating 등을 확인 가능
* 로그아웃 및 회원 탈퇴 구현
![myprofile](https://user-images.githubusercontent.com/80434457/178444475-6032c2b8-e899-4f42-90df-2a9128d7d976.png)


## 리더보드
* DB의 모든 유저의 기록에 대한 Elo-rating의 내림차순
* 원하는 유저 클릭시, 각 유저에 대한 상세정보 확인 가능
![leaderboard](https://user-images.githubusercontent.com/80434457/178444523-3af66b63-8636-4480-9198-10befce9fcca.png)
![specific info](https://user-images.githubusercontent.com/80434457/178444533-f2411fae-8abc-484a-8fe7-14c7c7d09cc5.png)


## 게임 화면
* 액티비티 진입시, WebSocket을 통해 대기열 입성
* 만약 본인이 홀수번째이면 Waiting 상태
![waiting](https://user-images.githubusercontent.com/80434457/178444589-5e51ef18-9bed-4dba-b1d2-0f243d5f976d.png)
* 만약 본인이 짝수번째이면 고유한 방 번호를 생성하며 gameFound 상태
![matchfound](https://user-images.githubusercontent.com/80434457/178444626-8a63ae3f-78aa-4e0f-86fb-6f93591d2185.png)
* WebSocket 실시간 통신을 통해 서로가 놓는 좌표를 계산해 본인 색의 알을 놓음
![gameplaying](https://user-images.githubusercontent.com/80434457/178444664-0271f3b8-7319-488b-9d4a-e0215352ed2f.png)
* MediaPlayer를 통해 소리 구현
* 기권 버튼 구현
![surrender](https://user-images.githubusercontent.com/80434457/178444702-35e943fb-fb89-4f5b-bb0a-b9c1a1a72ffb.png)
* 결과 승패에 따라 Elo-rating 변동 및 DB반영
![winlose](https://user-images.githubusercontent.com/80434457/178444725-7585ea55-f8d6-4285-8d7f-52c6324866a4.png)


