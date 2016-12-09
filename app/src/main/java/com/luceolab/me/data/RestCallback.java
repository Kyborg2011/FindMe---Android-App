package com.luceolab.me.data;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RestCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response != null) {
            if (response.isSuccessful()) {
                onSuccess(call, response);
            } else {
                onError(call, response);
            }
        } else {
            onFailure(null);
        }
    }

    abstract public void onSuccess(Call<T> call, Response<T> response);

    abstract public void onError(Call<T> call, Response<T> response);

    abstract public void onFailure(Throwable t);

}
