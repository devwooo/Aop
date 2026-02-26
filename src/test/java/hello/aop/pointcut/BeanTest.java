package hello.aop.pointcut;

import hello.aop.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
@Import({BeanTest.BeanAspect.class})
public class BeanTest {

    @Autowired
    OrderService orderService;

    @Test
    void success() {
        log.info("orderService Proxy = {}", orderService.getClass());
        orderService.orderItem("itemA");
    }

    /**
     * 스프링 Bean 이름으로 AOP 적용여부를 지정한다.
     * 아래의 예제는, OrderService, *Repository 의 메서드에 AOP가 적용된다.
     */
    @Aspect
    static class BeanAspect {
        @Around("bean(orderService) || bean(*Repository)")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[bean] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }

}
