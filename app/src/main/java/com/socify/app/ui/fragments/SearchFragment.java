package com.socify.app.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.adapters.UserAdapter;
import com.socify.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

  private RecyclerView recyclerView;
  private UserAdapter userAdapter;
  private List<User> mUsers;

  EditText search_bar;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_search, container, false);

    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    search_bar = view.findViewById(R.id.search_bar);

    mUsers = new ArrayList<>();
    userAdapter = new UserAdapter(getContext(), mUsers, true);
    recyclerView.setAdapter(userAdapter);

    readUsers();
    search_bar.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        searchUsers(s.toString().toLowerCase());
      }

      @Override
      public void afterTextChanged(Editable s) {
        //
      }
    });

    return view;
  }

  private void searchUsers(String s) {
    Query query = FirebaseDatabase.getInstance().getReference(User.USERS_DB).orderByChild("username")
      .startAt(s)
      .endAt(s + "\uf8ff");

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mUsers.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          User user = snapshot.getValue(User.class);
          mUsers.add(user);
        }

        userAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        //
      }
    });
  }

  private void readUsers() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (search_bar.getText().toString().equals("")) {
          mUsers.clear();

          // TODO (D-06072024): nên để dữ liệu trống khi ô tìm kiếm là chuỗi rỗng
//          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//            User user = snapshot.getValue(User.class);
//            mUsers.add(user);
//          }

          userAdapter.notifyDataSetChanged();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}