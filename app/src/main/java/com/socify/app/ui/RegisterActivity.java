package com.socify.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.socify.app.R;
import com.socify.app.models.User;
import com.socify.app.utils.SocifyPattern;
import com.socify.app.utils.SocifyUtils;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

  EditText username, fullname, email, password;
  Button register;
  TextView txt_login;

  FirebaseAuth auth;
  DatabaseReference reference;
  ProgressDialog pd;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    username = findViewById(R.id.username);
    fullname = findViewById(R.id.fullname);
    email = findViewById(R.id.email);
    password = findViewById(R.id.password);
    register = findViewById(R.id.register);
    txt_login = findViewById(R.id.txt_login);

    auth = FirebaseAuth.getInstance();

    txt_login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
      }
    });

    register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String str_username = username.getText().toString();
        String str_fullname = fullname.getText().toString();
        String str_email = email.getText().toString();
        String str_password = password.getText().toString();

        if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
            || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
          Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
        } else if (!SocifyPattern.isValid(str_username, SocifyPattern.USERNAME)) {
          Toast.makeText(RegisterActivity.this, "Username invalid", Toast.LENGTH_SHORT).show();
        } else if (!SocifyPattern.isValid(str_email, SocifyPattern.EMAIL)) {
          Toast.makeText(RegisterActivity.this, "Email invalid", Toast.LENGTH_SHORT).show();
        } else if (str_password.length() < 6) {
          Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
        } else {
          pd = new ProgressDialog(RegisterActivity.this);
          pd.setMessage("Please wait...");
          pd.show();

          register(str_username, str_fullname, str_email, str_password);
        }
      }
    });
  }

  private void register(String username, String fullname, String email, String password) {
    auth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (task.isSuccessful()) {
            FirebaseUser firebaseUser = auth.getCurrentUser();
            String userId = firebaseUser.getUid();

            reference = FirebaseDatabase.getInstance().getReference().child(User.USERS_DB).child(userId);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(SocifyUtils.EXTRA_ID, userId);
            hashMap.put(SocifyUtils.EXTRA_USERNAME, username.toLowerCase());
            hashMap.put(SocifyUtils.EXTRA_FULLNAME, fullname);
            hashMap.put(SocifyUtils.EXTRA_BIO, "");
            hashMap.put(SocifyUtils.EXTRA_IMAGE_URL, SocifyUtils.DATA_DEFAULT_IMAGE_URL);
            hashMap.put(SocifyUtils.EXTRA_STATUS, SocifyUtils.STATUS_OFFLINE);

            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  pd.dismiss();
                  Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);
                }
              }
            });
          } else {
            pd.dismiss();
            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
          }
        }
      });
  }
}