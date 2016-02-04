package com.kuziv.android.sunshineapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kuziv.android.sunshineapp.sync.SSyncAdapter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity implements ForecastFragment.ForecastItemCallback {

    private static final String LOG_TAG = "MainActivity";
    // drawer items
    private static final int DRAWER_ITEM_HOME = 1;
    private static final int DRAWER_ITEM_SETTINGS = 2;
    private static final int DRAWER_ITEM_ABOUT = 3;

    private Drawer drawer;

    private boolean mTwoPanelFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, " onCreate()");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main_container, new ForecastFragment())
                    .commit();
        }

        // Setting up toolbar view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Navigation Drawer initialization
        initNavigationDrawer(toolbar);

        SSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(LOG_TAG, " onCreateOptionsMenu()");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(LOG_TAG, " onCreateOptionsMenu()");

        int itemId = item.getItemId();
        switch (itemId) {

            case R.id.settings:

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), "menu Settings", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.about:

                TextView tvAbout = (TextView) getLayoutInflater().inflate(R.layout.about_view, null);
                tvAbout.setClickable(true);
                tvAbout.setMovementMethod(LinkMovementMethod.getInstance());
                String text = getString(R.string.google_example_text);
                tvAbout.setText(Html.fromHtml(text));


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.alert_dialog_title)
                        //.setMessage(R.string.google_example_text)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initNavigationDrawer(Toolbar toolbar) {

        // First initialise AccountHeader and pass it to DrawerBuilder
        AccountHeader accHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_background)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(getString(R.string.profile_drawer_item_name1))
                                .withEmail(getString(R.string.profile_drawer_item_email1))
                                .withIcon(R.drawable.ic_verified_user_black_36dp),

                        new ProfileDrawerItem()
                                .withName(getString(R.string.profile_drawer_item_name2))
                                .withEmail(getString(R.string.profile_drawer_item_email2))
                                .withIcon(R.drawable.ic_verified_user_black_36dp),

                        new ProfileDrawerItem()
                                .withName(getString(R.string.profile_drawer_item_name3))
                                .withEmail(getString(R.string.profile_drawer_item_email3))
                                .withIcon(R.drawable.ic_verified_user_black_36dp))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        //TODO
                        return false;
                    }
                })
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accHeader) //set the AccountHeader created earlier
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_add_account)
                                .withIcon(R.drawable.ic_action_new),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_settings)
                                .withIcon(R.drawable.ic_action_settings),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_about)
                                .withIcon(R.drawable.ic_action_about)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {

                            case DRAWER_ITEM_HOME:
                                Toast.makeText(MainActivity.this, "DRAWER_ITEM_HOME", Toast.LENGTH_SHORT).show();
                                break;

                            case DRAWER_ITEM_SETTINGS:
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                //Toast.makeText(MainActivity.this, "DRAWER_ITEM_SETTINGS", Toast.LENGTH_SHORT).show();
                                break;

                            case DRAWER_ITEM_ABOUT:
                                Toast.makeText(MainActivity.this, "DRAWER_ITEM_ABOUT", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                })
                .build();
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onForecastItemSelected(Uri itemUri) {
        if (mTwoPanelFlag) {
            /*
                Place where we should realise two-pane modification for tablet UI
                by using FragmentManager and FragmentTransaction (add new or replace existing fragment)
            */
            // TODO
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(itemUri);
            startActivity(intent);
        }
    }
}
