package com.luceolab.me.data;

public abstract class Model {

    private int mId = 0;
    private String mError = null;

    public String getError() {
        return mError;
    }

    public void setError(String error) {
        this.mError = error;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

}
