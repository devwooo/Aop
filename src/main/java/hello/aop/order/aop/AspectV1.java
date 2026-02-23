package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public class AspectV1 {
    /**
     * @Aspect 어노테이션 어드바이저 생성
     * 모든 어드바이스는 JoinPoint를 첫번째 파라미터로 사용할 수 있고, 생략가능
     * 단 @Around는 ProceedingJoinPoint를 사용해야한다. 
     *      JoinPoint   관계
     *         ^
     *         |
     * ProceedingJoinPoint
     *
     *
     *
     *
     *
     */
    @Around("execution(* hello.aop.order..*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }
}
