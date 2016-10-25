package com.dmitring.trippledata.utils

import static org.junit.Assert.fail

class ThrowableUtil {
    public static void assertCausesConains(Throwable etalon, Throwable cause) {
        def parentCause = null
        while ((cause != null) && (parentCause != cause)) {
            parentCause = cause
            if (etalon == cause)
                return
            cause = cause.getCause()
        }

       fail("etalon is not contained in checking")
    }
}
