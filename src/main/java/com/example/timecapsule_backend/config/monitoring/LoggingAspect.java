package com.example.timecapsule_backend.config.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.timecapsule_backend.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("[Service] {} executed in {}ms", methodName, executionTime);
            return result;
        } catch (Exception e) {
            log.error("[Service] {} failed: {}", methodName, e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.example.timecapsule_backend.controller..*Controller.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("[Controller] {} executed in {}ms", methodName, executionTime);
            return result;
        } catch (Exception e) {
            log.error("[Controller] {} failed: {}", methodName, e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.example.timecapsule_backend.service.scheduler.CapsuleSchedulerService.*(..))")
    public Object logSchedulerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("[Scheduler] {} executed in {}ms", methodName, executionTime);
            return result;
        } catch (Exception e) {
            log.error("[Scheduler] {} failed: {}", methodName, e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.example.timecapsule_backend.service.delivery.DeliveryService+.*(..))")
    public Object logDeliveryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("[Delivery] {} executed in {}ms", methodName, executionTime);
            return result;
        } catch (Exception e) {
            log.error("[Delivery] {} failed: {}", methodName, e.getMessage());
            throw e;
        }
    }

    private void logExecutionTime(String methodName, long executionTime) {
    }

    private void logMethodParameters(String methodName, Object[] args) {
    }

    private void logException(String methodName, Exception exception) {
    }
}