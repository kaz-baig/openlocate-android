/*
 * Copyright (c) 2017 OpenLocate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.openlocate.android.core;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseLoggerTests {
    private Logger logger;
    private LoggerDataSource dataSource;

    @Before
    public void setUp() {
        DatabaseHelper helper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
        dataSource = new LoggerDatabase(helper);
        logger = new DatabaseLogger(dataSource, LogLevel.WARN);
        dataSource.popAll();
    }

    @Test
    public void testWarnLog() {
        // Given
        String message = "This is a test warn";

        // When
        logger.w(message);

        // Then
        assertEquals(1, dataSource.size());
    }

    @Test
    public void testErrorLog() {
        // Given
        String message = "This is a test error";

        // When
        logger.e(message);

        // Then
        assertEquals(1, dataSource.size());
    }

    @Test
    public void testInfoLog() {
        // Given
        String message = "This is a test info";

        // When
        logger.i(message);

        // Then
        assertEquals(0, dataSource.size());
    }

    @Test
    public void testVerboseLog() {
        // Given
        String message = "This is a test verbose";

        // When
        logger.v(message);

        // Then
        assertEquals(0, dataSource.size());
    }
}
