# 🍳 RECIPIA_RECIPE

**레시피아 서비스의 [레시피 서버]**

**배포(원스토어):** https://m.onestore.co.kr/mobilepoc/apps/appsDetail.omp?prodId=0000774118 
<br/>
**소개 페이지 (노션):** https://upbeat-willow-06b.notion.site/7ece9e5f602a43f583d7f4cf101e7d69?pvs=4


<img width="960" alt="레시피아_인트로" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/74906042/06e4f4f0-76e7-4b13-bafe-7dd02cfc76f8">

## 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [개발 기간](#-개발-기간)
3. [개발 환경](#-개발-환경)
4. [주요 기능](#-주요-기능)
5. [인프라](#-인프라)
    - [MSA 인프라 구성](#msa-인프라-구성)
    - [ECS 구성](#ecs-구성)
    - [CI/CD 설계](#cicd-설계)
6. [ERD](#-erd)
7. [아키텍처](#-아키텍처)
    - [헥사고날 아키텍처 도입](#헥사고날-아키텍처-도입)
    - [MSA의 DB 정합성 보장 과정](#msa의-db-정합성-보장-과정)
    - [ZeroPayload 정책](#zeropayload-정책)
    - [미발행된 SNS 메시지는 재발행](#미발행된-sns-메시지는-재발행)
    - [Redis 데이터 복구전략 (RDBMS)](#redis-데이터-복구전략-rdbms)
8. [개발 전략](#-개발-전략)
    - [스프링 시큐리티를 통한 JWT 인증기능 구현](#스프링-시큐리티를-통한-jwt-인증기능-구현)
    - [커스텀 예외처리 구현](#커스텀-예외처리-구현)
9. [기능 설명](#-기능-설명)
    - [레시피 생성](#레시피-생성)
    - [레시피 상세조회](#레시피-상세조회)
    - [이미지 pre-signed-url 변환과정](#이미지-pre-signed-url-변환과정)
    - [레시피 삭제](#레시피-삭제)
    - [북마크](#북마크)
    - [좋아요](#좋아요)
10. [테스트 코드 작성](#-테스트-코드-작성)
11. [성능(부하) 테스트: nGrinder 사용](#-성능부하-테스트-ngrinder-사용)
    - [상용 레시피 서버 부하 테스트](#상용-레시피-서버-부하-테스트)
    - [로컬 vs 상용 성능 비교표](#로컬-vs-상용-성능-비교표)
   

<br/><br/>

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

## 🔶 인프라
### MSA 인프라 구성
- 프로젝트는 MSA로 구성되어 있으며 총 3개(멤버, 레시피, 지프킨)의 서비스로 이루어져 있습니다. 
- 그중 레시피서버 ECS 클러스터에 RECIPE 서버를 구축하였습니다. 
- 외부 데이터베이스는 RDS(PostgreSQL), Redis, MongoDB, S3를 사용합니다.
- MessageQueue로는 SNS, SQS를 사용합니다.
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
- GitHub에서 웹훅이 트리거 되면 CodeBuild가 동작합니다. 이때 SpringBoot의 소스 코드를 Docker 이미지로 빌드하고, 생성된 이미지를 ECR에 안전하게 업로드합니다. 
- CodeDeploy가 ECR에 저장된 Docker 이미지를 감지하고, ECS에 롤링 업데이트 방식을 사용하여 무중단 배포를 진행합니다. 
- 롤링 업데이트를 통해 새 버전의 애플리케이션을 점진적으로 배포하면서 서비스 중단 없이 업데이트를 완료할 수 있습니다.
<img width="1024" alt="ci-cd" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/7fa3c701-dfd7-46f3-9e4a-af7589ef9cb0">

<br/><br/>


## 🔶 ERD
### AQuery를 사용하여 레시피 서버의 ERD를 설계하였습니다.
<img width="1024" alt="레시피_ERD" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/31cbf837-b7a2-485a-b39d-2a9eb940b23d">

<br/><br/>


## 🔶 아키텍처
### 헥사고날 아키텍처 도입
<img width="1024" alt="레시피아_헥사고날" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/d02a5999-c348-42f3-8144-46902d0ec6a9">


### MSA의 DB 정합성 보장 과정
- 유저가 닉네임을 변경하면 멤버 서버에서는 닉네임 변경사항을 멤버 DB에 반영하고 Spring Event를 발행합니다. (이벤트 리스너는 2개를 선언) 
- 스프링 이벤트 리스너 중 1개가 동작하여 멤버 DB의 Outbox 테이블에 이벤트 발행 여부를 기록하고 DB 커밋을 합니다.(트랜잭션 커밋완료) 
- 트랜잭션 커밋이 완료되면 AFTER_COMMIT을 적어준 또 다른 스프링 이벤트 리스너가 동작하여 닉네임 변경 토픽으로 SNS 메시지를 발행합니다. 
- SNS 메시지가 발행되면 2개의 SQS리스너가 동시에 동작하게 됩니다. 
- 레시피 서버에서 닉네임 변경 토픽을 리스닝하고 있던 SQS가 실행됩니다. 
- 멤버 서버에서 닉네임 변경 토픽을 리스닝하고 있던 SQS가 실행됩니다. 이때 Outbox 테이블에 이벤트 발행 여부(published 칼럼)를 true로 업데이트합니다. 
- 레시피 서버의 SQS 리스너가 동작할 때 FeignClient를 사용하여 멤버 서버에 가장 최신의 유저 닉네임 정보를 요청합니다. 
- 멤버 서버로부터 받아온 가장 최신의 유저 닉네임 정보를 레시피 DB에 반영합니다.
<img width="1024" alt="spring-event" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/945f2187-aac3-4ee8-98cd-f181d20111f1">

### ZeroPayload 정책
- 데이터 전송시 메시지 내부에는 memberId만을 포함하도록 합니다.
- 분산추적을 위한 traceId는 SNS의 messageAttributes를 사용하여 전송합니다.
<img width="1024" alt="zero-payload" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/c5cad953-a027-4ac6-955f-9d1940bf8abf">

### 미발행된 SNS 메시지는 재발행
- Spring Batch를 사용하여 5분마다 미발행된 메시지를 재발행 합니다.
- Outbox 테이블에 저장된 발행여부(published)가 false것을 조회하여 배치가 동작합니다.
<img width="1024" alt="batch-event" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/466f5cc4-5510-4477-9ec2-711ce7c6a23b">

### Redis 데이터 복구전략 (RDBMS)
- Redis의 기본적인 복구전략은 AOF, RDB(Redis Database)를 사용하도록 했습니다.
- Redis에 저장된 조회수, 좋아요 데이터는 스케쥴러를 통해 매일 6시간마다 PostgreSQL에 업데이트하도록 설계했습니다.
    1. 6시간마다 백업을 수행하여 최근 상태의 데이터를 안정적으로 보존합니다.
    2. 만약 Redis 인스턴스에 문제가 생겨 데이터가 손실되더라도, PostgreSQL에 저장된 최대 6시간 전의 데이터로 복구가 가능합니다.
    3. 백업은 시스템 리소스를 상당히 사용할 수 있지만 6시간마다 백업을 실행하면 시스템이 피크 시간 외에 백업 작업을 처리할 수 있도록 일정을 조정할 수 있습니다.
<img width="1024" alt="Redis_복구" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/511a7d86-0265-4f44-9d71-cbd84c7a114f">


<br/><br/>

## 🔶 개발 전략
### 스프링 시큐리티를 통한 JWT 인증기능 구현
<img width="1024" alt="시큐리티_동작" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/8a3a5a99-4b5a-4eb6-a0a8-eb2921815f0a">


### 커스텀 예외처리 구현
<img width="1024" alt="레시피_커스텀_예외처리" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/92fb5261-c45d-42ab-9cb9-033b3ed5b2ed">


- 에러코드 작성
<img width="1024" alt="스크린샷 2024-02-10 오후 6 17 02" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/760e1cf0-3719-4429-9a79-bb624829db93">

- 레시피아 전용 예외코드 작성
<img width="1024" alt="스크린샷 2024-02-10 오후 6 18 33" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/a7f69ab0-be37-4303-bfa5-2a4a2ca4711f">

- 중앙집중 예외처리 ControllerAdvice 코드 작성 (이때 NPE는 따로 처리하도록 작성)
<img width="1024" alt="스크린샷 2024-02-10 오후 6 18 45" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/0bacdd2d-2b1b-4284-8c79-da38e38b1434">

- 코드에서 아래와 같이 예외를 throw해서 커스텀 예외를 적용시켰다.
<img width="1024" alt="스크린샷 2024-02-10 오후 6 20 46" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/afe9000d-7fbc-4b4f-93ff-a6b005a760e2">



<br/><br/>


## 🔶 기능 설명
### 레시피 생성
- 재료, 해시태그는 RDB에 저장과 동시에 MongoDB에 저장합니다.
- MongoDB에는 재료, 해시태그 정보를 저장하여 검색기능에서 연관검색어 기능이 동작하도록 구현했습니다.
<img width="1024" alt="레시피_작성" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/6eb41b27-c18f-4ba6-b69d-cbfc9ae6e47a">


### 레시피 상세조회
- 좋아요 횟수, 조회수는 Redis에 저장하고 조회합니다.
- S3에 저장된 이미지는 보안을 위해 pre-signed-url로 변환하여 사용자에게 보여줍니다.
<img width="1024" alt="레시피_상세조회" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/8d94cdc6-23c4-4cac-b53c-57804df7d825">

### 이미지 pre-signed-url 변환과정 설명
- 파일 테이블에는 S3버킷에 접근 가능한 이미지의 저장 경로(path)가 존재해야 합니다.
- AWS SDK를 통해 url을 pre-signed-url로 변환합니다. (S3에 접근하여 처리하고 반환받는게 아니라 스프링 내부의 SDK 코드로 처리)
<img width="1024" alt="pre-signed-url" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/453fe60e-275a-4b62-bdc0-dedbcfc515e6">


### 레시피 삭제
- 기본정보, 댓글, 파일은 soft delete 처리를 하도록 설계했습니다.
- soft delete된 데이터는 한달이 지나면 배치를 통해 삭제를 진행합니다.
<img width="1024" alt="레시피_삭제" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/c13149bf-6f14-43c8-92d8-bccda66c2621">


### 북마크
<img width="1024" alt="북마크_여부" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/3f7e6587-057c-4c4c-9ab4-3a1c4f2fdeda">

### 좋아요
- 분기처리를 통해 처리합니다.
<img width="1024" alt="좋아요_여부" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/d9a5a80c-a79a-4774-9d6c-8320ac7c48ac">



<br/><br/>


## 🔶 테스트 코드 작성
- 단위/통합 테스트 진행 (238개)
- Junit5, mockito, S3Mock, BddMockito 사용
- 외부 DB (MongoDB, Redis)를 사용한 테스트도 진행했습니다.

<img width="1024" alt="스크린샷 2024-02-10 오후 6 13 08" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/d46ca797-c4da-48fc-b8d1-c5ed0039533a">


<br/><br/>
## 🔶 성능(부하) 테스트: nGrinder 사용

### 상용 레시피 서버에 296명을 기준으로 부하 테스트를 진행했다.
- 상용 ECS의 레시피 SpringBoot서버에 테스트를 진행한 결과다. 
<img width="1024" alt="recipe-detail-test-296" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/b4054ea5-269c-41bc-b5f1-4fdab3c3360c">

### 로컬 vs 상용 성능 비교표
- **로컬 (Mac):** m1(16GB), m2 max(64GB)
- **상용 (ECS):** t4g.small 
- **테스트 결과:**
  - M2 Max 64GB 모델이 가장 높은 TPS(초당 처리 건수)를 기록하며 성능이 가장 우수하다.
  - M1 16GB 모델은 M2 Max에 비해 낮은 TPS를 기록했지만, 여전히 합리적인 성능을 보여준다.
  - 반면, AWS t4g.small 인스턴스는 매우 낮은 TPS를 기록하며 성능이 상대적으로 많이 부족한 것으로 나타났다. 
  - 이 결과에 따르면 만약 유저가 100명이 넘어갔다면 성능에 문제가 발생했을 것으로 보인다. 우리는 오토 스케일링을 적용했으니 실제로는 훨씬 잘 버티겠지만 유저가 늘어나면 적당히 사양을 높여주는것도 필요해보인다.
<img width="1024" alt="recipe-detail-test-result-table" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/6c5ab066-b3f9-48c7-8133-c01a7a8374cf">



<br/>
