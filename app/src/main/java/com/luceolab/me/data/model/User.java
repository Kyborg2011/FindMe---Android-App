package com.luceolab.me.data.model;

import com.luceolab.me.data.Model;

public class User extends Model {

    private String mPhone;
    private String mFirebaseToken;
    private int mActive;
    private String mFullName;

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getFirebaseToken() {
        return mFirebaseToken;
    }

    public void setFirebaseToken(String mFirebaseToken) {
        this.mFirebaseToken = mFirebaseToken;
    }

    public int getActive() {
        return mActive;
    }

    public void setActive(int mActive) {
        this.mActive = mActive;
    }
}