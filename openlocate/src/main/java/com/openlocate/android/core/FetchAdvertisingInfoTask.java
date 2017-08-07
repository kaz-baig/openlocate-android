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
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

final class FetchAdvertisingInfoTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private FetchAdvertisingInfoTaskCallback callback;
    private Logger logger;

    private AdvertisingIdClient.Info info;

    FetchAdvertisingInfoTask(Context context, FetchAdvertisingInfoTaskCallback callback) {
        this.context = context;
        this.callback = callback;
        this.logger = getLogger(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (IOException
                | GooglePlayServicesNotAvailableException
                | GooglePlayServicesRepairableException e) {
            logger.e(e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callback != null) {
            callback.onAdvertisingInfoTaskExecute(info);
        }
    }

    private Logger getLogger(Context context) {
        SQLiteOpenHelper helper = new DatabaseHelper(context);
        LoggerDataSource loggerDataSource = new LoggerDatabase(helper);
        return new DatabaseLogger(loggerDataSource, LogLevel.INFO);
    }
}
