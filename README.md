
![HearO_Logo3](https://github.com/user-attachments/assets/95e3a6f8-de4b-40bc-b2d9-756f4bbfed90)

## 🔦 프로젝트 개요 
### HearO: 청각 장애인을 위한 실내 소음 감지 및 알림 서비스  
현대 사회에서 다양한 소음과 음성 정보는 일상생활과 안전을 유지하는 데 중요한 역할을 한다. 그러나 청각 장애인은 이러한 정보에 접근하기 어려워 일상생활의 불편함을 겪거나, 비상 상황에서 위험에 처할 가능성이 높다.  
특히, 집 안에서 발생하는 소리(초인종, 가전제품 알림음, 화재 경보 등)는 시각적 대체 수단이 부족하여 더 큰 제약이 따른다.

본 프로젝트는 이러한 문제를 해결하고자 **청각 장애인이 일상에서 중요한 소리 정보를 놓치지 않도록 실시간으로 대체 정보를 제공**하며, **긴급 상황에서 빠르게 대응할 수 있는 시스템을 구축**하는 것을 목표로 한다.  
서비스 이름인 **HearO**는 ‘듣다(Hear)’와 ‘영웅(Hero)’를 결합한 이름으로, 소리를 대신 인식하고 전달함으로써 사용자의 일상을 더 안전하고 편리하게 만드는 조력자가 되고자 하는 의지를 담고 있다.

HearO는 **소음과 음성을 감지·분석하는 AI 기술**과 **실시간 알림 기능**을 결합하여, **실시간 소음 감지 맟 음성 변환(STT), 비상 상황 알림 서비스**를 제공한다.  
이를 통해 청각 장애인의 생활 편의성과 안전성을 높이고, 자율적인 일상생활을 도모하며, 누구나 소리로부터 소외되지 않는 환경을 조성하는 데 기여하고자 한다.

<br><br>
## ✔️ 주요 기능
1. 집 안의 특정 **생활 소음** 인식 후 **푸시 알림** 제공
    - 냉장고 소음, 가전제품 알림음 등 일상적인 소음을 감지
    - 이전 알림 내역을 **캘린더**에서 확인 가능

2. 초인종, 안내방송에서 사람 음성 인식 후 **요약 및 STT 변환**
    - 초인종의 특정 멜로디를 인식하여 방문자 알림 제공
    - 방송 내용 요약 후 텍스트로 변환하여 제공

3. **비상상황** 인식 시 진동
    - 경보음, 화재 경보 등 비상 신호를 감지
    - 사용자 기기로 강한 진동 제공

<br><br>
## ⚙️ 시스템 아키텍처
HearO는 청각 장애인을 위한 소음 감지 및 알림 서비스를 제공하기 위해 **마이크로서비스 아키텍처**를 기반으로 설계되었다. 이는 무중단 배포를 실현하고, 시스템 가용성을 극대화하여 사용자가 항상 안정적으로 서비스를 이용할 수 있도록 하기 위함이다.
![아키텍처](https://github.com/user-attachments/assets/89d02664-7f77-404c-ba2a-bee9845758b9)

### 1. 아키텍처 설계
- **인증, 인가**
    - **User Server(인증/인가 서버)**
        - 사용자의 로그인 요청을 처리하며, 성공적으로 인증된 사용자의 인증 객체를 Redis에 저장
        - 이후 Redis에 저장된 인증 객체를 참조하는 키값을 Gateway Server에 전달
    - **Gateway Server(라우팅 서버)**
        - User Server에게 전달받은 Redis 키 값으로 JWT 토큰 발급하여 클라이언트에게 반환(로그인 요청 시)
        - 이후 요청에서는 토큰 디코딩 후 요청 헤더에 키 값 담아서 전송
- **조회**
    - **Record Server**
        - 소음이 감지되면 AI 서버에게 분석 요청을 보내고, 결과를 반환받으면 DB에 기록을 저장하고, 사용자에게 알림을 보냄
        - 또한 사용자가 요청한 알림 기록을 반환함
        - AI 서버 분석 결과가 사람 음성인 경우 OpenAI를 사용하여 음성 데이터를 텍스트로 변환(STT) 및 요약
- **AI 서버와 소음 분석**
    - **TensorFlow**: 실시간 소음 데이터를 분석하여 소음의 유형을 분류
    - **FastAPI**: AI 모델의 API 서버로 활용, 분석 결과를 Record 마이크로서비스에 전달
- **비동기 처리**
    - AI 서버는 소음 데이터를 받아 분석한 뒤 결과를 Record Server에 전달하며, 이 과정은 독립적으로 실행되어 메인 서비스에 영향을 주지 않음
- **메시지 전송**
    - **Firebase Cloud Messaging(FCM)**: 감지된 소음 및 비상 상황이 AI 서버에 의해 분석이 완료되면 해당 내용에 대한 알림을 클라이언트(모바일 앱)로 전달함

<br><br>
### 2. 데이터 설계
![HearO drawio](https://github.com/user-attachments/assets/2696fe8a-0289-4833-a006-bc50823089e2)

<br><br>
### 3. 주요 기술 요소
- ### MSA 도입
    - 기존 모놀리식 구조에서의 서버 업데이트를 위한 짧은 서비스 중단이 청각 장애인 사용자에게 큰 영향이 있을 수 있겠다는 생각이 들어서 무중단 배포를 목표로 했음. 
    - MSA를 도입하여 서비스별 독립적 배포와 확장성을 보장
    - 무중단 배포를 위해 스프링 액추에이터를 활용하여 서버 상태를 인위적으로 DOWN 상태로 설정, 유레카가 이를 감지하고 라우팅에서 제외하여 중단 없는 업데이트를 구현

- ### MongoDB를 사용한 이유
    - 레코드가 생성되고 조회될 일이 많고, 트랜잭션 처리를 할만큼 무결성이 보장되어야 하는 작업이 없음. 그래서 정확성보다는 유연하고 빠른것이 중요하다고 생각했고 RDB 대신 NoSQL을 사용함.

- ### Redis를 통한 인증/인가
    - 기존에는 토큰 발급 및 인증, 인가 처리를 게이트웨이에서 처리함. 그러나 게이트웨이에서는 라우팅만 하는 것이 맞다고 생각하여 기능을 분리하기로 함. 그 결과 인증/인가 기능을 Redis를 통해 처리하여 결과적으로 토큰이 가벼워지고, 토큰에 민감한 정보를 담지 않게 되었음.
        - 로그인 성공 시, Redis에 인증 객체를 저장
        -  JWT 토큰에 Redis 키값을 담아 반환
        -  이후 요청에서는 JWT 토큰의 키 값을 통해 Redis에서 인증 객체 확인.

- ### Spring Actuator와 무중단 배포
    - 스프링 클라우드는 유레카 서버를 통해서 각 인스턴스를 탐지하고 그 정보를 바탕으로 스프링 게이트웨이에서 라우팅을 함. 스프링 유레카는 주기적으로 헬스체크를 통해서 서비스의 상태를 감지함.
    - 이때 서버 업데이트를 위해서 서버를 종료하더라도, 유레카는 다음 헬스체크 주기까지 서버가 종료된 사실을 인지하지 못해 종료된 서버로 트래픽이 라우팅되는 문제가 발생함. 이를 해결하기 위해 스프링 액추에이터를 사용하여, 서버 상태를 DOWN으로 수동 변경하여 상태가 즉각 반영되도록 함.
    - 본 프로젝트에서는 마이크로 서비스 서버를 기능별로 두 개씩 실행하고 있으며, 스프링 액추에이터를 사용해 서버 업데이트나 유지보수가 필요할 때 특정 인스턴스를 다운 상태로 만들어 요청을 해당 서버로 라우팅하지 않도록 처리하고, 업데이트가 완료된 후 다시 UP 상태로 전환하여 사용자에게 영향을 주지 않고 서비스를 무중단으로 배포함.

- ### Docker와 GitHub Actions
    - **MSA 아키텍처의 요구 충족**: Docker를 통해 각 서비스(Spring Boot 서버, Redis, MongoDB, AI 서버)를 독립적으로 실행하고 관리함
    - **CI/CD 파이프라인 구축**: GitHub Actions를 통해 PR이 merge 될 때마다 자동으로 빌드, 테스트, 컨테이너 이미지 생성, 배포까지의 과정을 처리.
    - 각 마이크로서비스를 독립적으로 빌드 및 테스트하여, 특정 서비스에만 변경 사항이 있을 경우 해당 서비스만 배포 가능. 이를 통해 무중단 배포와 빠른 업데이트를 지원.

- ### Firebase의 사용
    - **기존 문제**: WebSocket을 사용한 알림 전송 방식은 앱이 백그라운드 상태일 때 작동하지 않아 실시간 알림 구현 불가능
    - **해결 방안**: Firebase Cloud Messaging(FCM)을 도입하여 백그라운드 상태에서도 알림 전송 가능. 이는 단방향 알림 전달에 적합하며 안정적임
<br><br><br><br>
## 🖇️ 기술 스택
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

<br><br>
## 🔧 구현 결과 
![021](https://github.com/user-attachments/assets/4c0c305b-470a-4745-a033-429ff9c963ce)
![025](https://github.com/user-attachments/assets/5938395c-2a56-497c-966b-0b1df18585ab)


