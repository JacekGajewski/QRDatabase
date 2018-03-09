package com.tnt9.qrdatabase;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class NavigationDrawerActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private int currentPosition = 0;
    private String[] menuTitles;
    private String scannedQR;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = (NavigationView) findViewById(R.id.drawer);
        menuTitles = getResources().getStringArray(R.array.menu_titles);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //Odtworzenie elementów na pasku akcji => onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerToggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.white));
        drawerLayout.addDrawerListener(drawerToggle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Change toolbar title when user goes back
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getFragmentManager().findFragmentByTag("visible_fragment");
                if (fragment instanceof ScanFragment){
                    currentPosition = 0;
                }else if (fragment instanceof SearchFragment){
                    currentPosition = 1;
                }else if (fragment instanceof RecentFragment){
                    currentPosition = 2;
                }else if (fragment instanceof WatchlistFragment){
                    currentPosition = 3;
                } else{
                    currentPosition = 0;
                }
                setToolbarTitle(currentPosition);

                //Highlight correct item at drawer
                //drawerList.setItemChecked(currentPosition, true);
            }
        });

        if (savedInstanceState != null){
            currentPosition = savedInstanceState.getInt("position");
            setToolbarTitle(currentPosition);
        } else{
            selectItem(0);
        }

        drawer.setNavigationItemSelectedListener(this);
    }

    private void setToolbarTitle(int position) {
        String title = menuTitles[position];
        getSupportActionBar().setTitle(title);
    }

    private void selectItem(int position) {
        switch (position){
            case 0:
                commitFragment(new ScanFragment());
                break;
            case 1:
                commitFragment(new SearchFragment());
                break;
            case 2:
                commitFragment(new ScanResultFragment());
                break;
            default:
                commitFragment(new ScanFragment());
        }
        setToolbarTitle(position);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void commitFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        if (fragment instanceof ScanResultFragment){
            Bundle bundle = new Bundle();
            bundle.putCharSequence(ScanResultFragment.EXTRA_QR, scannedQR);
            fragment.setArguments(bundle);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }


    //Sync state of toggle with drawer
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_scan) {
            commitFragment(new ScanFragment());
        } else if (id == R.id.nav_search) {
            commitFragment(new SearchFragment());
        } else if (id == R.id.nav_recent) {
            commitFragment(new RecentFragment());
        } else if (id == R.id.nav_watchlist) {
            commitFragment(new WatchlistFragment());
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            scannedQR = result.getContents();
            if (scannedQR != null) {
                commitFragment(new ScanResultFragment());
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_json:
                prepareJSONToExcel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void prepareJSONToExcel() {
    }

}
