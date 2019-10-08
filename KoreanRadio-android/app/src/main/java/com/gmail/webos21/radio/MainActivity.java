package com.gmail.webos21.radio;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.webos21.android.patch.PRNGFixes;
import com.gmail.webos21.android.widget.ChooseFileDialog;
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

    private ItemTouchHelper ith;

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

        ith = new ItemTouchHelper(new SwipeCallback());
        ith.attachToRecyclerView(chlist);

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
        Log.d(TAG, "[getAllChannelListFromProvider]");
        getSupportLoaderManager().restartLoader(Consts.MAIN_LOADER_ID, null, new ChannelLoaderCallback());
    }

    private void getChannelListFromProvider(String searchTerm) {
        Log.d(TAG, "[getChannelListFromProvider] searchTerm = " + searchTerm);
        getSupportLoaderManager().restartLoader(Consts.MAIN_LOADER_ID, null, new ChannelLoaderCallback(searchTerm));
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

        new ChExporter(MainActivity.this, csvFile, new Runnable() {
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
            Uri uri = Uri.parse("content://" + Consts.CHANNEL_PROVIER_AUTHORITY + "/" + Consts.TB_RADIO_CHANNEL);
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
            String[] selectionArgs = null;
            String sortOrder = null;

            Log.d(TAG, "[ChannelLoaderCallback] searchTerm = " + searchTerm);

            if (searchTerm != null) {
                selection = "(" + ChRow.CH_FREQ + " LIKE ?) OR (" + ChRow.CH_NAME + " LIKE ?) OR (" + ChRow.MEMO + " LIKE ?)";
                selectionArgs = new String[]{"%" + searchTerm + "%", "%" + searchTerm + "%", "%" + searchTerm + "%"};
            }
            return new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            Log.d(TAG, "[ChannelLoaderCallback] onLoadFinished / data = " + data.getCount());
            MainActivity.this.chAdapter.swapCursor(data);
            String totalSite = MainActivity.this.getResources().getString(R.string.cfg_total_item) + MainActivity.this.chAdapter.getItemCount();
            MainActivity.this.tvTotalSite.setText(totalSite);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            Log.d(TAG, "[ChannelLoaderCallback] onLoaderReset");
            MainActivity.this.chAdapter.swapCursor(null);
            String totalSite = MainActivity.this.getResources().getString(R.string.cfg_total_item) + MainActivity.this.chAdapter.getItemCount();
            MainActivity.this.tvTotalSite.setText(totalSite);
        }
    }

    private class SwipeCallback extends ItemTouchHelper.SimpleCallback {

        private Drawable background, markPlay, markDelete;
        private int markMargin;

        public SwipeCallback() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        public SwipeCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int swipedPosition = viewHolder.getAdapterPosition();
            Long itemId = MainActivity.this.chAdapter.getItemId(swipedPosition);

            if (ItemTouchHelper.RIGHT == direction) {
                RadioApp app = (RadioApp) getApplicationContext();
                if (app != null) {
                    RadioServiceHelper rsh = app.getRadioServiceHelper();
                    MainActivity.this.chAdapter.getCursor().moveToPosition(swipedPosition);
                    rsh.play(ChRow.bindCursor(MainActivity.this.chAdapter.getCursor()));
                }
                MainActivity.this.chAdapter.notifyItemChanged(swipedPosition);
            } else if (ItemTouchHelper.LEFT == direction) {
                String selectionClause = "id = ?";
                String[] selectionArgs = {Long.toString(itemId)};
                int nDeleted = getContentResolver().delete(
                        Uri.parse("content://" + Consts.CHANNEL_PROVIER_AUTHORITY + "/" + Consts.TB_RADIO_CHANNEL),
                        selectionClause,
                        selectionArgs
                );
                MainActivity.this.getAllChannelListFromProvider();
            } else {
                /* Nothing to do */
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;

                markDelete = ContextCompat.getDrawable(itemView.getContext(), android.R.drawable.ic_delete);
                markDelete.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

                markPlay = ContextCompat.getDrawable(itemView.getContext(), android.R.drawable.ic_media_play);
                markPlay.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

                markMargin = (int) itemView.getContext().getResources().getDimension(R.dimen.pb_home_hmargin);

                // Item 을 좌측으로 Swipe 했을 때 Background 변화: ItemTouchHelper.LEFT
                if (dX < 1) {
                    background = new ColorDrawable(Color.parseColor("#FFD32F2F"));
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    //dX(dY): 사용자 동작에 의한 수평(수직) 변화의 양
                    background.draw(c); //Bounds: 범위. draw: 그리기. - 사용자 동작에 따라 Item 의 Background 변화

                    // Mark 그리기
                    int itemHeight = itemView.getBottom() - itemView.getTop(); // Item 높이
                    int markWidth = markDelete.getIntrinsicWidth(); // Intrinsic: 본질적 - xMark 의 실제 길이
                    int markHeight = markDelete.getIntrinsicHeight();

                    int markLeft = itemView.getRight() - markMargin - markWidth;
                    int markRight = itemView.getRight() - markMargin;
                    int markTop = itemView.getTop() + (itemHeight - markHeight) / 2;
                    int markBottom = markTop + markHeight;
                    markDelete.setBounds(markLeft, markTop, markRight, markBottom);
                    markDelete.draw(c);
                } else { // ItemTouchHelper.RIGHT
                    background = new ColorDrawable(Color.parseColor("#FF388E3C"));
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
                    background.draw(c);

                    // Mark 그리기
                    int itemHeight = itemView.getBottom() - itemView.getTop(); // Item 높이
                    int markWidth = markPlay.getIntrinsicWidth(); // Intrinsic: 본질적 - xMark 의 실제 길이
                    int markHeight = markPlay.getIntrinsicHeight();

                    int markLeft = itemView.getLeft() + markMargin;
                    int markRight = itemView.getLeft() + markMargin + markWidth;
                    int markTop = itemView.getTop() + (itemHeight - markHeight) / 2;
                    int markBottom = markTop + markHeight;
                    markPlay.setBounds(markLeft, markTop, markRight, markBottom);
                    markPlay.draw(c);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
                Log.d(TAG, "[onQueryTextSubmit] query = " + query);
                MainActivity.this.getChannelListFromProvider(query);
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if ((this.myAdapter != null) && (newText == null || newText.length() == 0)) {
                Log.d(TAG, "[onQueryTextChange] newText = " + newText);
                MainActivity.this.getAllChannelListFromProvider();
                return true;
            }
            return false;
        }
    }

    private class CsvFileSelectedListener implements ChooseFileDialog.FileChosenListener {
        @Override
        public void onFileChosen(File chosenFile) {
            new ChImporter(getApplicationContext(), chosenFile, new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "File is imported!!", Toast.LENGTH_SHORT).show();
                    MainActivity.this.getAllChannelListFromProvider();
                }
            }).execute();
        }
    }
}
