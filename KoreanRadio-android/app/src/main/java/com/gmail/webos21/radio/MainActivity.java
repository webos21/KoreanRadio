package com.gmail.webos21.radio;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.webos21.android.patch.PRNGFixes;
import com.gmail.webos21.android.widget.ChooseFileDialog;
import com.gmail.webos21.radio.db.ChDbInterface;
import com.gmail.webos21.radio.db.ChDbManager;
import com.gmail.webos21.radio.db.ChExporter;
import com.gmail.webos21.radio.db.ChImporter;
import com.gmail.webos21.radio.db.ChRow;
import com.gmail.webos21.radio.db.ChRowAdapter;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private NavigationView navigationView;

    private CheckBox cbIcon;
    private TextView tvTotalSite;

    private RecyclerView chlist;
    private ChRowAdapter chAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Consts.DEBUG) {
            Log.i(TAG, "onCreate!!!!!!!!!!!!!");
        }

        // Android SecureRandom Fix!!! (No Dependency)
        PRNGFixes.apply();

        // Set Tool-Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set Drawer-Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            //noinspection deprecation
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        // Set Navigation-View
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavItemSelected());
        }

        // Set FloatingActionButton
        FloatingActionButton fabInputOne = (FloatingActionButton) findViewById(R.id.fab_input_one);
        fabInputOne.setOnClickListener(this);
        FloatingActionButton fabImportCsv = (FloatingActionButton) findViewById(R.id.fab_import_csv);
        fabImportCsv.setOnClickListener(this);
        FloatingActionButton fabExportCsv = (FloatingActionButton) findViewById(R.id.fab_export_csv);
        fabExportCsv.setOnClickListener(this);

        // Get Shared Preferences
        SharedPreferences pref = getSharedPreferences(Consts.PREF_FILE, MODE_PRIVATE);

        // Set Views
        boolean bIconShow = pref.getBoolean(Consts.PREF_SHOW_ICON, false);
        cbIcon = (CheckBox) findViewById(R.id.chk_icon_show);
        cbIcon.setChecked(bIconShow);
        cbIcon.setOnCheckedChangeListener(new ShowConfigListener());

        tvTotalSite = (TextView) findViewById(R.id.tv_total_site);

        // Set Main-ListView
        chlist = (RecyclerView) findViewById(R.id.recyclerview);
        chAdapter = new ChRowAdapter(this, null, pref.getBoolean(Consts.PREF_SHOW_ICON, false));
        chlist.setAdapter(chAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chlist.setLayoutManager(layoutManager);

//        chlist.setAdapter(chAdapter);
//        chlist.setOnItemClickListener(new ChRowClickedListener());
//        chlist.setOnItemLongClickListener(new ChRowLongClickedListener());

        // Set Views
        tvTotalSite.setText(getResources().getString(R.string.cfg_total_item) + chAdapter.getItemCount());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Consts.DEBUG) {
            Log.i(TAG, "onCreateOptionsMenu!!!!!!!!!!!!!");
        }

        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchAdapter(chAdapter));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Consts.DEBUG) {
            Log.i(TAG, "onStart!!!!!!!!!!!!!");
        }

        // Request Permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Consts.PERM_REQ_EXTERNAL_STORAGE);
            return;
        }

        getAllChannelListFromProvider();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Consts.DEBUG) {
            Log.i(TAG, "onStop!!!!!!!!!!!!!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Consts.DEBUG) {
            Log.i(TAG, "onDestroy!!!!!!!!!!!!!");
        }
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.fab_input_one: {
                Intent i = new Intent(this, ChAddActivity.class);
                startActivityForResult(i, Consts.ACTION_ADD);
                break;
            }
            case R.id.fab_import_csv:
                showFileDialog();
                break;
            case R.id.fab_export_csv:
                exportCsv();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (Consts.DEBUG) {
            Log.i(TAG, "onRequestPermissionsResult!!!!!!!!!!!!!");
        }
        if (requestCode == Consts.PERM_REQ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // OK, nothing to do
            } else {
                Toast.makeText(this, getResources().getString(R.string.err_perm_exflah), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Consts.DEBUG) {
            Log.i(TAG, "onActivityResult!!!!!!!!!!!!!");
        }

        if (requestCode == Consts.ACTION_ADD) {
            if (resultCode == RESULT_OK) {
                getAllChannelListFromProvider();
            }
        }
        if (requestCode == Consts.ACTION_MODIFY) {
            if (resultCode == RESULT_OK) {
                getAllChannelListFromProvider();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getAllChannelListFromProvider() {
        getSupportLoaderManager().initLoader(Consts.MAIN_LOADER_ID, null, new ChannelLoaderCallback());
    }

    private void getChannelListFromProvider(String searchTerm) {
        getSupportLoaderManager().initLoader(Consts.MAIN_LOADER_ID, null, new ChannelLoaderCallback(searchTerm));
    }

    private void showFileDialog() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Consts.PERM_REQ_EXTERNAL_STORAGE);
            return;
        }

        String mountPoint = Environment.getExternalStorageDirectory().toString();
        ChooseFileDialog cfd = new ChooseFileDialog(this, mountPoint, "csv");
        cfd.setOnFileChosenListener(new CsvFileSelectedListener());
        cfd.show();
    }

    private void exportCsv() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Consts.PERM_REQ_EXTERNAL_STORAGE);
            return;
        }

        String mountPoint = Environment.getExternalStorageDirectory().toString();
        File csvFile = new File(mountPoint + "/Download", "radio_channel.csv");
        ChDbInterface pdi = ChDbManager.getInstance().getPbDbInterface();

        new ChExporter(pdi, csvFile, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "File is exported!!", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private class NavItemSelected implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.nav_settings: {
                    break;
                }
                default:
                    break;
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            navigationView.getMenu().getItem(0).setChecked(true);

            return true;
        }
    }

    private class ShowConfigListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int vId = buttonView.getId();
            switch (vId) {
                case R.id.chk_icon_show: {
                    SharedPreferences pref = getSharedPreferences(Consts.PREF_FILE, MODE_PRIVATE);
                    pref.edit().putBoolean(Consts.PREF_SHOW_ICON, isChecked).commit();
                    MainActivity.this.chAdapter.setShowIcon(isChecked);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private class ChannelLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private String searchTerm;

        public ChannelLoaderCallback() {
            this.searchTerm = null;
        }

        public ChannelLoaderCallback(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            Uri uri = Uri.parse("content://" + Consts.CHANNEL_PROVIER_URI + "/" + Consts.TB_RADIO_CHANNEL);
            String[] projection = new String[]{
                    ChRow.ID,
                    ChRow.CH_FREQ,
                    ChRow.CH_NAME,
                    ChRow.PLAY_URL,
                    ChRow.LOGO_URL,
                    ChRow.REG_DATE,
                    ChRow.FIX_DATE,
                    ChRow.MEMO
            };
            String selection = null;
            String sortOrder = null;
            if (searchTerm != null) {

            }
            return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            MainActivity.this.chAdapter.swapCursor(data);
            String totalSite = MainActivity.this.getResources().getString(R.string.cfg_total_item) + MainActivity.this.chAdapter.getItemCount();
            MainActivity.this.tvTotalSite.setText(totalSite);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            MainActivity.this.chAdapter.swapCursor(null);
            String totalSite = MainActivity.this.getResources().getString(R.string.cfg_total_item) + MainActivity.this.chAdapter.getItemCount();
            MainActivity.this.tvTotalSite.setText(totalSite);
        }
    }

    private class ChRowClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Object o = parent.getItemAtPosition(position);
            if (o instanceof ChRow) {
                final ChRow chrow = (ChRow) o;
                if (Consts.DEBUG) {
                    Log.i(TAG, "o is ChRow!!!!!!");
                    Log.i(TAG, "id = " + chrow.getId());
                    Log.i(TAG, "name = " + chrow.getChName());
                    Log.i(TAG, "url = " + chrow.getPlayUrl());
                }

                Intent i = new Intent(MainActivity.this, ChEditActivity.class);
                i.putExtra(Consts.EXTRA_ARG_ID, chrow.getId());
                MainActivity.this.startActivityForResult(i, Consts.ACTION_MODIFY);
            } else {
                if (Consts.DEBUG) {
                    Log.i(TAG, "o is not ChRow!!!!!!");
                }
            }
        }
    }

    private class ChRowLongClickedListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Object o = parent.getItemAtPosition(position);
            if (o instanceof ChRow) {
                final ChRow pbrow = (ChRow) o;
                if (Consts.DEBUG) {
                    Log.i(TAG, "o is ChRow!!!!!!");
                    Log.i(TAG, "id = " + pbrow.getId());
                    Log.i(TAG, "name = " + pbrow.getChName());
                    Log.i(TAG, "url = " + pbrow.getPlayUrl());
                }

                String popupTitle = MainActivity.this.getResources().getString(R.string.chp_delete);
                String popupMessage = MainActivity.this.getResources().getString(R.string.chp_delete_msg);
                popupMessage += "\n [" + pbrow.getChFreq() + "] " + pbrow.getChName();

                String txtDelete = MainActivity.this.getResources().getString(R.string.delete);
                String txtCancel = MainActivity.this.getResources().getString(R.string.cancel);

                AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
                adBuilder.setTitle(popupTitle);
                adBuilder.setMessage(popupMessage);
                adBuilder.setPositiveButton(txtDelete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                String selectionClause = "id = ?";
                                String[] selectionArgs = {Long.toString(pbrow.getId())};
                                int nDeleted = getContentResolver().delete(
                                        Uri.parse("content://" + Consts.CHANNEL_PROVIER_URI + "/" + Consts.TB_RADIO_CHANNEL),
                                        selectionClause,
                                        selectionArgs
                                );
                            }
                        });
                adBuilder.setNegativeButton(txtCancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // Nothing to do
                            }
                        });
                adBuilder.create().show();
            } else {
                if (Consts.DEBUG) {
                    Log.i(TAG, "o is not ChRow!!!!!!");
                }
            }

            return true;
        }
    }

    private class SearchAdapter implements SearchView.OnQueryTextListener {

        private ChRowAdapter myAdapter;

        public SearchAdapter(ChRowAdapter pbAdapter) {
            this.myAdapter = pbAdapter;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            if (this.myAdapter != null) {
                getChannelListFromProvider(query);
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if ((this.myAdapter != null) && (newText == null || newText.length() == 0)) {
                getAllChannelListFromProvider();
                return true;
            }
            return false;
        }
    }

    private class CsvFileSelectedListener implements ChooseFileDialog.FileChosenListener {
        @Override
        public void onFileChosen(File chosenFile) {
            ChDbInterface pdi = ChDbManager.getInstance().getPbDbInterface();
            new ChImporter(pdi, chosenFile, new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "File is imported!!", Toast.LENGTH_SHORT).show();
                    MainActivity.this.getAllChannelListFromProvider();
                }
            }).execute();
        }
    }
}
