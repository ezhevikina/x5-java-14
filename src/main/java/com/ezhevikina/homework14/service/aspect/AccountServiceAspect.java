package com.ezhevikina.homework14.service.aspect;

import com.ezhevikina.homework14.repository.Repository;
import com.ezhevikina.homework14.service.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AccountServiceAspect {
    static final Logger logger = LogManager.getLogger(Repository.class);
    @Autowired
    AccountService manager;

    @Around("execution(* com.ezhevikina.homework14.service.AccountService.*(..))")
    private void logAround(ProceedingJoinPoint jp) throws Throwable {
        logger.info(String.format("log before execution of AccountService.%s", jp.getSignature().getName()));

        jp.proceed();
        logger.info(String.format("log after execution of AccountService.%s", jp.getSignature().getName()));
    }

    @AfterThrowing(pointcut = "execution(* com.ezhevikina.homework14.service.AccountService.*(..))",
            throwing = "exception")
    public void logAfterException(JoinPoint jp, Exception exception) {
        logger.info(exception.getMessage());
        logger.info(String.format("log after execution of AccountService.%s", jp.getSignature().getName()));
    }
}
