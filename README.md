# yesaladin_auth

YesAladin Auth는 MSA 환경으로 구축된 YesAladin 서비스의 인증/인가 처리를 담당하는 시스템 입니다. Spring Security에 JWT 인증 방식을 적용하여 Client의 Login/Logout 요청을 수행하며
JWT 토큰을 제공 및 관리합니다.

## Getting Started

```bash
./mvnw spring-boot:run
```

## Features

### [@송학현](https://github.com/alanhakhyeonsong)

- JWT 인증 서버 구축
  - Client의 Login / Logout 요청 수행 및 JWT 토큰 관리
  - Front Server에서 사용자가 갖고 있는 토큰에 대한 재발급 API 제공
- NHN Cloud Log & Crash를 연동하여 모니터링 환경 구축
- Spring Cloud Config를 연동하여 설정 정보 외부화

### [@김홍대](https://github.com/mongmeo-dev)

- Shop API Server로부터 위임 받은 인가 처리를 위한 JWT 토큰 검증 및 payload 반환 API 구현

## Project Architecture

// 이미지 추가할 것
![]()

## Technical Issue

### MSA 환경에서의 인증/인가

// 추후 작성 할 것. (JWT 발급/재발급 및 Security Filter Customizing)

## Tech Stack

### Languages

![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=Java)

### Frameworks

![SpringBoot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=SpringBoot&logoColor=white)
![Spring Security](https://img.shields.io/static/v1?style=flat-square&message=Spring+Security&color=6DB33F&logo=Spring+Security&logoColor=FFFFFF&label=)
![SpringCloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=flat&logo=Spring&logoColor=white)

### Build Tool

![ApacheMaven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=ApacheMaven&logoColor=white)

### Authentication

![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens&style=flat)

### Database

![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=Redis&logoColor=white)

### DevOps

![NHN Cloud](https://img.shields.io/badge/-NHN%20Cloud-blue?style=flat&logo=iCloud&logoColor=white)
![Jenkins](http://img.shields.io/badge/Jenkins-D24939?style=flat-square&logo=Jenkins&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-4E98CD?style=flat&logo=SonarQube&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat&logo=Grafana&logoColor=white)

### 형상 관리 전략

![Git](https://img.shields.io/badge/Git-F05032?style=flat&logo=Git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=GitHub&logoColor=white)

- Git Flow 전략을 사용하여 Branch를 관리하며 Main/Develop Branch로 Pull Request 시 코드 리뷰 진행 후 merge 합니다.
  ![image](https://user-images.githubusercontent.com/60968342/219870689-9b9d709c-aa55-47db-a356-d1186b434b4a.png)
- Main: 배포시 사용
- Develop: 개발 단계가 끝난 부분에 대해 Merge 내용 포함
- Feature: 기능 개발 단계
- Hot-Fix: Merge 후 발생한 버그 및 수정 사항 반영 시 사용

## Contributors

<a href="https://github.com/NHN-YesAladin/yesaladin_auth/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=NHN-YesAladin/yesaladin_front" />
</a>