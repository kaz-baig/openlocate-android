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

final class HttpResponse {
    private final static int STATUS_CODE_HTTP_OK = 200;
    private final static int STATUS_CODE_MULTIPLE_CHOICE = 300;

    private int statusCode;
    private String bodyString;
    private Error error;

    private HttpResponse(int statusCode, String bodyString, Error error) {
        this.statusCode = statusCode;
        this.bodyString = bodyString;
        this.error = error;
    }

    int getStatusCode() {
        return statusCode;
    }

    String getBodyString() {
        return bodyString;
    }

    boolean isSuccess() {
        return statusCode >= STATUS_CODE_HTTP_OK && statusCode < STATUS_CODE_MULTIPLE_CHOICE;
    }

    Error getError() {
        return error;
    }

    static class Builder {
        private int statusCode;
        private String bodyString;
        private Error error;

        Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        Builder setBodyString(String bodyString) {
            this.bodyString = bodyString;
            return this;
        }

        Builder setError(Error error) {
            this.error = error;
            return this;
        }

        HttpResponse build() {
            return new HttpResponse(statusCode, bodyString, error);
        }
    }
}
