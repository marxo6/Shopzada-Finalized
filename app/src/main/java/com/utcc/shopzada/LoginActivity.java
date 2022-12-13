package com.utcc.shopzada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utcc.shopzada.Models.UserModel;
import com.utcc.shopzada.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInp, passwordInp;
    CheckBox rememberMe;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Users");

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }

        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ConstraintLayout backbutton = (ConstraintLayout) findViewById(R.id.backButton);
        ConstraintLayout loginButton = (ConstraintLayout) findViewById(R.id.loginConfirmButton);
        ConstraintLayout registerPage = (ConstraintLayout) findViewById(R.id.registerPage);

        usernameInp = (EditText) findViewById(R.id.usernameInput);
        passwordInp = (EditText) findViewById(R.id.passwordInput);
        rememberMe = (CheckBox) findViewById(R.id.rememberCheck);

        Paper.init(this);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        registerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                Animatoo.INSTANCE.animateSlideLeft(LoginActivity.this);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowAccess();
            }
        });
    }

    private void allowAccess() {
        String username = usernameInp.getText().toString();
        String password = passwordInp.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            usernameInp.requestFocus();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            passwordInp.requestFocus();
            return;
        } else {
            validateUser(username, password);
        }
    }

    private void validateUser(String username, String password) {

        if (rememberMe.isChecked()) {
            Paper.book().write(Prevalent.UsernameKey, username);
            Paper.book().write(Prevalent.PasswordKey, password);
        }

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(username).exists()) {
                    UserModel userData = snapshot.child(username).getValue(UserModel.class);

                    if (userData.getUsername().equals(username)) {
                        if (userData.getPassword().equals(password)) {
                            Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();

                            Prevalent.currentOnlineUser = userData;

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Animatoo.INSTANCE.animateSlideLeft(LoginActivity.this);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                            Paper.book().destroy();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Username is incorrect!", Toast.LENGTH_SHORT).show();
                        Paper.book().destroy();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Username is not exists!", Toast.LENGTH_SHORT).show();
                    Paper.book().destroy();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateSlideRight(LoginActivity.this);
    }
}