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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpTask extends AsyncTask<HttpRequest, Void, HttpResponse> {

    private final static int CONNECTION_READ_TIMEOUT = 15000;
    private final static int CONNECTION_CONNECT_TIMEOUT = 15000;
    private static String TAG = HttpTask.class.getSimpleName();
    private static String CONNECTION_CHARSET = "UTF-8";

    @Override
    protected HttpResponse doInBackground(HttpRequest... params) {
        HttpRequest request = params[0];
        HttpResponse response;

        try {
            URL url = new URL(request.getUrl());
            HttpURLConnection connection = getConnection(url, request.getMethodType());

            if (request.isValidForPost()) {
                setPostRequestArguments(connection);
                writeToOutputStream(connection.getOutputStream(), request.getParams());
            }

            connection.connect();
            String responseBody = getResponseBody(connection.getInputStream());

            response = new HttpResponse.Builder()
                    .setBodyString(responseBody)
                    .setStatusCode(connection.getResponseCode())
                    .build();
        } catch (IOException e) {
            response = new HttpResponse.Builder()
                    .setError(new Error(e.getMessage()))
                    .build();
        }

        return response;
    }

    private void setPostRequestArguments(HttpURLConnection connection) {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json; charset=" + CONNECTION_CHARSET);
    }

    private void writeToOutputStream(OutputStream outputStream, String params) throws IOException {
        outputStream.write(params.getBytes(CONNECTION_CHARSET));
        outputStream.flush();
    }

    private String getResponseBody(InputStream inputStream) throws IOException {
        String responseBody = null;
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;

        try {
            streamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            responseBody = stringBuilder.toString();
        } finally {
            try {
                if (streamReader != null) {
                    streamReader.close();
                }

                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return responseBody;
    }

    private HttpURLConnection getConnection(URL url, RestClientMethodType methodType) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(methodType.toString());
        connection.setReadTimeout(CONNECTION_READ_TIMEOUT);
        connection.setConnectTimeout(CONNECTION_CONNECT_TIMEOUT);
        connection.setDoInput(true);

        return connection;
    }
}
