package com.dmitring.trippledata.utils

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

class AssertFutureUtil {
    public static <T, F extends Future<T>> void getAndAssert(F future, T etalonResult, long timeout) {
        final result = get(future, timeout)
        assertEquals(etalonResult, result)
    }

    public static <T, F extends Future<T>> T get(F future, long timeout) {
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS)
        } catch (TimeoutException exception) {
            fail("Operation must be completed in ${timeout} milliseconds")
        }
    }
}
