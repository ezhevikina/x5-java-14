package com.ezhevikina.homework14.controller.aspect;

import com.ezhevikina.homework14.controller.AccountController;
import com.ezhevikina.homework14.repository.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AccountControllerAspect {
    static final Logger logger = LogManager.getLogger(Repository.class);
    @Autowired
    AccountController controller;

    @After("execution(* com.ezhevikina.homework14.controller.AccountController.*(..))")
    private void logBefore(JoinPoint jp) {
        logger.info(String.format("log before execution of AccountController.%s", jp.getSignature().getName()));
    }
}
