# 🍳 RECIPIA_RECIPE

**레시피아 서비스의 [레시피 서버]**

배포(원스토어): https://m.onestore.co.kr/mobilepoc/apps/appsDetail.omp?prodId=0000774118 
<br/>
소개 페이지 (노션): https://upbeat-willow-06b.notion.site/7ece9e5f602a43f583d7f4cf101e7d69?pvs=4

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
1. 프로젝트는 MSA로 구성되어있으며 ECS 클러스터에 Recipe서버를 구축하였습니다.
2. 외부 서비스는 RDS(PostgreSQL), Redis, MongoDB를 사용합니다.
<img width="1076" alt="스크린샷 2024-02-10 오후 3 45 08" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/2a400b2d-6ebf-4505-8a76-2aa0142b7205">

3. ECS인프라는 다음과 같이 ECR에 저장된 스프링부트 이미지를 받아서 컨테이너에 띄운다.
<img width="923" alt="aws-infra" src="https://github.com/Resipia/RECIPIA_RECIPE/assets/79524077/95564592-81c7-4e0f-a257-e4015b3f2f3c">


<br/>

## 🔶 아키텍처, 기술 스택



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


#### 로그인
