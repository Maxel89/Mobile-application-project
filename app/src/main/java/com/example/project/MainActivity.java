package com.example.project;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Salary_declaration> sds;
    ArrayAdapter<Salary_declaration> adapter;

    private Toolbar mTopToolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTopToolbar = (Toolbar) findViewById(R.id.menu);
        setSupportActionBar(mTopToolbar);

        sds = new ArrayList<Salary_declaration>();
        adapter = new ArrayAdapter<Salary_declaration>(this, android.R.layout.simple_list_item_1, sds);
        new JsonTask().execute("https://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=a18marax");
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Salary_declaration sd = sds.get(position);
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.listView),
                        sd.getName() + " (" + sd.getCompany() + ")" + "\n" + "Typ: " + sd.getCategory() + " " + "Lön: " + sd.getSalary(),
                        10000);
                snackbar.setAction("Stäng", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        });
    }

    public void OnOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_favorite:
            showDialogMenu();
            break;
        }
    }

    private void showDialogMenu(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Om företaget");
        builder.setMessage("Faktura AB tillhandahåller en faktura tjänst för dig som håller på med verksamhet i lagens gråzon (eller rent av olaglig verkashet vi dömmer ingen).");
        AlertDialog infoDialog = builder.create();
        infoDialog.show();
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;

        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null && !isCancelled()) {
                    builder.append(line).append("\n");
                }
                return builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            try {
                JSONArray jsonArray = (JSONArray) new JSONTokener(json).nextValue();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject element = jsonArray.optJSONObject(i);
                    String ID = element.getString("ID");
                    String name = element.getString("name");
                    String company = element.getString("company");
                    String category = element.getString("category");
                    int cost = element.getInt("cost");
                    sds.add(new Salary_declaration(ID, name, company, category, cost));
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
