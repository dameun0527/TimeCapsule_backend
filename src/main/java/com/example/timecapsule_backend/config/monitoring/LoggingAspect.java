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
        return null;
    }

    @Around("execution(* com.example.timecapsule_backend.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return null;
    }

    @Around("execution(* com.example.timecapsule_backend.service.SchedulerService.*(..))")
    public Object logSchedulerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return null;
    }

    @Around("execution(* com.example.timecapsule_backend.service.DeliveryService(..))")
    public Object logDeliveryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return null;
    }

    private void logExecutionTime(String methodName, long executionTime) {
    }

    private void logMethodParameters(String methodName, Object[] args) {
    }

    private void logException(String methodName, Exception exception) {
    }
}