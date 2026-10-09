package com.healthhub.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.IExecutionListener;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Registered in testng.xml. Two jobs:
 *   1. IAnnotationTransformer: attaches RetryAnalyzer to every @Test
 *      automatically, so individual test classes never need to remember
 *      `retryAnalyzer = RetryAnalyzer.class` themselves.
 *   2. IExecutionListener: simple start/finish banner in the console output,
 *      useful when reading a live run during a demo.
 */
public class TestListener implements IAnnotationTransformer, IExecutionListener {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                           Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public void onExecutionStart() {
        System.out.println("=== HealthHub Selenium suite starting ===");
    }

    @Override
    public void onExecutionFinish() {
        System.out.println("=== HealthHub Selenium suite finished ===");
    }
}
