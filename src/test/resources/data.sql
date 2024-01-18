INSERT INTO recipe (member_id, recipe_nm, recipe_desc, time_taken, ingredient, hashtag, del_yn, create_dttm, update_dttm)
VALUES (1, '김치찌개', '매콤한 김치찌개 레시피', 30, '김치, 돼지고기, 두부', '#김치찌개', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO recipe_like_count (recipe_id, like_count, create_dttm, update_dttm)
VALUES (1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO nutritional_info (carbohydrates, protein, fat, vitamins, minerals, recipe_id)
VALUES (10, 10, 10, 10, 10, 1);


INSERT INTO category (category_nm, sort_no, del_yn, create_dttm, update_dttm)
VALUES ('한식', 1, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('분식', 2, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('일식', 3, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('양식', 4, 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- 한식 서브 카테고리 추가
INSERT INTO sub_category (sub_category_nm, del_yn, create_dttm, update_dttm)
VALUES ('냉면', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('국밥', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('한식3', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('한식4', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- category_sub_map 테이블에 데이터 삽입
INSERT INTO public.category_sub_map (category_id, sub_category_id, create_dttm)
VALUES
    (1, 1, CURRENT_TIMESTAMP),
    (1, 2, CURRENT_TIMESTAMP),
    (1, 3, CURRENT_TIMESTAMP),
    (1, 4, CURRENT_TIMESTAMP);


INSERT INTO recipe_category_map (recipe_id, sub_category_id, create_dttm)
VALUES (1, 1, CURRENT_TIMESTAMP),
       (1, 2, CURRENT_TIMESTAMP),
       (1, 3, CURRENT_TIMESTAMP);

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