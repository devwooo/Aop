package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ExecutionTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
        //helloMethod=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod={}", helloMethod);
    }

    /**
     * execution문법
     * execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name pattern(param-pattern) throws-pattern?)
     * execution(접근제어자? 반환타입 선언타입(패키지명)?메서드이름(파라미터) 예외?)
     * ?는 생략가능, * 같은 패턴을 지정 할 수 있다.
     * 
     * pointcut.matches(메서드, 대상 클래스) : 메서드가 대상 클래스에 매칭되는지
     */
    @Test
    void exactMatch() {
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 가장 많이 생략한 포인트컷
     */
    @Test
    void allMatch() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 메서드 이름 매칭 관련 포인트컷
     * 메서드 이름 앞뒤로 *를 사용하여 매칭시킬수 있다.
     */
    @Test
    void nameMatch() {
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStart1() {
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStart2() {
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchFalse() {
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 패키지 매칭 관련 포인트컷
     */
    @Test
    void packageExactMatch1() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactMatch2() {
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactMatchFalse() {
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 메서드 앞에 *는 클래스명이라고 생각하자
     * [*.[*(..) 메서드명]클래스명]
     */
    @Test
    void packageMatchSubPackage1() {
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatchSubPackage2() {
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 타입 매칭 - 부모 타입도 허용
     */
    //pointcut.setExpression("execution()");
    @Test
    void typeExactMatch() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * helloMethod 는 MemberServiceImpl 가 MemberService 를 구현해서 오버라이딩한 함수이다.
     */
    @Test
    void typeMatchSuperType() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 타입 매칭 - 부모 타입에 있는 메서드만 허용
     */
    @Test
    void typeMatchInternal() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 포인트 컷에서 **부모타입을 선언한 경우 부모에 없는 메서드는 매칭되지 않는다.**
     * !부모의 메서드를 오버라이딩 하지 않고, 자식에만 있는 메서드는 매칭 되지 않음
     */
    @Test
    void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 파라미터 매칭
     * (String): 정확하게 String 타입 파라미터
     * () : 파라미터가 없어야 한다.
     * (*) : 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
     * (*, *) : 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
     * (..) : 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. 참고로 파라미터가 없어도 된다. `
     * 0..*로 이해하면 된다.
     * (String, ..) : String 타입으로 시작해야 한다. 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
     * 예) (String), (String, Xxx), (String, Xxx, Xxx)허용
     */

    /**
     * (String) 스트링 타입 파라미터 허용
     */
    @Test
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * () : 파라미터가 없는경우
     */
    @Test
    void argsMatchNoArgs() {
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 정확히 하나의 파라미터, 모든 타입허용
     * (Xxx)
     */
    @Test
    void argsMatchStar() {
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 숫자와 무관하게 모든 파라미터, 모든 타입허용
     * 파라미터가 없어도됨
     * (), (Xxx), (Xxx, Xxx)
     */
    @Test
    void argsMatchAll() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     *  String 타입으로 시작, 숫자와 무관하게 모든파라미터, 모든 타입허용
     *  //(String), (String, Xxx), (String, Xxx, Xxx) 허용
     */
    @Test
    void argsMatchComplex() {
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


}
