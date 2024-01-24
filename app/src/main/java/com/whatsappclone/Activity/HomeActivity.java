package com.whatsappclone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.whatsappclone.Adapters.FragmentsAdapter;
import com.whatsappclone.R;
import com.whatsappclone.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
ActivityHomeBinding binding;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        setSupportActionBar(binding.toolbar);
        SharedPreferences pref=getSharedPreferences("login",MODE_PRIVATE);
        SharedPreferences.Editor edit= pref.edit();
        edit.putBoolean("flag",true);
        edit.commit();

        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logout){
            auth.signOut();
            SharedPreferences pref=getSharedPreferences("login",MODE_PRIVATE);
            SharedPreferences.Editor edit= pref.edit();
            edit.putBoolean("flag",false);
            edit.commit();
            startActivity(new Intent(HomeActivity.this, SignIn.class));
        }
        return true;
    }

}