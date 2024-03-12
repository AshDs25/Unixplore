package com.example.unixplore;
import com.example.unixplore.R.id;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity{

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Fragment fragment = null;
                if(itemId == id.navHome){
                    fragment = new Home_Fragment();
                }else if(itemId == id.navSearch){
                    fragment = new SearchFragment();
                }else if(itemId == id.navSOP){
                    fragment = new SopFragment();
                }else if(itemId == id.navProfile){
                    fragment = new ProfileFragment();
                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(id.mainFrag, fragment)
                            .commit();
                    return true;
                } else {
                    return false;
                }
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(id.mainFrag, new Home_Fragment())
                .commit();

    }
}