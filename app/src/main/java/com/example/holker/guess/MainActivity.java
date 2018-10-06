package com.example.holker.guess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrl = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    int locationCorrect = 0;
    String[] answers = new String[4];

    ImageView mImageView;
    Button mButton0;
    Button mButton1;
    Button mButton2;
    Button mButton3;

    public void updateQuest() {
        Random random = new Random();
        chosenCeleb = random.nextInt(celebNames.size());

        ImageDownload imageDownload = new ImageDownload();

        Bitmap celebImage;
        try {
            celebImage = imageDownload.execute(celebUrl.get(chosenCeleb)).get();
            mImageView.setImageBitmap(celebImage);

            locationCorrect = random.nextInt(4);

            int wrongAnswerLocation;

            for (int i = 0; i < 4; i++) {
                if (i == locationCorrect) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    wrongAnswerLocation = random.nextInt(celebUrl.size());
                    while (wrongAnswerLocation == chosenCeleb) {
                        wrongAnswerLocation = random.nextInt(celebUrl.size());
                    }
                    answers[i] = celebNames.get(wrongAnswerLocation);
                }
            }

            mButton0.setText(answers[0]);
            mButton1.setText(answers[1]);
            mButton2.setText(answers[2]);
            mButton3.setText(answers[3]);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onChose(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationCorrect))) {
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
            updateQuest();
        } else {
            Toast.makeText(getApplicationContext(), "Wrong answer !", Toast.LENGTH_SHORT).show();
        }
    }


    public class ImageDownload extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    protected class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find
        mImageView = (ImageView) findViewById(R.id.iv_main);
        mButton0 = (Button) findViewById(R.id.button1);
        mButton1 = (Button) findViewById(R.id.button2);
        mButton2 = (Button) findViewById(R.id.button3);
        mButton3 = (Button) findViewById(R.id.button4);


        DownloadTask task = new DownloadTask();
        String resultExecute;

        try {
            resultExecute = task.execute("http://www.posh24.se/kandisar/").get();
            //<div class="sidebarContainer">

            String[] splitResultExecute = resultExecute.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");

            Matcher m = p.matcher(splitResultExecute[0]);

            while (m.find()) {
                celebUrl.add(m.group(1));
            }


            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResultExecute[0]);
            while (m.find()) {
                celebNames.add(m.group(1));
            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateQuest();
    }
}
