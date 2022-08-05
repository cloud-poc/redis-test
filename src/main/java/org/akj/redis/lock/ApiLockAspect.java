package org.akj.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.akj.redis.entity.ApiLock;
import org.akj.redis.entity.LockStatus;
import org.akj.redis.service.ApiLockService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class ApiLockAspect {

    private final ApiLockService apiLockService;

    public ApiLockAspect(ApiLockService apiLockService) {
        this.apiLockService = apiLockService;
    }

    @Pointcut("@annotation(ApiLock)")
    public void pointcut() {
    }

    @Around("pointcut()")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ApiLock lock = null;
        try {
            log.debug("start lock aspect.");
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) attributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            lock = ApiLock.builder().url(request.getRequestURI()).businessDate(LocalDate.now())
                    .apiKey(getMethodIdentifier(proceedingJoinPoint.getTarget().getClass(), signature.getMethod())).build();

            apiLockService.tryLock(lock);
            Object proceed = proceedingJoinPoint.proceed();

            log.debug("finished lock aspect.");
            return proceed;
        } catch (Throwable e) {
            throw e;
        } finally {
            if (null != lock) {
                apiLockService.unlock(lock);
            }
        }
    }

    private String getMethodIdentifier(Class clazz, Method method) {
        StringBuilder builder = new StringBuilder();
        String prefix = clazz.getName();
        builder.append(prefix)
                .append("#")
                .append(method.getName())
                .append("(");

        Stream.of(method.getParameters()).forEach(p -> builder.append(p.getType().getName()).append(","));
        String string = builder.toString();
        string = string.endsWith(",") ? string.substring(0, string.length() - 1) : string;

        return string + ")";
    }
}
