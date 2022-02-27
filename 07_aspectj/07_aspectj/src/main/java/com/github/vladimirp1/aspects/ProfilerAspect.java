package com.github.vladimirp1.aspects;

import com.github.vladimirp1.profiler.ProfilerImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ProfilerAspect {
    @Pointcut("execution(@com.github.vladimirp1.annotations.Profile * *(..))")
    public void interestingCode() {
    }

    @Pointcut("execution(@com.github.vladimirp1.annotations.GetProfiler * *(..))")
    public void getProfiler() {
    }

    @Around("interestingCode()")
    public Object aroundInterestingCode(ProceedingJoinPoint point) throws Throwable {
        long timeBefore = System.nanoTime();
        Object ret = point.proceed();
        long timeAfter = System.nanoTime();
        profiler.Record(point.getSignature(), timeAfter - timeBefore);
        return ret;
    }

    @Around("getProfiler()")
    public Object aroundGetProfiler(ProceedingJoinPoint point) {
        return profiler;
    }

    private ProfilerImpl profiler = new ProfilerImpl();
}