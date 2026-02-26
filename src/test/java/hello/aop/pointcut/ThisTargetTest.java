package hello.aop.pointcut;

import hello.aop.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * application.properties
 * spring.aop.proxy-target-class=true CGLIB
 * spring.aop.proxy-target-class=false JDK 동적 프록시
 */
@Slf4j
@Import(ThisTargetTest.ThisTargetAspect.class)
//@SpringBootTest(properties = "spring.aop.proxy-target-class=false") //JDK 동적 프록시
@SpringBootTest(properties = "spring.aop.proxy-target-class=true") //CGLIB
public class ThisTargetTest {

    /**
     * this : 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
     * target :  Target 객체(스프링 AOP 프록시가 가리키는 실제 대상)를 대상으로 하는 조인 포인트
     * 적용 타입을 정확하게 지정해야한다.
     * '*' 패턴사용 사용 불가능, 부모타입을 허용
     * ex)  this(hello.aop.member.MemberService)
     * target(hello.aop.member.MemberService)
     */

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ThisTargetAspect {
        //부모 타입 허용

        /**
         * this의 포인트컷에 걸리는 객체는 MemberService 를 보고 구현한 프록시 객체가되고
         * 프록시가 MemberService 타입이냐?
         */
        @Around("this(hello.aop.member.MemberService)")
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws
                Throwable {
            log.info("[this-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        //부모 타입 허용

        /**
         * target의 포인트컷에 걸리는 객체는 MemberService를 직접 구현한 MemberServiceImpl이 된다.
         * 실제 객체가 MemberService 타입이냐?
         */
        @Around("target(hello.aop.member.MemberService)")
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws
                Throwable {
            log.info("[target-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * this는 Interface를 보고 생성된 프록시이므로 MemberServiceImpl 의 존재유무를 알수 없기 때문에
         * 해당 AOP를 타지 않는다.
         */
        @Around("this(hello.aop.member.MemberServiceImpl)")
        public Object doThis(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[this-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * 실제 target 객체 대상
         */
        @Around("target(hello.aop.member.MemberServiceImpl)")
        public Object doTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}

