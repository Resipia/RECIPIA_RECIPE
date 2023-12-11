package com.recipia.recipe.hexagonal.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
@Aspect
public class LoggingAspect {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Around("execution(* com.recipia.recipe.controller..*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.currentTimeMillis();

            String formattedStartTime = dateFormat.format(new Date(startTime));
            String formattedEndTime = dateFormat.format(new Date(endTime));

            log.info(" ===== Method: " + joinPoint.getSignature().getName() + " ===== ");
            log.info(" ===== Parameters: " + Arrays.toString(joinPoint.getArgs()) + " ===== ");
            log.info(" ===== Start Time: " + formattedStartTime + " ===== ");
            log.info(" ===== End Time: " + formattedEndTime + " ===== ");
            log.info(" ===== Duration: " + (endTime - startTime) + "ms ===== ");
        }

    }

}
