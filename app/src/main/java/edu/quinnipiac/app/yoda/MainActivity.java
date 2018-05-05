package edu.quinnipiac.app.yoda;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private boolean switchedTitle, listSelection, shiftedColor = false;
    public static String output;
    private String[] samples;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    Toolbar toolbar;
    FrameLayout FL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        samples = getResources().getStringArray(R.array.yoda_samples);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        //Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, samples));
        //Set the list's click listeners
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        FL = (FrameLayout) findViewById(R.id.content_frame);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_title:
                // User chose the "Settings" item, show the app settings UI...
                if (!switchedTitle) {
                    toolbar.setTitle("Whoa");
                    switchedTitle = true;
                    return true;
                } else {
                    toolbar.setTitle("Yoda");
                    switchedTitle = false;
                    return true;
                }

            case R.id.action_share:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;


            case R.id.action_settings:
                if (!shiftedColor) {
                    FL.setBackgroundColor(Color.DKGRAY);
                    switchedTitle = true;
                    return true;
                } else {
                    FL.setBackgroundColor(Color.WHITE);
                    switchedTitle = false;
                    return true;
                }

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            listSelection = true;
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        String formattedInput = "&sentence=" + samples[position].replaceAll(" ", "+") + "&json=true";
        drawerList.setItemChecked(position, true);
        new FetchYodaSpeak().execute(formattedInput);

    }

    //Executes AsyncTask when the button is clicked
    public void onClick(View view) {
        EditText userInput = (EditText) findViewById(R.id.userInput);
        String userText = userInput.getText().toString();
        String userTextFormatted = "&sentence=" + userText.replaceAll(" ", "+") + "&json=true";
        new FetchYodaSpeak().execute(userTextFormatted);
        drawerLayout.closeDrawer(drawerList);
    }

    private class FetchYodaSpeak extends AsyncTask<String,Void,String> {

        public String yodaSpeakString;

        @Override
        protected String doInBackground(String...params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the Yoda Speak query
                URL url = new URL("https://yoda.p.mashape.com/yoda?mashape-key=iGYZQBB98omshsfPQkJTcL5Px2v7p1MUqdmjsnRP5Zs0muNedo" + params[0]);
                // Create the request to open YodaSpeak, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) { return null; }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    System.out.println(buffer);
                }
                if (buffer.length() == 0) { return null; }
                //Response from Yoda Speak API
                yodaSpeakString = buffer.toString();
                Log.i(LOG_TAG, yodaSpeakString);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e.getMessage(), e);
                return null;
            } finally {
                if (urlConnection != null) { urlConnection.disconnect(); }
                if (reader != null) {
                    try { reader.close(); } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            if (yodaSpeakString == null) { Log.e(LOG_TAG, "Error retrieving YodaSpeak"); }
            return yodaSpeakString;
        }

        protected void onPostExecute(String result) {
            output = result;
            if (listSelection) {
                Fragment fragment = new YodaFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
                drawerLayout.closeDrawers();
                listSelection = false;
            } else {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(ResultActivity.translation, result);
                startActivity(intent);
            }
        }
    }
}