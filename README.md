# 🍳 RECIPIA_RECIPE

**레시피아 서비스의 [레시피 서버]**

**배포(원스토어):** https://m.onestore.co.kr/mobilepoc/apps/appsDetail.omp?prodId=0000774118 
<br/>
**소개 페이지 (노션):** https://upbeat-willow-06b.notion.site/7ece9e5f602a43f583d7f4cf101e7d69?pvs=4


<img width="960" alt="레시피아_인트로" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/74906042/06e4f4f0-76e7-4b13-bafe-7dd02cfc76f8">



<br/>

## 🔶 프로젝트 소개
- 레시피아는 레시피를 이미지와 함께 작성하고 공유할 수 있는 SNS입니다.
- 검색을 통해 찾고싶은 레시피 이름을 검색하여 구경할 수 있습니다.
- 다양한 회원을 팔로우하며 마음에 드는 레시피에 댓글을 작성할 수 있고, 좋아요, 북마크를 할 수 있습니다.
- 레시피 서버는 그중 레시피와 관련된 모든 프로세스를 담당하고 있습니다.

<br/>

## 🔶 개발 기간
- 2023.05 ~ 2023.02 (베타버전 출시)
- 계속해서 추가개발을 진행중입니다.


<br/>

## 🔶 개발 환경
- **언어:** Java 17
- **프레임워크:** Spring Boot 3.1.2, Spring Security 6
- **ORM:** JPA, Querydsl
- **테스트 및 모니터링:** JUnit, Mockito, P6Spy, Zipkin
- **API 및 서비스 통신:** JWT, Feign Client
- **클라우드:** AWS SDK
- **데이터베이스:** PostgreSQL (RDS), MongoDB, Redis
- **개발 도구:** IntelliJ IDEA, DataGrip
- **협업 도구:** Jira, Confluence, Notion

<br/>


## 🔶 인프라
### MSA 인프라 구성
- 프로젝트는 MSA로 구성되어있으며 총 세개(멤버, 레시피, 지프킨)의 서비스로 이루어져있습니다.
- 그 중 멤버서버 ECS 클러스터에 MEMBER 서버를 구축하였습니다.
- 외부 데이터베이스는 RDS(PostgreSQL), Redis, S3를 사용합니다.
- 이벤트 드리븐, 메시지 드리븐으로는 SNS, SQS를 사용합니다.
<img width="1024" alt="스크린샷 2024-02-10 오후 3 45 08" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/2a400b2d-6ebf-4505-8a76-2aa0142b7205">

### ECS 구성
- ECS 인프라는 다음과 같이 ECR에 저장된 스프링부트 이미지를 받아서 컨테이너로 동작시킵니다.
- 하나의 ECS에는 하나의 서비스, 하나의 태스크 정의로 실행됩니다.
- SpringBoot에는 Zipkin서버로 로그를 전송하도록 설계하였습니다.
<img width="1024" alt="스크린샷 2024-02-10 오후 4 08 23" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/6684b823-55a9-4332-aa22-acc967379def">

### CI/CD 설계
- CI/CD는 AWS의 CodePipeline으로 구축하였습니다.
- main 브랜치에 merge 발생 시 AWS CodePipeline이 자동으로 활성화됩니다.
- 이 과정은 GitHub 웹훅을 통해 이루어지며, GitHub의 변경 사항을 감지하여 트리거합니다.
- GitHub에서 웹훅이 트리거되면 CodeBuild가 동작합니다. 이때 SpringBoot의 소스 코드를 Docker 이미지로 빌드하고, 생성된 이미지를 ECR에 안전하게 업로드합니다.
- CodeDeploy가 ECR에 저장된 Docker 이미지를 감지하고, ECS에 롤링 업데이트 방식을 사용하여 무중단 배포를 진행합니다.
- 롤링 업데이트를 통해 새 버전의 애플리케이션을 점진적으로 배포하면서 서비스 중단 없이 업데이트를 완료할 수 있습니다.
<img width="1024" alt="ci-cd" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/7fa3c701-dfd7-46f3-9e4a-af7589ef9cb0">

<br/>

## 🔶 ERD
### AQuery를 사용하여 레시피 서버의 ERD를 설계하였습니다.
<img width="1024" alt="레시피_ERD" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/31cbf837-b7a2-485a-b39d-2a9eb940b23d">

<br/>

## 🔶 아키텍처
### 헥사고날 아키텍처 도입
<img width="1024" alt="레시피아_헥사고날" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/d02a5999-c348-42f3-8144-46902d0ec6a9">



### MSA의 DB 정합성 보장 과정
- 유저가 닉네임을 변경하면 멤버 서버에서는 닉네임 변경사항을 멤버 DB에 반영하고 Spring Event를 발행합니다. (이벤트 리스너는 2개를 선언)
- 스프링 이벤트 리스너중 1개가 동작하여 멤버 DB의 Outbox 테이블에 이벤트 발행 여부를 기록하고 DB 커밋을 합니다.(트랜잭션 커밋완료)
- 트랜잭션 커밋이 완료되면 AFTER_COMMIT을 적어준 또다른 스프링 이벤트 리스너가 동작하여 닉네임 변경 토픽으로 SNS 메시지를 발행합니다.
- SNS 메시지가 발행되면 2개의 SQS리스너가 동시에 동작하게 됩니다.
  - 레시피 서버에서 닉네임 변경 토픽을 리스닝하고있던 SQS가 실행됩니다.
  - 멤버 서버에서 닉네임 변경 토픽을 리스닝하고 있던 SQS가 실행됩니다. 이때 Outbox 테이블에 이벤트 발행 여부(published 컬럼)를 true로 업데이트합니다.
- 레시피 서버의 SQS 리스너가 동작할때 FeignClient를 사용하여 멤버 서버에 가장 최신의 유저 닉네임 정보를 요청합니다.
- 멤버 서버로부터 받아온 가장 최신의 유저 닉네임 정보를 레시피DB에 반영합니다.
<img width="1024" alt="spring-event" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/945f2187-aac3-4ee8-98cd-f181d20111f1">

### ZeroPayload 정책
- 데이터 전송시 메시지 내부에는 memberId만을 포함하도록 합니다.
- 분산추적을 위한 traceId는 SNS의 messageAttributes를 사용하여 전송합니다.
<img width="1024" alt="zero-payload" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/c5cad953-a027-4ac6-955f-9d1940bf8abf">

### 미발행된 SNS 메시지는 재발행
- Spring Batch를 사용하여 5분마다 미발행된 메시지를 재발행 합니다.
- Outbox 테이블에 저장된 발행여부(published)가 false것을 조회하여 배치가 동작합니다.
<img width="1024" alt="batch-event" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/466f5cc4-5510-4477-9ec2-711ce7c6a23b">

<br/>

## 🔶 주요 기능
#### 최진안, 김이준 (페어)
1. JWT로 사용자 검증
2. 레시피 CRUD (S3에 이미지 저장, 카테고리, 재료, 해시태그, 영양소 저장)
3. 댓글, 대댓글 작성기능
4. 회원탈퇴시 유저의 레시피 관련 데이터 삭제


#### 최진안
1. 레시피 내부의 닉네임 변경
2. 재료, 해시태그 검색기능
3. 북마크 기능
4. 좋아요 기능
5. 조회수 기능


<br/>

## 🔶 개발 전략
### 1. 스프링 시큐리티를 통한 JWT 인증기능 구현
<img width="1024" alt="시큐리티_동작" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/8a3a5a99-4b5a-4eb6-a0a8-eb2921815f0a">


### 2. 커스텀 예외처리 구현


- 에러코드 작성
<img width="1024" alt="스크린샷 2024-02-10 오후 6 17 02" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/760e1cf0-3719-4429-9a79-bb624829db93">

- 레시피아 전용 예외코드 작성
<img width="1024" alt="스크린샷 2024-02-10 오후 6 18 33" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/a7f69ab0-be37-4303-bfa5-2a4a2ca4711f">

- 중앙집중 예외처리 ControllerAdvice 코드 작성 (이때 NPE는 따로 처리하도록 작성)
<img width="1024" alt="스크린샷 2024-02-10 오후 6 18 45" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/0bacdd2d-2b1b-4284-8c79-da38e38b1434">

- 코드에서 아래와 같이 예외를 throw해서 커스텀 예외를 적용시켰다.
<img width="1024" alt="스크린샷 2024-02-10 오후 6 20 46" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/afe9000d-7fbc-4b4f-93ff-a6b005a760e2">



<br/>

## 🔶 기능 설명
### 1️⃣ 레시피 생성
- 재료, 해시태그는 RDB에 저장과 동시에 MongoDB에 저장합니다.
- MongoDB에는 재료, 해시태그 정보를 저장하여 검색기능에서 연관검색어 기능이 동작하도록 구현했습니다.
<img width="1024" alt="레시피_작성" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/6eb41b27-c18f-4ba6-b69d-cbfc9ae6e47a">


### 2️⃣ 레시피 상세조회 (이미지는 PreUrl을 가져온다.)
- 좋아요 횟수, 조회수는 Redis에 저장하고 조회합니다.
- S3에 저장된 이미지는 보안을 위해 pre-signed-url로 변환하여 사용자에게 보여줍니다.
<img width="1024" alt="레시피_상세조회" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/8d94cdc6-23c4-4cac-b53c-57804df7d825">


### 3️⃣ 레시피 삭제
- 기본정보, 댓글, 파일은 soft delete 처리를 하도록 설계했습니다.
- soft delete된 데이터는 한달이 지나면 배치를 통해 삭제를 진행합니다.
<img width="1024" alt="레시피_삭제" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/c13149bf-6f14-43c8-92d8-bccda66c2621">


### 4️⃣ 북마크
<img width="1024" alt="북마크_여부" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/3f7e6587-057c-4c4c-9ab4-3a1c4f2fdeda">

### 5️⃣ 좋아요
- 분기처리를 통해 처리합니다.
<img width="1024" alt="좋아요_여부" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/d9a5a80c-a79a-4774-9d6c-8320ac7c48ac">



<br/>

## 🔶 테스트 코드 작성
- 단위/통합 테스트 진행 (238개)
- Junit5, mockito, S3Mock, BddMockito 사용
- 외부 DB (MongoDB, Redis)를 사용한 테스트도 진행했습니다.

<img width="1024" alt="스크린샷 2024-02-10 오후 6 13 08" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/d46ca797-c4da-48fc-b8d1-c5ed0039533a">
