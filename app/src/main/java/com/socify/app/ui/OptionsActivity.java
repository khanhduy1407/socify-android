package com.socify.app.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.socify.app.R;
import com.socify.app.SplashActivity;
import com.socify.app.helpers.LocaleHelper;
import com.socify.app.models.User;
import com.socify.app.utils.SocifyUtils;

import java.util.HashMap;

public class OptionsActivity extends AppCompatActivity {

  private static final int REQUEST_CODE_SETTINGS = 2002;

  TextView settings, logout, delete_user;

  FirebaseUser fUser;
  DatabaseReference reference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_options);

    settings = findViewById(R.id.settings);
    logout = findViewById(R.id.logout);
    delete_user = findViewById(R.id.delete_user);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(getResources().getString(R.string.options));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    fUser = FirebaseAuth.getInstance().getCurrentUser();

    settings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(OptionsActivity.this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
      }
    });

    logout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(OptionsActivity.this, SplashActivity.class)
          .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
      }
    });

    delete_user.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog alertDialog = new AlertDialog.Builder(OptionsActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.do_you_want_to_delete));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.no),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              AlertDialog.Builder passwordDialog = new AlertDialog.Builder(OptionsActivity.this);
              passwordDialog.setTitle(getResources().getString(R.string.confirm_password));
              final EditText input = new EditText(OptionsActivity.this);
              input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
              input.setHint(R.string.confirm_password);
              passwordDialog.setView(input);

              passwordDialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  String password = input.getText().toString().trim();
                  if (!password.isEmpty()) {
                    ProgressDialog pd = new ProgressDialog(OptionsActivity.this);
                    pd.setMessage("Deleting...");
                    pd.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(fUser.getEmail(), password);

                    fUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                          String userId = fUser.getUid();

                          reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(userId);

                          HashMap<String, Object> hashMap = new HashMap<>();
                          hashMap.put(SocifyUtils.EXTRA_DELETED, true);

                          reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()) {
                                // delete user from Firebase Authentication after add deleted field in RealtimeDB
                                fUser.delete()
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                      if (task.isSuccessful()) {
                                        // go to splash screen
                                        startActivity(new Intent(OptionsActivity.this, SplashActivity.class)
                                          .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                      } else {
                                        //
                                      }
                                    }
                                  });
                              }
                            }
                          });
                        } else {
                          Toast.makeText(OptionsActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                        pd.dismiss();
                        dialog.dismiss();
                      }
                    });
                  }
                }
              });

              passwordDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              });

              passwordDialog.show();
              dialog.dismiss();
            }
          });
        alertDialog.show();
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_SETTINGS && resultCode == RESULT_OK) {
      LocaleHelper.applyLocale(OptionsActivity.this);
      recreate();
    }
  }
}