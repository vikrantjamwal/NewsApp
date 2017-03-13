package com.android.vik.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        ArrayList<News> articles = new ArrayList<News>();
        adapter = new NewsAdapter(this, articles);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        if (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()) {
            ArticleFetchTask articleFetchTask = new ArticleFetchTask();
            articleFetchTask.execute();
        } else {
            Toast.makeText(this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public class ArticleFetchTask extends AsyncTask<String, Void, News[]> {

        private int maxResults = 15;

        @Override
        protected News[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String articleListJsonStr = null;

            try {

                final String NEWS_LIST_BASE_URL = " http://content.guardianapis.com/search?";
                final String MAX_RESULT_PARAM = "page-size";
                final String API_KEY = "api-key";

                Uri builtUri = Uri.parse(NEWS_LIST_BASE_URL).buildUpon()
                        .appendQueryParameter("show-fields", "thumbnail")
                        .appendQueryParameter(MAX_RESULT_PARAM, Integer.toString(maxResults))
                        .appendQueryParameter(API_KEY, "test")
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    articleListJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    articleListJsonStr = null;
                }
                articleListJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("News list Activity", "Error ", e);
                articleListJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("News list activity", "Error closing stream", e);
                    }
                }
            }

            try {
                return getArticleFromJson(articleListJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(News[] result) {
            if (result != null) {
                adapter.clear();
                for (News article : result) {
                    adapter.add(article);
                }
            }
        }

        private News[] getArticleFromJson(String articleListJsonStr) throws JSONException {

            JSONObject articleList = new JSONObject(articleListJsonStr);
            JSONObject response = articleList.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            News[] resultObj = new News[maxResults];

            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String title = result.getString("webTitle");
                String url = result.getString("webUrl");
                String thumbnail = null;
                if (result.has("fields")) {
                    JSONObject fieldsObject = result.getJSONObject("fields");
                    if (fieldsObject != null) {
                        thumbnail = fieldsObject.getString("thumbnail");
                    }
                }
                resultObj[i] = new News(thumbnail, title, url);
            }
            return resultObj;
        }
    }
}
