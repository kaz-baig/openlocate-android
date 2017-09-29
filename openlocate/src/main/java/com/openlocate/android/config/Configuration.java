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

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private String url;
    private HashMap<String, String> headers;

    public static class Builder {
        private String url;
        private Map<String, String> headers;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Configuration build() {
            if (headers == null) {
                return new Configuration(url, new HashMap<String, String>());
            } else {
                return new Configuration(url, new HashMap<>(headers));
            }
        }
    }

    private Configuration(String url, HashMap<String, String> headers) {
        this.url = url;
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public boolean isValid() {
        return getUrl() != null && !getUrl().isEmpty();
    }
}
