package com.ezhevikina.homework14.repository.aspect;

import com.ezhevikina.homework14.repository.FileAccountRepository;
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
public class RepositoryAspect {
    static final Logger logger = LogManager.getLogger(Repository.class);
    @Autowired
    Repository repository;

    @After("execution(* com.ezhevikina.homework14.repository.Repository.*(..))")
    private void logAfter(JoinPoint jp) {
        logger.info(String.format("log after execution of Repository.%s", jp.getSignature().getName()));
    }
}
