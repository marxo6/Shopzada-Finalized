package com.utcc.shopzada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utcc.shopzada.Models.UserModel;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameInp, emailInp, passwordInp, conPasswordInp;
    CheckBox agreement;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        ConstraintLayout loginPage = (ConstraintLayout) findViewById(R.id.loginPage);
        ConstraintLayout registerConfirm = (ConstraintLayout) findViewById(R.id.registerConfirmButton);

        usernameInp = (EditText) findViewById(R.id.usernameInput);
        emailInp = (EditText) findViewById(R.id.emailInput);
        passwordInp = (EditText) findViewById(R.id.passwordInput);
        conPasswordInp = (EditText) findViewById(R.id.conPasswordInput);
        agreement = (CheckBox) findViewById(R.id.agreement);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                Animatoo.INSTANCE.animateSlideLeft(RegisterActivity.this);
                finish();
            }
        });

        registerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = usernameInp.getText().toString();
        String email = emailInp.getText().toString();
        String password = passwordInp.getText().toString();
        String conpassword = conPasswordInp.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            usernameInp.requestFocus();
            return;
        } else if (email.isEmpty()) {
            Toast.makeText(this, "E-mail cannot be empty!", Toast.LENGTH_SHORT).show();
            emailInp.requestFocus();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            passwordInp.requestFocus();
            return;
        } else if (conpassword.isEmpty()) {
            Toast.makeText(this, "Confirm password cannot be empty!", Toast.LENGTH_SHORT).show();
            conPasswordInp.requestFocus();
            return;
        } else if (!password.equalsIgnoreCase(conpassword)) {
            Toast.makeText(this, "Confirm password does not match!", Toast.LENGTH_SHORT).show();
            conPasswordInp.requestFocus();
            return;
        } else if (!agreement.isChecked()) {
            Toast.makeText(this, "Agreement is not checked!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            validateUser(username, email, password);
        }
    }

    private void validateUser(String username, String email, String password) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = new UserModel(username, email, password, "", "", "customer", "", 0, 1, 0, 0, false, false, false);
                Map<String, Object> userValues = userModel.toMap();

                reference.child(username).updateChildren(userValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            Animatoo.INSTANCE.animateSlideLeft(RegisterActivity.this);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        Animatoo.INSTANCE.animateSlideRight(RegisterActivity.this);
    }
}