package com.example.michalhoracek.comicapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DisplayMessageActivity extends AppCompatActivity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Button button = findViewById(R.id.button3);
        final EditText editinput = findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        String InputText = editinput.getText().toString().replace(" ", "+");
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("https://gateway.marvel.com/v1/public/characters?ts=thesoer&apikey=001ac6c73378bbfff488a36141458af2&hash=72e5ed53d1398abb831c3ceec263f18b&name="+InputText)
                                .build();
                        Response response = null;

                        try{
                            response = client.newCall(request).execute();
                            return response.body().string();

                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        //textView.setText(o.toString());
                        try {
                            JSONObject root = new JSONObject(o.toString());
                            JSONObject data = root.getJSONObject("data");
                            org.json.JSONArray results = data.getJSONArray("results");
                            int count = data.getInt("count");
                            if(count > 0)
                            {
                                JSONObject resultt = results.getJSONObject(0);
                                JSONObject thumbnail = resultt.getJSONObject("thumbnail");
                                JSONObject comics = resultt.getJSONObject("comics");
                                Intent intent = new Intent(DisplayMessageActivity.this, ShowCharacter.class);
                                //EditText editText = (EditText) findViewById(R.id.editText);
                                //String message = editText.getText().toString();
                                intent.putExtra("charactername", resultt.getString("name"));
                                intent.putExtra("comicsurl", comics.getString("collectionURI")+"?ts=thesoer&apikey=001ac6c73378bbfff488a36141458af2&hash=72e5ed53d1398abb831c3ceec263f18b");
                                intent.putExtra("thumbnailImgUrl", thumbnail.getString("path")+"/portrait_uncanny.jpg");
                                startActivity(intent);
                            }
                            else
                            {
                                Toast toast = Toast.makeText(getApplicationContext(), "Character doesn't exist !!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }
        });

    }
}
