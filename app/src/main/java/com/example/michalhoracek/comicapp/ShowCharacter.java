package com.example.michalhoracek.comicapp;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.service.autofill.FieldClassification;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

public class ShowCharacter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_character);
        Bundle extras = getIntent().getExtras();
        TextView characterNameInput = findViewById(R.id.characterName);
        ImageView characterThumbnail = findViewById(R.id.characterImage);
        characterNameInput.setText(extras.getString("charactername"));
        Picasso.get().load(extras.getString("thumbnailImgUrl")).into(characterThumbnail);
        final TableLayout tableView = findViewById(R.id.tablelayout);
       // tableView.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        final TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        tableView.setShrinkAllColumns(true);
        tableView.setStretchAllColumns(true);
        TextView txtView = new TextView(this);
        txtView.setTextSize(15);
        TableRow newRow = new TableRow(this);
        newRow.setGravity(Gravity.CENTER);
        newRow.addView(txtView);
        tableView.addView(newRow);

        final String comicsURL = extras.getString("comicsurl");

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(comicsURL)
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
                    LinearLayout llh = new LinearLayout(ShowCharacter.this);
                    llh.setOrientation(LinearLayout.VERTICAL);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject row = results.getJSONObject(i);
                        String title = row.getString("title");
                        TextView txtView = new TextView(ShowCharacter.this);
                        txtView.setText(title);
                        txtView.setTextSize(15);
                        TableRow newRow = new TableRow(ShowCharacter.this);
                        //newRow.setGravity(Gravity.CENTER);
                        //newRow.setLayoutParams(lp);
                        ImageView comicspicture = new ImageView(ShowCharacter.this);

                        LinearLayout ll = new LinearLayout(ShowCharacter.this);
                       /* LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(200),
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        ll.setLayoutParams(lp);*/
                       // ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        JSONObject thumbnail = row.getJSONObject("thumbnail");
                        Picasso.get().load(thumbnail.getString("path")+"/portrait_xlarge.jpg").into(comicspicture);
                        newRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        ll.addView(comicspicture);
                        //comicspicture.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT));
                        txtView.setPadding(10,0,0,0);
                        ll.addView(txtView);
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        //newRow.addView(txtView);
                        //newRow.addView(comicspicture);
                        newRow.addView(ll);
                        newRow.setPadding(0,5,0,0);
                        llh.addView(newRow);
                    }
                    tableView.addView(llh);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
