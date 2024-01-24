package com.whatsappclone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whatsappclone.R;
import com.whatsappclone.databinding.ActivityMainBinding;
import com.whatsappclone.models.Users;

public class SignUp extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseDatabase database;
    EditText name, pass, email;
    ProgressDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        name = binding.signUpUsername;
        pass = binding.signUpUserPassword;
        email = binding.signUpUserEmail;

        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMessage("Creating your account.");
        progressDialog.setTitle("Creating account.");
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);

        binding.signUpGoogleBtn.setOnClickListener(view -> signIn());


        binding.signUpLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                userRegister();
            }
        });
    }

    private void userRegister() {
        String userEmail = email.getText().toString();
        String userPass = pass.getText().toString();
        String userName = name.getText().toString();

        if (userEmail.isEmpty()) {
            email.setError("Enter an email address");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
            return;
        }
        if (userPass.isEmpty()) {
            pass.setError("Enter password");
            pass.requestFocus();
            return;
        }
        if (userPass.length() < 6) {
            pass.setError("Minimum length of password should be 6");
            pass.requestFocus();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(SignUp.this, HomeActivity.class));
                    Users user = new Users(userName, userEmail, userPass);
                    String id = task.getResult().getUser().getUid();
                    reference.child(id).setValue(user);
                    Toast.makeText(SignUp.this, "Register successful.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUp.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (Exception e) {
                Log.d("TAG", "Google login failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Users users = new Users();
                        users.setUserId(user.getUid());
                        users.setUserName(user.getDisplayName());
                        users.setEmail(user.getEmail());
                        users.setProfileImage(user.getPhotoUrl().toString());

                        reference.child(user.getUid()).setValue(users);

                        startActivity(new Intent(SignUp.this, HomeActivity.class));
                        Toast.makeText(SignUp.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUp.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}