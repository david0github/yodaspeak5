package edu.quinnipiac.app.yoda;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResultActivity extends Activity {

    private ShareActionProvider sap;
    public static String translation;
    public static String output;
    private final String LOG_TAG = ResultActivity.class.getSimpleName();
    private final String[] samples =
            {"The dark side is near!", "I am sorry Mace Windu!", "I will destroy you General Grievous!",
                    "Hi there, Obi Wan!", "Is Rey Luke's daughter?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        String result = (String) intent.getExtras().get(translation);
        TextView resultView = (TextView) findViewById(R.id.resultView);
        resultView.setText(result);
    }

    //Executes AsyncTask when the button is clicked
    public void onClick(View view) {
        int random = (int) Math.floor(Math.random() * 4);
        String randomText = samples[random];
        String randomTextFormatted = "&sentence=" + randomText.replaceAll(" ", "+") + "&json=true";
        new FetchYodaSpeak().execute(randomTextFormatted);
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
            Fragment fragment = new RandomFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.relativeLayout, fragment);
            fragmentTransaction.addToBackStack("");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
            Button button = (Button) findViewById(R.id.randomizeButton);
            button.setVisibility(View.INVISIBLE);
        }
    }
}
