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

enum LogLevel {
    NONE(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    VERBOSE(4);

    private final int value;

    LogLevel(final int newValue) {
        value = newValue;
    }

    int getValue() {
        return value;
    }

    @Override
    public String toString() {
        String value;

        switch (this) {
            case ERROR:
                value = "ERROR";
                break;
            case WARN:
                value = "WARN";
                break;
            case INFO:
                value = "INFO";
                break;
            case VERBOSE:
                value = "VERBOSE";
                break;
            default:
                value = "NONE";
        }

        return value;
    }
}
