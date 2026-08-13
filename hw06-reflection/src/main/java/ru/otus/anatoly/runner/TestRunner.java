package ru.otus.anatoly.runner;

import lombok.NonNull;
import ru.otus.anatoly.annotation.After;
import ru.otus.anatoly.annotation.Before;
import ru.otus.anatoly.annotation.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void run(@NonNull Class<?> testClass) {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        
        try {
            Object testInstance;
            try {
                testInstance = testClass.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                System.out.println("Error: class " + testClass.getSimpleName() + " has no no-argument constructor");
                return;
            }

            // Find all methods with annotation
            List<Method> beforeMethods = findAnnotatedMethods(testClass, Before.class);
            List<Method> afterMethods = findAnnotatedMethods(testClass, After.class);
            List<Method> testMethods = findAnnotatedMethods(testClass, Test.class);

            System.out.println("Running " + testMethods.size() + " tests from class: " + testClass.getSimpleName());
            System.out.println("-------------------------------------------------------");

            // Run each test method with Before -> Test -> After cycle
            for (Method testMethod : testMethods) {
                runTestMethod(testInstance, beforeMethods, afterMethods, testMethod);
            }

            System.out.println("-------------------------------------------------------");
            System.out.println("Test Statistics:");
            System.out.println("  Total tests: " + totalTests);
            System.out.println("  Passed:      " + passedTests);
            System.out.println("  Failed:      " + failedTests);

        } catch (Exception e) {
            System.out.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runTestMethod(Object testInstance, List<Method> beforeMethods, List<Method> afterMethods, Method testMethod) {
        totalTests++;
        
        // @Before methods
        for (Method beforeMethod : beforeMethods) {
            try {
                makeAccessible(beforeMethod);
                beforeMethod.invoke(testInstance);
            } catch (Exception e) {
                System.out.println("  [BEFORE] " + testMethod.getName() + " - FAILED: " + e.getMessage());
            }
        }

        // @Test method
        try {
            makeAccessible(testMethod);
            testMethod.invoke(testInstance);
            passedTests++;
            System.out.println(GREEN + "✅ [PASS] " + testMethod.getName() + RESET);
        } catch (InvocationTargetException e) {
            failedTests++;
            Throwable cause = e.getCause();
            System.out.println(RED + "❌ [FAIL] " + testMethod.getName() + " - " + cause.getClass().getSimpleName() + ": " + cause.getMessage() + RESET);
        } catch (Exception e) {
            failedTests++;
            System.out.println(RED + "❌ [FAIL] " + testMethod.getName() + " - " + e.getClass().getSimpleName() + ": " + e.getMessage() + RESET);
        }

        // @After methods
        for (Method afterMethod : afterMethods) {
            try {
                makeAccessible(afterMethod);
                afterMethod.invoke(testInstance);
            } catch (Exception e) {
                System.out.println("  [AFTER] " + testMethod.getName() + " - FAILED: " + e.getMessage());
            }
        }
    }

    private static List<Method> findAnnotatedMethods(Class<?> clazz, Class<? extends java.lang.annotation.Annotation> annotationType) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationType)) {
                methods.add(method);
            }
        }
        return methods;
    }

    private static void makeAccessible(Method method) {
        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            method.setAccessible(true);
        }
    }

}
