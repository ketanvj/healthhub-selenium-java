package com.healthhub.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to MAX_RETRY_COUNT times before letting it count
 * as a real failure. This exists specifically for tests running against
 * HealthHub's ?_flaky= simulation param, it is NOT a substitute for fixing
 * a genuinely broken test, only for tolerating the deliberate
 * non-determinism the training app injects on purpose.
 *
 * Applied automatically to every test via TestListener's
 * IAnnotationTransformer, no need to add @Retry manually per test.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY_COUNT = 2;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.printf("Retrying '%s', attempt %d of %d%n",
                    result.getName(), retryCount, MAX_RETRY_COUNT);
            return true;
        }
        return false;
    }
}
