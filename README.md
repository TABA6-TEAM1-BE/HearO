
![HearO_Logo3](https://github.com/user-attachments/assets/95e3a6f8-de4b-40bc-b2d9-756f4bbfed90)

## 🔦 프로젝트 소개
청각 장애인들을 위한 실내 소음 감지 및 알림 서비스.(나중에 더 잘 써야 할듯)

## 주요 기능 (각 요소들의 세부 기능을 디테일하게 쓸 예정)
### 인증, 인가
### 조회
### 비동기 처리
### 메시지 전송

## 아키텍처
![아키텍처](https://github.com/user-attachments/assets/89d02664-7f77-404c-ba2a-bee9845758b9)
### MSA를 사용한 이유
장애인을 대상으로 하는 서비스다보니 업데이트를 위한 짧은 서버 중단도 큰 영향이 있을 수 있겠다는 생각이 들어서 무중단 배포를 목표로 했음. ///무중단 배포를 하려면 꼭 msa여야 하나??
### MongoDB를 사용한 이유
레코드가 생성되고 조회될 일이 많고, 트랜잭션 처리를 할만큼 무결성이 보장되어야 하는 작업이 없음. 그래서 정확성보다는 유연하고 빠른것이 중요하다고 생각했고 rdb 대신 nosql을 사용함.
### Redis를 사용한 이유
기존에는 토큰 발급 및 인증, 인가 처리를 게이트웨이에서 처리했는데, 게이트웨이는 라우팅만 하는 것이 맞다고 생각해서 기능을 분리하기로 함. 그 결과 인증/인가 기능을 레디스를 통해 처리해서 결과적으로 토큰이 가벼워지고, 토큰에 민감한 정보를 담지 않게 되었음.

### 스프링 액추에이터를 사용한 이유
스프링 유레카는 주기적으로 헬스체크를 통해서 서비스의 상태를 감지하는데, 서버 업데이트를 위해서 서버를 종료하더라도 헬스체크를 통해서 서버의 상태를 확인하다보니 즉각적으로 반영되지 않아서 서버가 종료되더라도 라우팅을 하는 문제가 발생했음. 그래서 스프링 액추에이터를 사용해서 인위적으로 서버의 상태를 바꾸면 상태가 바로 반영되었던거 같음.(나중에 다시 작성)
### 도커와 깃허브 액션을 사용한 이유

### Firebase를 사용한 이유
대충 웹소켓을 쓰려 했지만 앱이 꺼지면 알림이 안간다는 내용



## 기술 스택
### Frontend
<img src="https://img.shields.io/badge/React Native-61DAFB?style=flat&logo=react&logoColor=black"/>

### Backend
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=springsecurity&logoColor=white"/> <img src="https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white"> <img src="https://img.shields.io/badge/Redis-FF4438?style=flat&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=white"> <img src="https://img.shields.io/badge/OpenAI-412991?style=flat&logo=openai&logoColor=white">

### Database
<img src="https://img.shields.io/badge/mongoDB-47A248?style=flat&logo=MongoDB&logoColor=white">

### Infrastructure/DevOps
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=flat&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat&logo=amazonec2&logoColor=white">

### AI/ML
<img src="https://img.shields.io/badge/python-3776AB?style=flat&logo=python&logoColor=white"> <img src="https://img.shields.io/badge/TensorFlow-FF6F00?style=flat&logo=tensorflow&logoColor=white"> <img src="https://img.shields.io/badge/FastAPI-009688?style=flat&logo=fastapi&logoColor=white"/>


