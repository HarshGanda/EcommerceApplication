package com.ecommerce.catalog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* com.ecommerce.catalog.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Executing: {} with arguments: {}",
            joinPoint.getSignature().getName(),
            joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.ecommerce.catalog.service..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method {} executed successfully", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "execution(* com.ecommerce.catalog..*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Exception in {} with message: {}",
            joinPoint.getSignature().getName(),
            error.getMessage());
    }
}

