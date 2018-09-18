package com.example.mpes.aegeannews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mpes.aegeannews.fragment.HomeFragment;
import com.example.mpes.aegeannews.fragment.LimnosFragment;
import com.example.mpes.aegeannews.fragment.SurosFragment;
import com.example.mpes.aegeannews.fragment.RodosFragment;
import com.example.mpes.aegeannews.fragment.SamosFragment;
import com.example.mpes.aegeannews.fragment.LesvosFragment;

public class MainActivity extends AppCompatActivity{

    //
    private NavigationView navigationView;
    //To parathuro pou energopoihtai apo to menu
    private DrawerLayout drawer;
    private Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    //Metablhth pou krataei thn pathmenh thesh sto menu
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_samos = "samos";
    private static final String TAG_suros = "suros";
    private static final String TAG_rodos = "rodos";
    private static final String TAG_lesvos = "lesvos";
    private static final String TAG_limnos = "limnos";
    public static String CURRENT_TAG = TAG_HOME;
    private String[] activityTitles;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Pinakas poy krataei to onoma olwn twn nisiwn
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // initializing navigation menu
        setUpNavigationView();
        //Panta arxika fortenetsi to arxiko
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }
    //Fortwsh tou menu
    private void loadHomeFragment() {
        selectNavMenu();
        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        //Thread tou menu
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        mHandler.post(mPendingRunnable);
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }
    //Epistrofh tou fragment poy pathsame
    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                SamosFragment samosFragment = new SamosFragment();
                return samosFragment;
            case 2:
                SurosFragment surosFragment = new SurosFragment();
                return surosFragment;
            case 3:
                RodosFragment RodosFragment = new RodosFragment();
                return RodosFragment;
            case 4:
                LesvosFragment lesvosFragment = new LesvosFragment();
                return lesvosFragment;
            case 5:
                LimnosFragment limnosFragment = new LimnosFragment();
                return limnosFragment;
            default:
                return new HomeFragment();
        }
    }
    //Onoma toy toolbar
    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }
    //Pragrammatistika kratame endeiksh pou eimaste
    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }
    //Analogws to poy patame mas paei mpros h pisw
    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.samos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_samos;
                        break;
                    case R.id.suros:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_suros;
                        break;
                    case R.id.rodos:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_rodos;
                        break;
                    case R.id.lesvos:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_lesvos;
                        break;
                    case R.id.limnos:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_limnos;
                        break;

                    case R.id.feedback:
                        startActivity(new Intent(MainActivity.this, Feedback.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }
                //An pathsoume ksana idio  koympi den ksana fortwnei
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                //Fortwsh tou fragment poy pathsame
                loadHomeFragment();

                return true;
            }
        });

        //Anoigma - kleisimo tou toolbar
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        //An to menu einai anoixto kai paththei pisw tha kleisei
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        //An eisai se opoiodhpote fragment ektos tou home, phgianei sto home
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            //An eimaste hdh sto home kai pathsoye duo fores to back, bgainoume apo thm efarmogh
            } else if (navItemIndex == 0) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finish();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                //An te deutero back dem paththei mesa se 2 deuterolepta akurwnetai to prohgoymeno back
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    //Sunarths poy epistrefei poio item exoume pathsei
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
