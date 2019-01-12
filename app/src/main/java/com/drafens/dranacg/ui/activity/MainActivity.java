package com.drafens.dranacg.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import com.drafens.dranacg.ui.fragment.FragmentAbout;
import com.drafens.dranacg.ui.fragment.FragmentBookShelf;
import com.drafens.dranacg.ui.fragment.FragmentBookSource;
import com.drafens.dranacg.ui.fragment.FragmentDownload;
import com.drafens.dranacg.R;
import com.drafens.dranacg.ui.fragment.FragmentLogin;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener, FragmentBookSource.CallBackValue {
    private Toolbar toolbar;
    private Fragment fragmentBookShelf, fragmentBookSource, fragmentDownLoad, fragmentLogin, fragmentAbout;
    private Fragment currentFragment;
    private String siteItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentBookShelf = new FragmentBookShelf();
        fragmentBookSource = new FragmentBookSource();
        fragmentDownLoad = new FragmentDownload();
        fragmentLogin = new FragmentLogin();
        fragmentAbout = new FragmentAbout();
        initView();
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.comic);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        currentFragment = fragmentBookShelf;
        fragmentTransaction.add(R.id.fragment_main,currentFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_book_source) {
            showFragment(fragmentBookSource);
        } else if (id == R.id.nav_book_shelf) {
            showFragment(fragmentBookShelf);
        }else if (id == R.id.nav_download) {
            showFragment(fragmentDownLoad);
        } else if (id == R.id.nav_login) {
            showFragment(fragmentLogin);
        } else if (id == R.id.nav_about) {
            showFragment(fragmentAbout);
        }
        toolbar.setTitle(item.getTitleCondensed());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showFragment(Fragment fragment){
        if (currentFragment != fragment){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(currentFragment);
            if (!fragment.isAdded()) {
                fragmentTransaction.add(R.id.fragment_main, fragment).show(fragment);
            } else {
                fragmentTransaction.show(fragment);
            }
            fragmentTransaction.commit();
            currentFragment = fragment;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                Intent intent = new Intent(this,SearchActivity.class);
                intent.putExtra("site_item", siteItem);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendMessage(String str) {
        siteItem = str;
    }
}
