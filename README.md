# yesaladin_auth

YesAladin Auth는 분산 서버 환경으로 구축된 YesAladin 서비스의 인증/인가 처리를 담당하는 시스템 입니다. Spring Security에 JWT 인증 방식을 적용하여 Client의 Login/Logout 요청을 수행하며
JWT 토큰을 제공 및 관리합니다.

## Getting Started

```bash
./mvnw spring-boot:run
```
## Scheduling
- NHN Dooray!의 칸반 활용
<img width="1037" alt="image" src="https://user-images.githubusercontent.com/115197142/221077112-ba7e882f-6fa8-4994-b382-6550f1d91577.png">

- [@WBS(Work Breakdown Structure)](https://docs.google.com/spreadsheets/d/14DnQZrjOVgyu7F5QVFmUu2sGo3URppLTPhmdjCfbmiQ/edit#gid=537092179)를 구글 스프레드시트로 관리
<img width="1042" alt="image" src="https://user-images.githubusercontent.com/115197142/221077360-daaf6cdc-d0a6-4d1b-ba81-a3c2b672f87c.png">

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

<img width="1055" alt="스크린샷 2023-02-22 오전 10 15 46" src="https://user-images.githubusercontent.com/60968342/220495916-41d23e85-f467-4e1c-ae44-a1f3b6d58d2a.png">

## CI/CD

<img width="1102" alt="스크린샷 2023-02-22 오후 7 24 08" src="https://user-images.githubusercontent.com/60968342/220593590-58f50bd3-302f-455d-bf99-78371b2a1ba7.png">

## Technical Issue

### 분산 서버 환경에서의 인증/인가

Front Server에서의 사용자 요청을 받아 유효한 요청인지의 여부를 판별하고, JWT를 발급시켜주기 위해 `UsernamePasswordAuthenticationFilter`를 Customizing 하였습니다.

사용자 정보에 대한 Database는 Shop API Server에 종속되어 있어 내부적 flow는 다음과 같습니다.  
`AuthenticationProvider`에 의해 책임을 받은 `UserDetailsService`를 Custom한 곳에서 `RestTemplate`으로 API 호출 후 인증 과정을 수행합니다. Spring Security를 적용한 서버는 Front Server, Shop API Server, Auth Server이고 Front Server를 제외한 각 서버는 Session 유지방식을 Stateless로 고정하였습니다.  

Front Server의 경우 Vue.js, React.js 등과 같은 Frontend Framework가 아닌 Spring Boot + Thymeleaf 기반의 서버이기 때문에 인증/인가 요청 이후 발급된 JWT를 직접적으로 Http Body에 넣어 넘겨주지 않고 회원마다 고유하게 발급된 uuid와 accessToken과 같은 정보만을 HTTP Header에 넣어 return 합니다. Front Server는 발급받은 JWT를 기반으로 Session 및 Cookie를 활용하는 방식으로 회원의 로그인을 유지하도록 설계하였습니다.

또한, Front Server의 scale out으로 인해 로그인을 유지하기 어렵다는 문제로 Redis를 공유 세션 저장소로 사용하였고, 사용자는 Auth Server로부터 브라우저에 Cookie로 발급된 uuid key를 기준으로 Redis Session에 접근하는 방식으로 로그인/로그아웃, 토큰 재발급의 기능을 수행합니다.

토큰 재발급의 경우, JWT의 accessToken, refreshToken을 토대로 accessToken의 유효한 타임을 기준으로 Front Server에 구현했던 Interceptor를 통해 사전에 재발급 해야 하는 시점인지 판별하고, 이에 해당하면 자동으로 Auth Server에 재발급 요청을 보내 응답받은 뒤, 다음 과정들(페이지 이동, Shop API 호출 등)을 수행하도록 합니다.

Shop API Server는 Front Server로부터 Authorization Header에 담긴 JWT 토큰 정보를 받아 이에 대한 인가 처리를 사전에 Auth Server로 위임합니다. Auth Server에서 해당 JWT 토큰의 유효성 검증이 완료되어 인가 된 경우, payload에 들어있는 사용자 식별 정보와 권한 정보를 추출하여 Shop API Server에 돌려줍니다. 이 정보를 바탕으로 Shop API Server 내에서 Spring Security를 통해 자체적으로 Authentication 을 생성하도록 처리하였으며, FilterSecurityInterceptor 및 method security를 적용하여 API 보안을 강화하였습니다.

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
