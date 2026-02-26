package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.MethodAop;
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
@Import({AtAnnotationTest.AtAnnotationAspect.class})
public class AtAnnotationTest {

    @Autowired
    MemberService memberService;

    /**
     * @annotation 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
     * MemberServiceImpl 의 hello 메서드에 @MethodAop가 붙어있다
     *
     * 포인트컷 @annotation("여기 값을")  매개변수의 타입과 매칭시킬수 있다.
     *
     * @args : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
     * 전달된 인수의 런타임 타입에 @Check 애노테이션이 있는 경우에 매칭한다.
     */


    @Test
    void success() {
        log.info("memberService Proxy = {}", memberService.getClass());
        memberService.hello("helloA");

    }

    @Slf4j
    @Aspect
    static class AtAnnotationAspect {
        @Around("@annotation(hello.aop.member.annotation.MethodAop)")
        public Object doAtAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@annotation] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * 매개변수로 포인트컷의 클래스를 지정해줄수 있다.
         */
        @Around("@annotation(methodAop)")
        public Object doSomething(ProceedingJoinPoint joinPoint, MethodAop methodAop) throws Throwable {
            String annotationValue = methodAop.value();
            System.out.println("annotation value = " + annotationValue);
            return joinPoint.proceed();
        }
    }
}
