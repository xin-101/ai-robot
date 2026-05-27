package io.github.zh.aspect;

//时间切面

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@Slf4j
public class ServiceLogTimeAspect {

    @Around("execution(* io.github.zh.service.impl..*.*(..))")
    public Object logTime(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object proceed = joinPoint.proceed();
        /*获取执行方法名称 */
        String methodName = joinPoint.getTarget().getClass().getName()
                + "."
                + joinPoint.getSignature().getName();

        stopWatch.stop();

        log.info("执行：{},耗时：{}",methodName,stopWatch.getTotalTimeMillis());

        return proceed;
    }


}
