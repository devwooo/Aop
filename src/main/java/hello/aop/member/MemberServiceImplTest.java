package hello.aop.member;

import hello.aop.member.annotation.ClassAop;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

//@Primary
//@Component
//@ClassAop
public class MemberServiceImplTest extends MemberServiceImpl {
    public String subInternal(String param) {
        return "subInternal";
    }
}
