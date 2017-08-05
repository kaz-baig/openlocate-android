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

import android.content.Context;
import android.content.SharedPreferences;

final class SharedPreferencesManager {
    private static final String BASEURL_KEY = "com.openlocate.shared_preferences:BaseUrlKey";
    private static final String TCP_HOST_KEY = "com.openlocate.shared_preferences:TcpHostKey";
    private static final String TCP_PORT_KEY = "com.openlocate.shared_preferences:TcpPortKey";

    private static final String SHARED_PREFERENCE_KEY = OpenLocate.class.getCanonicalName() + ":SharedPreferences";

    private Context context;

    SharedPreferencesManager(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        SharedPreferences preferences = getSharedPreferences();
        return preferences.edit();
    }

    String getBaseUrl() {
        return getSharedPreferences().getString(BASEURL_KEY, null);
    }

    void setBaseUrl(String baseUrl) {
        getEditor().putString(BASEURL_KEY, baseUrl);
    }

    void setTcpHost(String tcpHost) {
        getEditor().putString(TCP_HOST_KEY, tcpHost);
    }

    String getTcpHost() {
        return getSharedPreferences().getString(TCP_HOST_KEY, null);
    }

    void setTcpPort(int port) {
        getEditor().putInt(TCP_PORT_KEY, port);
    }

    int getTcpPort() {
        return getSharedPreferences().getInt(TCP_PORT_KEY, Constants.DEFAULT_PORT);
    }
}
