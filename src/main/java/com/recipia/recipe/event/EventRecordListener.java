package com.recipia.recipe.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipia.recipe.config.aws.AwsSnsConfig;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.event.RecipeEventRecord;
import com.recipia.recipe.domain.repository.RecipeEventRecordRepository;
import com.recipia.recipe.domain.repository.RecipeRepository;
import com.recipia.recipe.exception.ApiErrorCodeEnum;
import com.recipia.recipe.utils.CustomJsonBuilder;
import com.recipia.recipe.utils.RecipeStringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class EventRecordListener {

    private final RecipeRepository recipeRepository;
    private final RecipeEventRecordRepository recipeEventRecordRepository;
    private final AwsSnsConfig awsSnsConfig;
    private final CustomJsonBuilder customJsonBuilder;

    /**
     * 이벤트를 호출한 서비스 코드의 트랜잭션과 묶이게 된다.
     */
    @Transactional
    @EventListener
    public void listen(RecipeNameChange event) throws JsonProcessingException {
        // 여기서 db에 저장하는 로직 실행 (트랜잭션이 묶여있어야 함)
        Recipe recipe = recipeRepository.findById(event.recipeId()).orElseThrow(() -> new RuntimeException(ApiErrorCodeEnum.MEMBER_SERVICE_ERROR.getMessage()));

        // JSON 객체 생성 및 문자열 변환
        String messageJson = customJsonBuilder.add("recipeId", recipe.getId().toString()).build();

        String topicName = RecipeStringUtils.extractLastPart(awsSnsConfig.getSnsTopicNicknameChangeARN());

        RecipeEventRecord memberEventRecord = RecipeEventRecord.of(
                recipe,
                topicName,
                "NicknameChangeEvent",
                messageJson,
                false,
                null
        );

        recipeEventRecordRepository.save(memberEventRecord);
    }



}

