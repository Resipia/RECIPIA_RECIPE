INSERT INTO recipe (member_id, recipe_nm, recipe_desc, time_taken, ingredient, hashtag, like_count, del_yn, create_dttm, update_dttm)
VALUES (1, '김치찌개', '매콤한 김치찌개 레시피', 30, '김치, 돼지고기, 두부', '#김치찌개', 0, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO nutritional_info (carbohydrates, protein, fat, vitamins, minerals, recipe_id)
VALUES (10, 10, 10, 10, 10, 1);


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

-- 댓글 추가
INSERT INTO comment (recipe_id, member_id, comment_text, del_yn, create_dttm, update_dttm)
VALUES (1, 1, 'comment', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 대댓글 추가
INSERT INTO subcomment (comment_id, member_id, subcomment_text, del_yn, create_dttm, update_dttm)
VALUES (1, 1, 'first-subComment', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, 2, 'second-subComment', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- 닉네임 추가
INSERT INTO nickname (member_id, nickname)
VALUES (1, 'member-1-nickname'),
       (2, 'member-2-nickname');