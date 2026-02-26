package hello.aop.pointcut;

import hello.aop.member.annotation.ClassAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
@Import({ AtTargetAtWithinTest.Config.class})
public class AtTargetAtWithinTest {

    @Autowired
    Child child;

    /**
     * ! 클래스에 붙은 어노테이션을 기준으로 포인트컷을 지정한다.
     *
     * @target : 명시된 클래스의 조상의 메서드까지 조인 포인트로 적용한다.
     * @within : 명시된 클래스 내에 있는 메서드만 조인 포인트로 적용한다.
     * @target, @within 지시자는 뒤에 설명할 파라미터 바인딩에서 함께 사용된다.
     */
    @Test
    void success() {
        // parentMethod() 는 Parent 클래스에만 정의되어 있고, Child 클래스에 정의되어 있지 않기
        // 때문에 AOP의 적용 대상이 되지 못했다.
        log.info("child Proxy = {}", child.getClass());
        child.childMethod();
        child.parentMethod();   // 부모에만있는 메서드
    }

    static class Config {

        @Bean
        public Parent parent() {
            return new Parent();
        }

        @Bean
        public Child child() {
            return new Child();
        }

        @Bean
        public AtTargetAtWithinAspect atTargetAtWithinAspect() {
            return new AtTargetAtWithinAspect();
        }
    }

    static class Parent {
        public void parentMethod() {
            // 부모에만 있는 메서드
        }
    }

    @ClassAop
    static class Child extends Parent {
        public void childMethod() {
            // 자식 에만 있는 메서드
        }
    }

    @Slf4j
    @Aspect
    static class AtTargetAtWithinAspect {
        @Around("execution(* hello.aop..*.*(..)) && @target(hello.aop.member.annotation.ClassAop)")
        public Object atTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@target] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        @Around("execution(* hello.aop..*.*(..)) && @within(hello.aop.member.annotation.ClassAop)")
        public Object atWithin(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@within] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
