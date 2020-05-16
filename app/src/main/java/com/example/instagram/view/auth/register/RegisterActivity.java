package com.example.instagram.view.auth.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.model.User;
import com.example.instagram.view.auth.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnRegister;
    private ImageView imgUser;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupView();
    }

    private void setupView()
    {
        edtUsername = findViewById(R.id.username_editText);
        edtEmail = findViewById(R.id.email_editText);
        edtPassword = findViewById(R.id.password_editText);
        btnRegister = findViewById(R.id.sign_up_button);
      //  btnsignin = findViewById(R.id.sign_in_button);
        imgUser = findViewById(R.id.register_imageView);

        mAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {

        String username = edtUsername.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        final String imagePath = UUID.randomUUID().toString() + ".jpg";


        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setImage("");
        createUser(user);
    }

    private void createUser(final User user)
    {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            saveUserToDB(firebaseUser.getUid() , user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToDB(String id , User user)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(id);
        user.setPassword(null);
        myRef.setValue(user);
        Toast.makeText(RegisterActivity.this, "Success",
                Toast.LENGTH_LONG).show();
        finish();
    }


    public void goToSignIn(View view) {
        startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
    }
}
