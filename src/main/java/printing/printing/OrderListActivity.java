package printing.printing;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OrderListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ArrayList<OrdersModel> dataModels;
    ListView listView;
    private static CustomOrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        listView=(ListView)findViewById(R.id.list);


        dataModels= new ArrayList<>();

        adapter= new CustomOrdersAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);



        new ParseTask().execute();

    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {

                final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                final String tmDevice, tmSerial, androidId;
                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

                URL url = new URL("http://kolchanov.info/info.php?author="+androidId);

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
                JSONArray newsList = dataJsonObj.getJSONArray("orders");

                dataModels= new ArrayList<>();

                if (newsList.length() == 0) {
                    dataModels.add(new OrdersModel("У вас еще нет заказов", "Когда вы сделаете заказ, он появится здесь", "1", ""));
                }

                for (int i = 0; i < newsList.length(); i++) {
                    JSONObject news = newsList.getJSONObject(i);
                    String id = news.getString("id");

                    Log.d("Info", "You may use this ID to change status of order: " + id);
                    
                    String title = news.getString("title");
                    String comment = news.getString("comment");
                    String status = news.getString("status");

                    dataModels.add(new OrdersModel(title, comment, "1", status));

                }

                listView = (ListView)findViewById(R.id.list);

                adapter= new CustomOrdersAdapter(dataModels, getApplicationContext());

                listView.setAdapter(adapter);


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

        } else if (id == R.id.list) {
            Intent intent = new Intent(this, OrderListActivity.class);
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
