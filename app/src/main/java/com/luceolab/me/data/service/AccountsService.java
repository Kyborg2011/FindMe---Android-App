package com.luceolab.me.data.service;

import com.luceolab.me.data.model.AccessToken;
import com.luceolab.me.data.model.User;

import retrofit2.Call;
import retrofit2.http.*;

public interface AccountsService {

    String ACCESS_GRANT_TYPE = "password";
    String REFRESH_GRANT_TYPE = "refresh_token";

    // oAuth2 authentication
    @POST("/oauth/token")
    @FormUrlEncoded
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grantType);

    @POST("/oauth/token")
    @FormUrlEncoded
    Call<AccessToken> refreshAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType);

    @POST("/api/users")
    @FormUrlEncoded
    Call<User> signUp(
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("full_name") String full_name);

    // New user activation
    @POST("/api/users/activate")
    @FormUrlEncoded
    Call<User> activateUser(
            @Field("email") String email,
            @Field("code") String code);

    // New user activation
    @POST("/api/users/resend")
    @FormUrlEncoded
    Call<User> resendUserSms(
            @Field("email") String email);

    // Password recovery
    @POST("/api/users/recovery")
    @FormUrlEncoded
    Call<User> recoveryUser(
            @Field("email") String email);
}
