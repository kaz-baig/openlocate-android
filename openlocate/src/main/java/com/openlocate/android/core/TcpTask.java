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

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

class TcpTask extends AsyncTask<TcpRequest, Void, TcpResponse> {

    @Override
    protected TcpResponse doInBackground(TcpRequest... params) {
        TcpRequest request = params[0];
        TcpResponse response;

        Socket socket = null;
        PrintWriter writer = null;

        try {
            InetAddress address = InetAddress.getByName(request.getHost());
            socket = new Socket(address, request.getPort());
            writer = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream()
                            )
                    ), true);

            sendMessage(writer, request.getMessage());
            response = new TcpResponse.Builder()
                    .build();
        } catch (IOException e) {
            response = new TcpResponse.Builder()
                    .setError(new Error(e.getMessage()))
                    .build();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }

                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    private void sendMessage(PrintWriter writer, String message) {
        if (!writer.checkError()) {
            writer.println(message);
            writer.flush();
        }
    }
}
