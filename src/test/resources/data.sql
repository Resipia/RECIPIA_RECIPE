INSERT INTO recipe (member_id, recipe_nm, recipe_desc, time_taken, ingredient, hashtag, nickname,
                    create_dttm, update_dttm, del_yn)
VALUES (1, '김치찌개', '매콤한 김치찌개 레시피', 30, '김치, 돼지고기, 두부', '#김치찌개', '진아',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (1, '된장찌개', '구수한 된장찌개 레시피', 25, '된장, 두부, 야채', '#된장찌개', '민준',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (1, '볶음밥', '간단한 볶음밥 레시피', 15, '밥, 야채, 계란', '#볶음밥', '서윤',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (2, '파스타', '크림 파스타 레시피', 20, '파스타, 크림소스, 베이컨', '#파스타', '지후',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (3, '스테이크', '주말에 즐기는 스테이크', 40, '소고기, 감자, 샐러드', '#스테이크', '하은',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (4, '라면', '간단하게 끓이는 라면', 10, '라면, 계란, 파', '#라면', '유진',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (5, '피자', '집에서 만드는 피자', 50, '피자도우, 치즈, 토마토소스', '#피자', '도윤',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (6, '샐러드', '건강한 샐러드 레시피', 15, '야채, 닭가슴살, 드레싱', '#샐러드', '시우',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (7, '토스트', '아침에 먹기 좋은 토스트', 10, '식빵, 계란, 햄', '#토스트', '은우',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'),
       (8, '죽', '몸에 좋은 죽 끓이기', 35, '쌀, 야채, 해산물', '#죽', '수아',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N');


INSERT INTO category (category_nm, sort_no, del_yn, create_dttm, update_dttm)
VALUES ('한식', 1, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('중식', 2, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('일식', 3, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('양식', 4, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- 한식 서브 카테고리 추가
INSERT INTO sub_category (category_id, sub_category_nm, sort_no, del_yn, create_dttm, update_dttm)
VALUES (1, '김치찌개', 1, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, '된장찌개', 2, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, '비빔밥', 3, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, '불고기', 4, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- 추가적인 한식 서브 카테고리 ...

-- 중식 서브 카테고리 추가
INSERT INTO sub_category (category_id, sub_category_nm, sort_no, del_yn, create_dttm, update_dttm)
VALUES (2, '짜장면', 1, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, '짬뽕', 2, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, '마파두부', 3, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, '꿔바로우', 4, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- 추가적인 중식 서브 카테고리 ...

-- 일식 서브 카테고리 추가
INSERT INTO sub_category (category_id, sub_category_nm, sort_no, del_yn, create_dttm, update_dttm)
VALUES (3, '초밥', 1, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, '라멘', 2, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, '돈부리', 3, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, '우동', 4, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- 추가적인 일식 서브 카테고리 ...

-- 양식 서브 카테고리 추가
INSERT INTO sub_category (category_id, sub_category_nm, sort_no, del_yn, create_dttm, update_dttm)
VALUES (4, '파스타', 1, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, '스테이크', 2, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, '피자', 3, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, '샐러드', 4, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- 추가적인 양식 서브 카테고리 ...

INSERT INTO recipe_category_map (recipe_id, sub_category_id, create_dttm)
VALUES (1, 5, CURRENT_TIMESTAMP),
       (1, 6, CURRENT_TIMESTAMP),
       (1, 7, CURRENT_TIMESTAMP);
