package com.example.instagram.view.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.example.instagram.model.User;
import com.example.instagram.utils.OnLikeClicked;
import com.example.instagram.view.home.post.PostAdaptor;
import com.example.instagram.view.splash.SplashActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PersonFragment extends Fragment implements View.OnClickListener, OnLikeClicked {

    private ImageView imgProfile;
    private TextView tvName;
    private TextView tvEmail;
    private Button btnLogOut , btnSave;
    private RecyclerView recyclerView_personFragment;
    private String userid;
    private ArrayList<Post> posts;
  //  Query query;
    private PostAdaptor postAdapter;


    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference postRef;
    private DatabaseReference userImageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private StorageReference mStorageRef;
    private Bitmap selectedImage;
    private Uri imageUri;

    private static final int GALLERY_PICK = 300;
    private static final int GALLERY_PERMISSION = 400;

    public PersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        setupView(view);
        readPostsFromDB_ByID();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void setupView(View view) {
        imgProfile = view.findViewById(R.id.profile_imageView);
        tvName = view.findViewById(R.id.profile_name_textView);
        tvEmail = view.findViewById(R.id.profile_email_textView);
        btnLogOut = view.findViewById(R.id.profile_sign_out_button);
        btnLogOut.setOnClickListener(this);

        recyclerView_personFragment = view.findViewById(R.id.profile_recyclerView);
        posts = new ArrayList<>();
        postAdapter = new PostAdaptor(posts , PersonFragment.this);
        recyclerView_personFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_personFragment.setAdapter(postAdapter);

        btnSave = view.findViewById(R.id.btnsave);
        btnSave.setOnClickListener(this);

        imgProfile.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("Users").child(user.getUid());

      //  userImageRef = database.getReference("uImage").child(user.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userObject = dataSnapshot.getValue(User.class);
                tvName.setText(userObject.getName());
                tvEmail.setText(userObject.getEmail());
                userid = userObject.getId();
               // imgProfile.setImageResource(userObject.getImage());
//                Uri uri = Uri.parse(userObject.getImage());
//                imgProfile.setImageURI(uri);

                Picasso.get()
                        .load(userObject.getImage())
                        .placeholder(R.drawable.img_placeholder)
                        .into(imgProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.toException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
        @Override
    public void onClick(View v) {

            switch (v.getId()) {
                case R.id.profile_sign_out_button:
                    mAuth.signOut();
                    Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), SplashActivity.class));
                    break;

                case R.id.profile_imageView:
                    checkAccessImagesPermission();
                //    saveUserImage();
                    break;

                case R.id.btnsave:
                    saveUserImage();
                    break;
            }
    }

    private void saveUserImage()
    {
        final String imagePath = UUID.randomUUID().toString() + ".jpg";
        mStorageRef.child("userImages").child(imagePath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageRef.child("userImages").child(imagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = uri.toString();

                        User user=new User();
                        user.setImage(imageURL);
                    //    imgProfile.setImageURI(uri);
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        saveImageToDB(firebaseUser.getUid() , user);
                    }
                });
            }
        });
    }


    private void saveImageToDB(String id2 , User user)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
     //   DatabaseReference myRef = database.getReference("Users").child(id2);
      //  String id = myRef.push().getKey();
      //  myRef.child(id2).setValue(user);
        //  myRef.setValue(user);
      //   Map<String, Object> postValues = post.toMap();
        DatabaseReference ref=database.getReference().child("Users").child(id2);
        Map<String, Object> updates = new HashMap<String,Object>();
        updates.put("image",user.getImage());


        ref.updateChildren(updates);

        Toast.makeText(getActivity(), "image Added", Toast.LENGTH_LONG).show();

    }


    private void checkAccessImagesPermission() {
        int permission = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION);
        } else {
            getImageFromGallery();
        }
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_PICK);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageFromGallery();
            } else {
                Toast.makeText(getActivity(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                assert imageUri != null;
                InputStream imageStream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);

                // base64
                // String imageBase64 = getResizedBase64(selectedImage, 100, 100);

                imgProfile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.you_havent_picked_image, Toast.LENGTH_LONG).show();
        }
    }



    // show posts from DB according to ID of user

    private void readPostsFromDB_ByID()
    {
        // Read from the database
      Query query = database.getReference("post")
                .orderByChild("userId")
                .equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void onLikeClicked(int position) {

    }

    @Override
    public void onCommentAdd(int position, String comment) {

    }
}
