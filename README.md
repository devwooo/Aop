**JoinPoint 인터페이스의 주요 기능**
`getArgs()`: 메서드 인수를 반환합니다.
`getThis()`: 프록시 객체를 반환합니다.
`getTarget()`: 대상 객체를 반환합니다.
`getSignature()`: 조언되는 메서드에 대한 설명을 반환합니다.
`toString()`: 조언되는 방법에 대한 유용한 설명을 인쇄합니다.

**ProceedingJoinPoint 인터페이스의 주요 기능**
`proceed()`: 다음 어드바이스나 타켓을 호출한다


**@AfterReturning** 메서드 실행이 정상적으로 반환될 때 실행
@AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()",
returning = "result")
#### returning 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
#### returning 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다. 절에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
#### @Around 와 다르게 반환되는 객체를 변경할 수는 없다. 반환 객체를 변경하려면 참고로 반환 객체를 조작할 수 는 있다.


**@AfterThrowing** 메서드 실행이 예외를 던져서 종료될 때 실행
@AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()",
throwing = "ex")
#### throwing 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
#### throwing 절에 지정된 타입과 맞는 예외를 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)