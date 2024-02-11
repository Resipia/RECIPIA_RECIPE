# 🍳 RECIPIA_RECIPE

**레시피아 서비스의 [레시피 서버]**

배포(원스토어): https://m.onestore.co.kr/mobilepoc/apps/appsDetail.omp?prodId=0000774118 
<br/>
소개 페이지 (노션): https://upbeat-willow-06b.notion.site/7ece9e5f602a43f583d7f4cf101e7d69?pvs=4


<img width="960" alt="레시피아_인트로" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/74906042/06e4f4f0-76e7-4b13-bafe-7dd02cfc76f8">



<br/>

## 🔶 프로젝트 소개
- 레시피아는 레시피를 이미지와 함께 작성하고 공유할 수 있는 SNS입니다.
- 검색을 통해 찾고싶은 레시피 이름을 검색하여 구경할 수 있습니다.
- 다양한 회원을 팔로우하며 마음에 드는 레시피에 댓글을 작성할 수 있고, 좋아요, 북마크를 할 수 있습니다.
- 레시피 서버는 그중 레시피와 관련된 모든 프로세스를 담당하고 있습니다.

<br/>

## 🔶 개발 기간
- 2023.05 ~ 현재 진행 중


<br/>

## 🔶 개발 환경
- Java 17
- Spring Boot 3.1.2, Spring Security
- IDE: Intellij, Datagrip
- Database: RDS(PostgreSQL), MongoDB, Redis
- ORM: JPA
- 버전 및 이슈관리
- 협업 툴

<br/>


## 🔶 인프라
- 프로젝트는 MSA로 구성되어있으며 ECS 클러스터에 Recipe서버를 구축하였습니다.
- 외부 서비스는 RDS(PostgreSQL), Redis, MongoDB를 사용합니다.
<img width="1024" alt="스크린샷 2024-02-10 오후 3 45 08" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/2a400b2d-6ebf-4505-8a76-2aa0142b7205">

- ECS인프라는 다음과 같이 ECR에 저장된 스프링부트 이미지를 받아서 컨테이너로 동작시킵니다.
- SpringBoot에는 Zipkin서버로 로그를 전송하도록 설계하였습니다.
<img width="1024" alt="스크린샷 2024-02-10 오후 4 08 23" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/6684b823-55a9-4332-aa22-acc967379def">

- CI/CD는 AWS의 CodePipeline으로 구축하였습니다.
- GitHub와 CodeBuild를 연결했고 CodeDeploy에서는 ECR에 접근해서 빌드된 이미지를 사용해서 배포하도록 했습니다.
- 이렇게 설계하여 만약 main에 merge가 발생하면 Github 훅이 동작하여 CodePipeline이 동작합니다.
<img width="1024" alt="ci-cd" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/7fa3c701-dfd7-46f3-9e4a-af7589ef9cb0">

<br/>

## 🔶 아키텍처
- 헥사고날 아키텍처 도입


- 이벤트 드리븐 (DB 정합성 보장)
<img width="1024" alt="spring-event" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/945f2187-aac3-4ee8-98cd-f181d20111f1">

- ZeroPayload 정책
<img width="1024" alt="zero-payload" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/c5cad953-a027-4ac6-955f-9d1940bf8abf">

- 배치를 통한 미발행된 SNS 메시지는 재발행 실시
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
1. 커스텀 예외처리 구현


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
- 재료, 해시태그는 RDB에 저장과 동시에 MongoDB에 저장한다.
- MongoDB에 재료, 해시태그 정보를 저장하여 검색기능에서 연관검색어 기능을 구현했다.
<img width="1024" alt="레시피_작성" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/6eb41b27-c18f-4ba6-b69d-cbfc9ae6e47a">


### 2️⃣ 레시피 상세조회 (이미지는 PreUrl을 가져온다.)
- 좋아요 횟수, 조회수는 Redis에 저장하고 조회한다.
- S3에 저장된 이미지는 보안을 위해 pre-signed-url로 변환하여 사용한다.
<img width="1024" alt="레시피_상세조회" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/8d94cdc6-23c4-4cac-b53c-57804df7d825">


### 3️⃣ 레시피 삭제
- 기본정보, 댓글, 파일은 soft delete 처리한다.
- soft delete된 데이터는 한달이 지나면 배치를 통해 삭제를 진행한다.
<img width="1024" alt="레시피_삭제" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/c13149bf-6f14-43c8-92d8-bccda66c2621">


### 4️⃣ 북마크
<img width="1024" alt="북마크_여부" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/3f7e6587-057c-4c4c-9ab4-3a1c4f2fdeda">

### 5️⃣ 좋아요
- 분기처리를 통해 처리한다.
<img width="1024" alt="좋아요_여부" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/d9a5a80c-a79a-4774-9d6c-8320ac7c48ac">



<br/>

## 🔶 테스트 코드 작성
- 단위/통합 테스트 진행 (238개)
- Junit5, mockito, S3Mock, BddMockito 사용
- 외부 DB (MongoDB, Redis)를 사용한 테스트도 진행

<img width="1024" alt="스크린샷 2024-02-10 오후 6 13 08" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/d46ca797-c4da-48fc-b8d1-c5ed0039533a">
