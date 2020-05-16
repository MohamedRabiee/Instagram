package com.example.instagram.view.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.model.Comments;
import com.example.instagram.model.Post;
import com.example.instagram.model.User;
import com.example.instagram.utils.OnLikeClicked;
import com.example.instagram.view.home.comment.CommentAdaptor;
import com.example.instagram.view.home.post.PostAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements OnLikeClicked {


    private RecyclerView recyclerView;
    private PostAdaptor postAdapter;

    private RecyclerView commentRecyclerView;
    private CommentAdaptor commentAdaptor;


    private DatabaseReference myRef;
    private DatabaseReference commentRef;
    private FirebaseDatabase database;
    private ArrayList<Post> posts;
    private ArrayList<Comments> commentArr;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setupView(view);
        readPostsFromDB();
   //     readCommentsFromDB();

  //      readCommentsFromDB_ByID();
        return view;

    }
    private void setupView(View view)
    {
        recyclerView = view.findViewById(R.id.recyclerView);
        commentRecyclerView = view.findViewById(R.id.post_recyclerView);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("post");
        commentRef = database.getReference("comments");
        posts = new ArrayList<>();
        commentArr = new ArrayList<>();

        postAdapter = new PostAdaptor(posts , HomeFragment.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdapter);


//        commentAdaptor = new CommentAdaptor(commentArr);
//        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        commentRecyclerView.setAdapter(commentAdaptor);







        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    private void readPostsFromDB()
    {
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    final Post post = snapshot.getValue(Post.class);
                    post.setId(id);
                    DatabaseReference userRef = database.getReference("Users").child(post.getUserId());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // cast datasnapshot to User
                            User user = dataSnapshot.getValue(User.class);
                            post.setUser(user);

                            // apend post to list (posts)
                            posts.add(post);
                            postAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getActivity(), " "+error.toException(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onLikeClicked(final int position) {
        final Post post = posts.get(position);

        final DatabaseReference likeRef = database.getReference("UserLikes").child(firebaseUser.getUid()).child(post.getId()).child("didLike");

        final DatabaseReference myRef = database.getReference("Posts").child(post.getId()).child("numberOfLikes");
        final int numberOfLiked = post.getNumberOfLikes();

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean didLike = dataSnapshot.getValue(Boolean.class);
                Post p = new Post();
                if (didLike != null && didLike) {
                    myRef.setValue(numberOfLiked - 1);
                    posts.get(position).setNumberOfLikes(numberOfLiked - 1);
                    likeRef.setValue(false);
                    p.setNumberOfLikes(numberOfLiked-1);
                } else {
                    myRef.setValue(numberOfLiked + 1);
                    posts.get(position).setNumberOfLikes(numberOfLiked + 1);

                    likeRef.setValue(true);
                    p.setNumberOfLikes(numberOfLiked+1);
                }
                changeStateOfLikes(post.getId() , p);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "on like error ", Toast.LENGTH_LONG).show();

            }
        });
    }



    public void changeStateOfLikes(String id , Post post)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref=database.getReference().child("post").child(id);
        Map<String, Object> updates = new HashMap<String,Object>();

        updates.put("numberOfLikes",post.getNumberOfLikes());
        ref.updateChildren(updates);

        Toast.makeText(getActivity(), "like Updated", Toast.LENGTH_LONG).show();

    }

    //ToDo comments
    @Override
    public void onCommentAdd(int position , String comment) {
        final Post post = posts.get(position);
        String postID = post.getId();

        Post p = new Post();
        FirebaseUser currentUser = mAuth.getCurrentUser();
      //  p.setCommentId(currentUser.getUid());


        Comments comments = new Comments();
        comments.setCommentData(comment);
        comments.setPostId(postID);
     //   comments.setUserName(currentUser.getDisplayName());

        saveCommentToDB(postID , comments);
    }

    private void saveCommentToDB(String id , Comments comment)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("comments");
        String id2 = myRef.push().getKey();

        myRef.child(id).child(id2).setValue(comment);
        //  myRef.setValue(post);

        Toast.makeText(getActivity(), "comment Added", Toast.LENGTH_LONG).show();

    }


    private void readCommentsFromDB()
    { // Read from the database
        commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    final Post post = snapshot.getValue(Post.class);
                    post.setId(id);
                    DatabaseReference userRef = database.getReference("Users").child(post.getUserId());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // cast datasnapshot to User
                            User user = dataSnapshot.getValue(User.class);
                            post.setUser(user);

                            // apend post to list (posts)
                            posts.add(post);
                            postAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                        commentAdaptor = new CommentAdaptor(commentArr);
                        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        commentRecyclerView.setAdapter(commentAdaptor);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getActivity(), " "+error.toException(), Toast.LENGTH_LONG).show();
            }
        });
    }




}
