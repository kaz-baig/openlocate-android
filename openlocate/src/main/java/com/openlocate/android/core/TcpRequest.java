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

final class TcpRequest {
    private String message;
    private TcpClientCallback callback;
    private String host;
    private int port;

    private TcpRequest(String message, TcpClientCallback callback, String host, int port) {
        this.message = message;
        this.callback = callback;
        this.host = host;
        this.port = port;
    }

    String getMessage() {
        return message;
    }

    TcpClientCallback getCallback() {
        return callback;
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    static class Builder {
        private String message;
        private TcpClientCallback callback;
        private String host;
        private int port;

        Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        Builder setTcpClientCallback(TcpClientCallback callback) {
            this.callback = callback;
            return this;
        }

        Builder setHost(String host) {
            this.host = host;
            return this;
        }

        Builder setPort(int port) {
            this.port = port;
            return this;
        }

        TcpRequest build() {
            return new TcpRequest(message, callback, host, port);
        }
    }
}
