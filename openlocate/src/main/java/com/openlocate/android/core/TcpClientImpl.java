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

import java.util.concurrent.ExecutionException;

final class TcpClientImpl implements TcpClient {

    private String host;
    private int port;

    private TcpClientImpl() {

    }

    TcpClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void write(String message, TcpClientCallback callback) {
        TcpRequest request = new TcpRequest.Builder()
                .setMessage(message)
                .setTcpClientCallback(callback)
                .setHost(host)
                .setPort(port)
                .build();

        execute(request);
    }

    private void execute(TcpRequest request) {
        TcpTask task = new TcpTask();

        try {
            TcpResponse response = task.execute(request).get();
            request.getCallback().onCompletion(
                    request,
                    response
            );
        }  catch (ExecutionException | InterruptedException exception) {
            TcpResponse response = new TcpResponse.Builder()
                    .setError(new Error(exception.getMessage()))
                    .build();

            request.getCallback().onCompletion(
                    request,
                    response
            );
        }
    }
}
