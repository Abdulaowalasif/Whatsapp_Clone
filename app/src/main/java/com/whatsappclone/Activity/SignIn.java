package com.whatsappclone.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.RadioButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whatsappclone.R;
import com.whatsappclone.databinding.ActivitySignInBinding;
import com.whatsappclone.models.Users;

/**
 * @noinspection deprecation
 */
public class SignIn extends AppCompatActivity {
    ActivitySignInBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    DatabaseReference reference;

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 20;
    CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing in");
        progressDialog.setMessage("Signing in to your account.");
        mCallbackManager = CallbackManager.Factory.create();

        SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
        boolean check=  preferences.getBoolean("flag",false);
        Intent intent;
        if (check){
            intent=new Intent(SignIn.this, HomeActivity.class);
            startActivity(intent);
        }

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);

        binding.signInGoogleBtn.setOnClickListener(view -> signIn());

        binding.signInLoginText.setOnClickListener(view -> startActivity(new Intent(SignIn.this, SignUp.class)));

        binding.signInBtn.setOnClickListener(view -> {
            progressDialog.show();
            userLogin();
        });
    }
    private void userLogin() {
        String email = binding.signInUserEmail.getText().toString();
        String pass = binding.signInUserPassword.getText().toString();

        if (email.isEmpty()) {
            binding.signInUserEmail.setError("Invalid email");
            binding.signInUserEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signInUserEmail.setError("Enter a valid email address");
            binding.signInUserEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            binding.signInUserPassword.setError("Enter password");
            binding.signInUserPassword.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            binding.signInUserPassword.setError("Minimum length of password should be 6");
            binding.signInUserPassword.requestFocus();
            return;
        }
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                startActivity(new Intent(SignIn.this, HomeActivity.class));
                Toast.makeText(SignIn.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignIn.this, "Invalid account", Toast.LENGTH_SHORT).show();
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

                        startActivity(new Intent(SignIn.this, HomeActivity.class));
                        Toast.makeText(SignIn.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignIn.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}