package com.socify.app.ui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.socify.app.R;
import com.socify.app.models.Story;
import com.socify.app.utils.SocifyUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddStoryActivity extends AppCompatActivity {

  private Uri mImageUri;
  String myUrl = "";
  private StorageTask storageTask;
  StorageReference storageReference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_story);

    storageReference = FirebaseStorage.getInstance().getReference(Story.STORIES_STORAGE);

    CropImage.activity()
      .setAspectRatio(9, 16)
      .start(AddStoryActivity.this);
  }

  private String getFileExtension(Uri uri) {
    ContentResolver contentResolver = getContentResolver();
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
  }

  private void publishStory() {
    final ProgressDialog pd = new ProgressDialog(this);
    pd.setMessage("Posting...");
    pd.show();

    if (mImageUri != null) {
      StorageReference imageReference = storageReference.child(System.currentTimeMillis()
        + "." + getFileExtension(mImageUri));

      storageTask = imageReference.putFile(mImageUri);
      storageTask.continueWithTask(new Continuation() {
        @Override
        public Object then(@NonNull Task task) throws Exception {
          if (!task.isSuccessful()) {
            throw task.getException();
          }
          return imageReference.getDownloadUrl();
        }
      }).addOnCompleteListener(new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
          if (task.isSuccessful()) {
            Uri downloadUri = task.getResult();
            myUrl = downloadUri.toString();

            String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Story.STORIES_DB).child(myId);

            String storyId = reference.push().getKey();
            long time_end = System.currentTimeMillis() + 86400000; // 1 day

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(SocifyUtils.EXTRA_IMAGE_URL, myUrl);
            hashMap.put(SocifyUtils.EXTRA_TIME_START, ServerValue.TIMESTAMP);
            hashMap.put(SocifyUtils.EXTRA_TIME_END, time_end);
            hashMap.put(SocifyUtils.EXTRA_STORY_ID, storyId);
            hashMap.put(SocifyUtils.EXTRA_USER_ID, myId);

            reference.child(storyId).setValue(hashMap);
            pd.dismiss();

            finish();
          } else {
            Toast.makeText(AddStoryActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      });
    } else {
      Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      mImageUri = result.getUri();

      publishStory();
    } else {
      Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
      finish();
    }
  }
}