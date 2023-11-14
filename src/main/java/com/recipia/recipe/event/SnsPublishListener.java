package com.recipia.recipe.event;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipia.recipe.aws.AwsSnsService;
import com.recipia.recipe.utils.CustomJsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class SnsPublishListener {

    private final AwsSnsService awsSnsService;
    private final CustomJsonBuilder customJsonBuilder;

    /**
     * 이벤트를 호출한 서비스 코드의 트랜잭션과 묶여있지 않고 트랜잭션이 commit된 후에 동작한다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void snsListen(RecipeNameChange event) throws JsonProcessingException {

        String messageJson = customJsonBuilder.add("recipeId", event.recipeId().toString()).build();
        awsSnsService.publishNicknameToTopic(messageJson);
    }

}

