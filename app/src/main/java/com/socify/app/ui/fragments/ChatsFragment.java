package com.socify.app.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socify.app.R;
import com.socify.app.ui.adapters.UserChatAdapter;
import com.socify.app.models.ChatList;
import com.socify.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

  private RecyclerView recyclerView;

  private UserChatAdapter userChatAdapter;
  private List<User> mUsers;

  FirebaseUser fUser;
  DatabaseReference reference;

  private List<ChatList> usersList;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chats, container, false);

    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    fUser = FirebaseAuth.getInstance().getCurrentUser();

    usersList = new ArrayList<>();

    reference = FirebaseDatabase.getInstance().getReference(ChatList.CHAT_LIST_DB).child(fUser.getUid());
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        usersList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          ChatList chatList = snapshot.getValue(ChatList.class);
          usersList.add(chatList);
        }

        chatList();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });

    return view;
  }

  private void chatList() {
    mUsers = new ArrayList<>();
    reference = FirebaseDatabase.getInstance().getReference(User.USERS_DB);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mUsers.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          User user = snapshot.getValue(User.class);
          for (ChatList chatList : usersList) {
            if (user.getId().equals(chatList.getId())) {
              mUsers.add(user);
            }
          }
        }
        userChatAdapter = new UserChatAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userChatAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        //
      }
    });
  }
}