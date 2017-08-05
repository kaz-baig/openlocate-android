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

final class HttpRequest {
    private RestClientMethodType methodType;
    private String url;
    private String params;
    private HttpClientCallback successCallback;
    private HttpClientCallback failureCallback;

    private HttpRequest(RestClientMethodType methodType, String url, String params, HttpClientCallback successCallback, HttpClientCallback failureCallback) {
        this.methodType = methodType;
        this.url = url;
        this.params = params;
        this.successCallback = successCallback;
        this.failureCallback = failureCallback;
    }

    RestClientMethodType getMethodType() {
        return methodType;
    }

    String getUrl() {
        return url;
    }

    String getParams() {
        return params;
    }

    HttpClientCallback getSuccessCallback() {
        return successCallback;
    }

    HttpClientCallback getFailureCallback() {
        return failureCallback;
    }

    boolean isValidForPost() {
        return getMethodType() == RestClientMethodType.POST && getParams() != null;
    }

    static class Builder {
        private RestClientMethodType methodType;
        private String url;
        private String params;
        private HttpClientCallback successCallback;
        private HttpClientCallback failureCallback;

        Builder setMethodType(RestClientMethodType methodType) {
            this.methodType = methodType;
            return this;
        }

        Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        Builder setParams(String params) {
            this.params = params;
            return this;
        }

        Builder setSuccessCallback(HttpClientCallback successCallback) {
            this.successCallback = successCallback;
            return this;
        }

        Builder setFailureCallback(HttpClientCallback failureCallback) {
            this.failureCallback = failureCallback;
            return this;
        }

        HttpRequest build() {
            return new HttpRequest(methodType, url, params, successCallback, failureCallback);
        }
    }

}
