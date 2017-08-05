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

import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.List;

final public class DispatchLogService extends GcmTaskService {

    @Override
    public int onRunTask(TaskParams taskParams) {
        SQLiteOpenHelper helper = new DatabaseHelper(this);
        LoggerDataSource dataSource = new LoggerDatabase(helper);

        String host = taskParams.getExtras().getString(Constants.HOST_KEY);
        int port = taskParams.getExtras().getInt(Constants.PORT_KEY);
        TcpClient tcpClient = new TcpClientImpl(host, port);

        postLogs(tcpClient, dataSource);

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void postLogs(TcpClient tcpClient, final LoggerDataSource dataSource) {
        final List<LogType> logs = dataSource.popAll();

        if (logs == null || logs.isEmpty()) {
            return;
        }

        for (final LogType log: logs) {
            tcpClient.write(log.getMessage(), new TcpClientCallback() {
                @Override
                public void onCompletion(TcpRequest request, TcpResponse response) {
                    if (response.getError() != null) {
                        dataSource.add(log);
                    }
                }
            });
        }
    }
}
