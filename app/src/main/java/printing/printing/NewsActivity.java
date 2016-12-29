package printing.printing;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<NewsModel> dataModels;
    ListView listView;
    private static CustomNewsAdapter adapter;

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "cache", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("app", "--- onCreate database ---");

            db.execSQL("create table news ("
                    + "id integer primary key autoincrement,"
                    + "title text,"
                    + "content text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("news", null, null, null, null, null, null);

        dataModels= new ArrayList<>();

        boolean usedBefore = false;

        if (c.moveToFirst()) {

            usedBefore = true;

            int titleId = c.getColumnIndex("title");
            int contentId = c.getColumnIndex("content");

            do {
                dataModels.add(new NewsModel(c.getString(titleId), c.getString(contentId), "1"));
            } while (c.moveToNext());
        }

        listView = (ListView)findViewById(R.id.list);


        adapter= new CustomNewsAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);

        new ParseTask(usedBefore).execute();

    }



    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        Boolean usedBefore;

        ParseTask(boolean usedBefore) {
            this.usedBefore = usedBefore;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL("http://kolchanov.info/news.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            JSONObject dataJsonObj = null;

            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray newsList = dataJsonObj.getJSONArray("news");

                dataModels= new ArrayList<>();

                Context context = getBaseContext();
                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();


                for (int i = 0; i < newsList.length(); i++) {
                    JSONObject news = newsList.getJSONObject(i);
                    String title = news.getString("title");
                    String content = news.getString("content");

                    ContentValues cv = new ContentValues();

                    cv.put("id", i);
                    cv.put("title", title);
                    cv.put("content", content);

                    // We used hack. SQLite store unique ID, so this code will add only new news (which are't in database)

                    try {
                        long rowID = db.insert("news", null, cv);
                        Log.d("app", "row inserted, ID = " + rowID);
                    } catch (Exception e) {}

                    if(!usedBefore) {
                        dataModels.add(new NewsModel(title, content, "1"));
                    }
                 }

                listView = (ListView)findViewById(R.id.list);

                if(!usedBefore) {
                    adapter= new CustomNewsAdapter(dataModels,getApplicationContext());

                    listView.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.services) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.news) {
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);

        } else if (id == R.id.order) {
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);

        } else if (id == R.id.ask) {
            Intent intent = new Intent(this, AskActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
