package me.selim.mesh.web.rest.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricsAspect {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MetricsAspect.class);

    private final MeterRegistry meterRegistry;

    public MetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("execution(* me.selim.mesh.web.rest..*.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        Timer timer = meterRegistry.timer("method.execution.time", "method", methodName);
        return timer.recordCallable(() -> {
            try {
                return pjp.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
    }
}
