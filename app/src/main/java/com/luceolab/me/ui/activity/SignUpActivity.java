package com.luceolab.me.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.luceolab.me.R;
import com.luceolab.me.data.service.AccountsService;
import com.luceolab.me.data.service.ServiceGenerator;
import com.luceolab.me.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button signUpBtn = (Button) findViewById(R.id.signup_apply_but);
        if (signUpBtn != null) {
            signUpBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    send();
                }
            });
        }
    }

    public void send() {
        String fullName = ((EditText)findViewById(R.id.full_name)).getText().toString();
        String phone = ((EditText)findViewById(R.id.phone_sign_up)).getText().toString();
        mEmail = ((EditText)findViewById(R.id.email_sign_up)).getText().toString();
        String pass = ((EditText)findViewById(R.id.password_sign_up)).getText().toString();
        String passReType = ((EditText)findViewById(R.id.password_retype_sign_up)).getText().toString();

        // Password retype error
        if (!pass.equals(passReType)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.sign_up_retype_error), Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        phone = phone.replaceAll("\\D", "");

        // All fields must be filled
        if (fullName.isEmpty() || phone.isEmpty() || mEmail.isEmpty() || pass.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.sign_up_blank_error), Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // Creating of retrofit service
        AccountsService loginService =
                ServiceGenerator.createService(AccountsService.class);
        Call<User> call = loginService.signUp(
                mEmail,
                pass,
                phone,
                fullName
        );

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                System.out.println("status: " + response.code());

                User obj = response.body();

                if (obj != null && obj.getError() == null) {
                    showDialog();
                } else {
                    showErrorToast();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showErrorToast();
            }
        });
    }

    public void showErrorToast() {
        Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.simple_error), Toast.LENGTH_LONG);
        toast.show();
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        LayoutInflater inflater = SignUpActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_object_activation, null);

        builder.setTitle(R.string.activate_object)
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
                EditText editText = (EditText) dialogView.findViewById(R.id.object_activation_code);
                String enteredCode = editText.getText().toString();

                // Creating of retrofit service
                AccountsService loginService =
                        ServiceGenerator.createService(AccountsService.class);

                // User activation request
                Call<User> call = loginService.activateUser(
                        mEmail,
                        enteredCode
                );

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        int statusCode = response.code();
                        User result = response.body();

                        if (statusCode == 200 && result.getError() == null) {
                            showErrorToast(getString(R.string.user_activate_successfull));

                            // Open login activity when successful
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);

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

        Button theResendButton = (Button) dialogView.findViewById(R.id.resend);
        theResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating of retrofit service
                AccountsService loginService =
                        ServiceGenerator.createService(AccountsService.class);

                // User activation request
                Call<User> call = loginService.resendUserSms(
                        mEmail
                );

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        showErrorToast(getString(R.string.successfull));
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
