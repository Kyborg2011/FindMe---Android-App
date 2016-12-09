package com.luceolab.me.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.luceolab.me.Constants;
import com.luceolab.me.R;
import com.luceolab.me.Utils;
import com.luceolab.me.data.service.AccountsService;
import com.luceolab.me.data.service.ServiceGenerator;
import com.luceolab.me.data.model.AccessToken;
import com.luceolab.me.data.model.User;
import com.luceolab.me.security.helper.KeyStoreHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // Used for store access and refresh tokens
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            //This will only create a certificate once as it checks
            //internally whether a certificate with the given name
            //already exists.
            KeyStoreHelper.createKeys(this, Constants.KEYSTORE_ALIAS);
        } catch (Exception e) {
            //Probably will never happen.
            throw new RuntimeException(e);
        }

        /* Testing of KeyStore */
        String encrypted = KeyStoreHelper.encrypt(Constants.KEYSTORE_ALIAS, "Hello World");
        String decrypted = KeyStoreHelper.decrypt(Constants.KEYSTORE_ALIAS, encrypted);

        System.out.println("encrypted text: " + encrypted);
        System.out.println("decrypted text: " + decrypted);

        /* Testing of NDK (API KEY and API SECRET */
        System.out.println("api key: " + Utils.getApiKey());
        System.out.println("api key: " + Utils.getApiSecret());

        final Context ctx = getApplicationContext();

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.edit_text_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if (mEmailSignInButton != null) {
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }

        Button mOpenSignupButton = (Button) findViewById(R.id.open_signup);
        if (mOpenSignupButton != null) {
            mOpenSignupButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent intent = new Intent(ctx, SignUpActivity.class);
                startActivity(intent);
                }
            });
        }

        Button mOpenRecoveryDialog = (Button) findViewById(R.id.password_recovery);
        if (mOpenRecoveryDialog != null) {
            mOpenRecoveryDialog.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPasswordRenewalDialog();
                }
            });
        }

        mSettings = getSharedPreferences(Constants.PREFS_NAME, 0);
        String accessToken = mSettings.getString("access_token", null);
        if (accessToken != null) {
            //Intent intent = new Intent(this, DashboardActivity.class);
            //startActivity(intent);
        }
    }

    private void attemptLogin() {

        final Context ctx = this;

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            // Creating of retrofit service
            AccountsService loginService =
                    ServiceGenerator.createService(AccountsService.class, Constants.REST_API_CLIENT_ID, Constants.REST_API_CLIENT_SECRET);
            Call<AccessToken> call = loginService.getAccessToken(
                    Constants.REST_API_CLIENT_ID,
                    Constants.REST_API_CLIENT_SECRET,
                    email,
                    password,
                    AccountsService.ACCESS_GRANT_TYPE
            );

            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    int statusCode = response.code();
                    Log.d("RESTAPI", "status code: " + statusCode);
                    AccessToken token = response.body();

                    if (token != null && statusCode == 200) {
                        String accessToken = token.getAccessToken();
                        Log.d("RESTAPI", "access token: " + accessToken);

                        // Write access token to shared preferences
                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("access_token", accessToken);
                        editor.apply();

                        //Intent intent = new Intent(ctx, DashboardActivity.class);
                        //startActivity(intent);

                    } else {
                        showErrorToast();
                    }

                    showProgress(false);
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    System.out.println("FAILURE " + t.getMessage());
                    showProgress(false);
                    showErrorToast();
                }
            });
        }
    }

    public void showErrorToast() {
        Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.auth_error), Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // Password recovery dialog
    public void showPasswordRenewalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = LoginActivity.this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.password_recovery_dialog, null);

        builder.setTitle(R.string.password_recovery_title)
                .setView(dialogView)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button theButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) dialogView.findViewById(R.id.password_recovery_email);
                String email = editText.getText().toString();

                // Creating of retrofit service
                AccountsService loginService =
                        ServiceGenerator.createService(AccountsService.class);

                // Password recovery request
                Call<User> call = loginService.recoveryUser(
                        email
                );



                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        int statusCode = response.code();
                        User result = response.body();

                        if (statusCode == 200 && result.getError() == null) {
                            showErrorToast(getString(R.string.user_recovery_successfull));
                            dialog.dismiss();
                        } else {
                            if (result != null) {
                                showErrorToast(result.getError());
                            } else {
                                showErrorToast();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        showErrorToast();
                    }
                });
            }
        });
    }

    public void showErrorToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(),
                text, Toast.LENGTH_LONG);
        toast.show();
    }
}


