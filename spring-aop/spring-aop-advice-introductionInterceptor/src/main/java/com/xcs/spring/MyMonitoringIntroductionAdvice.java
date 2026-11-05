package com.xcs.spring;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

public class MyMonitoringIntroductionAdvice extends DelegatingIntroductionInterceptor implements MyMonitoringCapable {

    private boolean active = false;

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void toggleMonitoring() {
        setActive(true);
    }

    // When a monitored method is called and monitoring is active, print logs
    @Override
    protected Object doProceed(MethodInvocation mi) throws Throwable {
        if (this.active) {
            System.out.println("[Start monitoring " + mi.getMethod().getName() + "]");
            long startTime = System.currentTimeMillis();
            Object result = super.doProceed(mi);
            long endTime = System.currentTimeMillis();
            System.out.println("[End monitoring " + mi.getMethod().getName() + "] Time taken: " + (endTime - startTime) + " ms");
            return result;
        }
        return super.doProceed(mi);
    }
}
