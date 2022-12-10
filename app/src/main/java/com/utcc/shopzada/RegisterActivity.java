package com.utcc.shopzada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText usernameInp, emailInp, passwordInp, conPasswordInp;
    CheckBox agreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        auth = FirebaseAuth.getInstance();

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
        final DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(username).exists())) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("username", username);
                    userDataMap.put("email", email);
                    userDataMap.put("emailVerified", "0");
                    userDataMap.put("password", password);
                    userDataMap.put("phone", "-");
                    userDataMap.put("phoneVerified", "0");
                    userDataMap.put("verified", "0");
                    userDataMap.put("credits", "0.00");
                    userDataMap.put("level", "1");
                    userDataMap.put("levelpoints", "0");
                    userDataMap.put("luckypoints", "0");

                    reference.child("Users").child(username).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
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
                } else {
                    Toast.makeText(RegisterActivity.this, "Username is already exists!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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