package com.ecommerce.catalog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        when(joinPoint.getSignature()).thenReturn(signature);
    }

    @Test
    void testLogBefore() {
        // Test: Log before method execution with method name and arguments
        when(signature.getName()).thenReturn("getAllProducts");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});

        loggingAspect.logBefore(joinPoint);

        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getName();
        verify(joinPoint, times(1)).getArgs();
    }

    @Test
    void testLogBeforeWithNoArguments() {
        // Test: Log before with no method arguments
        when(signature.getName()).thenReturn("getProducts");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        loggingAspect.logBefore(joinPoint);

        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getName();
    }

    @Test
    void testLogAfterReturning() {
        // Test: Log after successful method execution with result
        when(signature.getName()).thenReturn("saveProduct");
        Object result = new Object();

        loggingAspect.logAfterReturning(joinPoint, result);

        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getName();
    }

    @Test
    void testLogAfterReturningWithNullResult() {
        // Test: Log after returning with null result
        when(signature.getName()).thenReturn("deleteProduct");

        loggingAspect.logAfterReturning(joinPoint, null);

        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void testLogAfterThrowing() {
        // Test: Log after exception is thrown
        when(signature.getName()).thenReturn("updateProduct");
        Throwable error = new RuntimeException("Product not found");

        loggingAspect.logAfterThrowing(joinPoint, error);

        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getName();
    }

    @Test
    void testLogAfterThrowingWithDifferentExceptions() {
        // Test: Log different types of exceptions
        when(signature.getName()).thenReturn("createProduct");

        // NullPointerException
        NullPointerException npe = new NullPointerException("Null value");
        loggingAspect.logAfterThrowing(joinPoint, npe);

        // IllegalArgumentException
        IllegalArgumentException iae = new IllegalArgumentException("Invalid argument");
        loggingAspect.logAfterThrowing(joinPoint, iae);

        verify(joinPoint, times(2)).getSignature();
    }
}

