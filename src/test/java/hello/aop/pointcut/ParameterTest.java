package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImplTest;
import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
@Import({ParameterTest.ParameterAspect.class})
public class ParameterTest {

    @Autowired
    MemberService memberService;

//    @Autowired
//    MemberServiceImplTest memberServiceImplTest;
    /**
     * 포인트컷 표현식을 사용해서 어드바이스에 매개변수를 전달할 수 있다.
     * this, target, args,@target, @within, @annotation, @args
     * <p>
     * 포인트컷의 !이름! 과 !매개변수의 이름!을 맞춰야 한다.
     * 포인트컷의 타입은, 메서드에 지정한 타입으로 제한된다.
     */
    @Test
    void success() {
        memberService.hello("helloA");
    }

    @Test
    void success2() {
        /**
         * MemberServiceImpl @ClassAop 제거 후 MemberServiceImplTest (MemberServiceImpl 상속함) 에 @ClassAop 를 붙일경우
         * @Before("allMember() && @within(annotation)") 이 포인트컷에서 MemberServiceImplTest 에만 존재하는 메서드에서만 AOP가 호출되고
         * @Before("allMember() && @target(annotation)") 인경우  MemberServiceImplTest 에 존재하지 않는 부모에 존재하는 메서드에서도 AOP가 호출됨
         */
//        memberServiceImplTest.hello("helloA");
//        memberServiceImplTest.subInternal("subInternal");
    }


    @Slf4j
    @Aspect
    static class ParameterAspect {
        @Pointcut("execution(* hello.aop.member..*.*(..))")
        private void allMember() {
        }

        /**
         * joinPoint.getArgs()[0] 와 같이 매개변수를 전달 받는다
         */
        @Around("allMember()")
        public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable {
            Object args = joinPoint.getArgs()[0];
            log.info("[logArgs1] {}, arg={}", joinPoint.getSignature(), args);
            return joinPoint.proceed();
        }

        /**
         * args(args, ..) 와 같이 매개변수를 전달 받는다.
         */
        // 포인트컷과, 매개변수의 이름을 맞춰야 한다.
        @Around("allMember() && args(args, ..)")
        public Object logArgs2(ProceedingJoinPoint joinPoint, Object args) throws Throwable {
            log.info("[logArgs2] {}, arg={}", joinPoint.getSignature(), args);
            return joinPoint.proceed();
        }

        /**
         * @Before를 사용한 축약 버전이다. 추가로 타입을 String으로 제한했다.
         *
         */
        @Before("allMember() && args(arg, ..)")
        public void logArg3(String arg) {
            log.info("[logArg3] {}", arg);
        }

        /**
         * this -> 타켓의 프록시 객체(프록시 객체를 전달 받는다.)
         */
        @Before("allMember() && this(obj)")
        public void thisArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[this]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        /**
         * target -> 실제 타켓 객체( 실제 대상 객체를 전달 받는다.)
         */
        @Before("allMember() && target(obj)")
        public void targetArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[target]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        /**
         * @target 타입의 애노테이션을 전달 받는다.
         */
        @Before("allMember() && @target(annotation)")
        public void atTarget(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@target]{}, obj={}]", joinPoint.getSignature(), annotation);
        }

        /**
         * @within 타입의 애노테이션을 전달 받는다.
         */
        @Before("allMember() && @within(annotation)")
        public void atWithin(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@within]{}, obj={}]", joinPoint.getSignature(), annotation);
        }

        /**
         * @annotation 메서드의 애노테이션을 전달 받는다. annotaion.valu() 로 해당 메서드에 지정된 값을 출력할수 있다
         */
        @Before("allMember() && @annotation(annotation)")
        public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
            log.info("[@annotation]{}, obj={}]", joinPoint.getSignature(), annotation.value());
        }
    }
}
