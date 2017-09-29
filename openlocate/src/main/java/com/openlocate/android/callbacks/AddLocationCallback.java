package com.openlocate.android.callbacks;

/**
 * Created by suhas on 29/09/17.
 */

public interface AddLocationCallback {
    void onSuccess();
    void onError(Error error);
}
