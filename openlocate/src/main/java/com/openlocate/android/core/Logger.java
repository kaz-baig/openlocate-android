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

import android.util.Log;

abstract class Logger {

    private static final String TAG = Logger.class.getSimpleName();

    protected LogLevel logLevel;

    void e(String message) {
        try {
            log(message, LogLevel.ERROR);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    void w(String message) {
        try {
            log(message, LogLevel.WARN);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    void i(String message) {
        try {
            log(message, LogLevel.INFO);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    void v(String message) {
        try {
            log(message, LogLevel.VERBOSE);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private boolean shouldLog(LogLevel logLevel) {
        return this.logLevel != LogLevel.NONE && logLevel.getValue() <= this.logLevel.getValue();
    }

    private void log(String message, LogLevel logLevel) throws IllegalStateException {
        if (!shouldLog(logLevel)) {
            throw new IllegalStateException(
                    "Illegal log level"
            );
        }

        LogType log = new OpenLocateLog.Builder()
                .setMessage(message)
                .setLogLevel(logLevel)
                .build();
        log(log);
    }

    void log(LogType log) {
        switch (log.getLogLevel()) {
            case ERROR:
                Log.e(TAG, log.getMessage());
                break;
            case WARN:
                Log.w(TAG, log.getMessage());
                break;
            case INFO:
                Log.i(TAG, log.getMessage());
                break;
            case VERBOSE:
                Log.v(TAG, log.getMessage());
                break;
        }
    }
}
