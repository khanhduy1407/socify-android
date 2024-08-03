package com.socify.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.SplashActivity;
import com.socify.app.ui.fragments.ChatsFragment;
import com.socify.app.ui.fragments.PeopleFragment;
import com.socify.app.models.Chat;
import com.socify.app.models.User;
import com.socify.app.utils.SocifyUtils;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {

  CircleImageView profile_image;
  TextView fullname;

  FirebaseUser firebaseUser;
  DatabaseReference reference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_chat);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");

    profile_image = findViewById(R.id.profile_image);
    fullname = findViewById(R.id.fullname);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(firebaseUser.getUid());

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        fullname.setText(user.getFullname());
        Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });

    final TabLayout tabLayout = findViewById(R.id.tab_layout);
    final ViewPager viewPager = findViewById(R.id.view_pager);

    reference = FirebaseDatabase.getInstance().getReference(Chat.CHATS_DB);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        int unread = 0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Chat chat = snapshot.getValue(Chat.class);
          if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isSeen()) {
            unread++;
          }
        }

        if (unread == 0) {
          viewPagerAdapter.addFragment(new ChatsFragment(), getResources().getString(R.string.chats));
        } else {
          viewPagerAdapter.addFragment(new ChatsFragment(), getResources().getString(R.string.chats) + " (" + unread +")");
        }

        viewPagerAdapter.addFragment(new PeopleFragment(), getResources().getString(R.string.people));

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.chat_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainChatActivity.this, SplashActivity.class)
          .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return true;
    }

    return false;
  }

  @Override
  public void onBackPressed() {
    TabLayout tabLayout = findViewById(R.id.tab_layout);
    int currentTabIndex = tabLayout.getSelectedTabPosition();

    if (currentTabIndex != 0) {
      // Chuyển về tab đầu tiên
      ViewPager viewPager = findViewById(R.id.view_pager);
      viewPager.setCurrentItem(0);
    } else {
      super.onBackPressed();
      finish();
    }
  }

  class ViewPagerAdapter extends FragmentPagerAdapter{

    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;

    ViewPagerAdapter(FragmentManager fm) {
      super(fm);
      this.fragments = new ArrayList<>();
      this.titles = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
      return fragments.get(position);
    }

    @Override
    public int getCount() {
      return fragments.size();
    }

    public void addFragment(Fragment fragment, String title) {
      fragments.add(fragment);
      titles.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
      return titles.get(position);
    }
  }

  private void status(String status) {
    reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB).child(firebaseUser.getUid());

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put(SocifyUtils.EXTRA_STATUS, status);

    reference.updateChildren(hashMap);
  }

  @Override
  protected void onResume() {
    super.onResume();
    status(SocifyUtils.STATUS_ONLINE);
  }

  @Override
  protected void onPause() {
    super.onPause();
    status(SocifyUtils.STATUS_OFFLINE);
  }
}