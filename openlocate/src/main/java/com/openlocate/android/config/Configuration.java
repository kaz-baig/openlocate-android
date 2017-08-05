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
package com.openlocate.android.config;

public class Configuration {
    private String baseUrl;
    private String tcpHost;
    private int tcpPort;

    private Configuration(String baseUrl, String tcpHost, int tcpPort) {
        this.baseUrl = baseUrl;
        this.tcpHost = tcpHost;
        this.tcpPort = tcpPort;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTcpHost() {
        return tcpHost;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public boolean isValid() {
        return baseUrl != null && !baseUrl.isEmpty();
    }

    public boolean isTcpConfigured() {
        return tcpHost != null && !tcpHost.isEmpty() && tcpPort != -1;
    }

    public static class Builder {
        private String baseUrl;
        private String tcpHost;
        private int tcpPort;

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setTcpHost(String tcpHost) {
            this.tcpHost = tcpHost;
            return this;
        }

        public Builder setTcpPort(int tcpPort) {
            this.tcpPort = tcpPort;
            return this;
        }

        public Configuration build() {
            return new Configuration(baseUrl, tcpHost, tcpPort);
        }
    }
}
