package ml.chiragkhandhar.newsgateway;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArticleDownloader extends AsyncTask<String,Void, ArrayList<Article>>
{
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private ArrayList<Article> articleArrayList = new ArrayList<>();

    private static final String TAG = "ArticleDownloader";

    ArticleDownloader(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    protected ArrayList<Article> doInBackground(String... strings)
    {
        String source = strings[0];
        String URL = "https://newsapi.org/v2/everything?sources=" + source + "&language=en&pageSize=100&apiKey=" + BuildConfig.API_KEY;
        String data = getArticleDataFromURL(URL);
        articleArrayList = parseJSON(data);

        return null;
    }


    private String getArticleDataFromURL(String URL)
    {
        Uri dataUri = Uri.parse(URL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();

        try
        {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line).append('\n');
        }
        catch (Exception e)
        {
            Log.e(TAG, "EXCEPTION | ArticleDownloader: getArticleDataFromURL: bp:", e);
            return sb.toString();
        }

        return sb.toString();
    }

    private ArrayList<Article> parseJSON(String data)
    {
        ArrayList<Article> tempList = new ArrayList<>();
        Article tempArticle;

        try
        {
            JSONObject temp = new JSONObject(data);
            JSONArray articles = (JSONArray) temp.get("articles");

            for(int i = 0; i<articles.length(); i++)
            {
                JSONObject article = (JSONObject) articles.get(i);
                tempArticle = new Article();

                tempArticle.setTitle(getTitlefromData(article));
                tempArticle.setAuthor(getAuthorfromData(article));
                tempArticle.setDescription(getDescfromData(article));
                tempArticle.setPublishedAt(convertDate(getPublishingDatefromDATA(article)));            // Converting Date from Z format to Simple Date Format
                tempArticle.setUrl(getArticleUrlfromData(article));
                tempArticle.setUrlToImage(getUrlToImagefromData(article));
                tempList.add(tempArticle);
                Log.d(TAG, "parseJSON: bp:----------------------Article " + i +"--------------------------------");
                Log.d(TAG, "parseJSON: bp: Title: " + tempArticle.getTitle());
                Log.d(TAG, "parseJSON: bp: Author: " + tempArticle.getAuthor());
                Log.d(TAG, "parseJSON: bp: Pub Date: " + tempArticle.getPublishedAt());
                Log.d(TAG, "parseJSON: bp: Desc: " + tempArticle.getDescription());
                Log.d(TAG, "parseJSON: bp: URL: " + tempArticle.getUrl());
                Log.d(TAG, "parseJSON: bp: Image URL: " + tempArticle.getUrlToImage());
                Log.d(TAG, "parseJSON: bp:------------------------------------------------------");
            }

        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | parseJSON: bp: " + e);
        }
        return tempList;
    }

    private String convertDate(String stringDate)
    {
        Date date = null;
        String public_date;
        try
        {
            if (stringDate != null)
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(stringDate);
            String pattern = "MMM dd, yyyy HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            public_date = simpleDateFormat.format(date);
            Log.d(TAG, "rerturnDate: "+public_date);
            return public_date;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    private String getTitlefromData(JSONObject article)
    {
        String title = "";
        try
        {
            title = article.getString("title");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getTitlefromData: bp: " + e);
        }
        return title;
    }

    private String getAuthorfromData(JSONObject article)
    {
        String author = "";
        try
        {
            author = article.getString("author");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getAuthorfromData: bp: " + e);
        }
        return author;
    }

    private String getDescfromData(JSONObject article)
    {
        String desc = "";
        try
        {
            desc = article.getString("description");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getDescfromData: bp: " + e);
        }
        return desc;
    }


    private String getPublishingDatefromDATA(JSONObject article)
    {
        String publishingDate = "";
        try
        {
            publishingDate = article.getString("publishedAt");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getPublishingDatefromDATA: bp: " + e);
        }
        return publishingDate;
    }

    private String getArticleUrlfromData(JSONObject article)
    {
        String articleUrl = "";
        try
        {
            articleUrl = article.getString("url");
        }
        catch(Exception e)
        {
            Log.d(TAG, "EXCEPTION | getArticleUrlfromData: bp: " + e);
        }
        return articleUrl;
    }

    private String getUrlToImagefromData(JSONObject article)
    {
        String urlToImage = "";
        try
        {
            urlToImage = article.getString("urlToImage");
        }
        catch(Exception e)
        {
            Log.d(TAG, "EXCEPTION | getUrlToImagefromData: bp: " + e);
        }
        return urlToImage;
    }


    @Override
    protected void onPostExecute(ArrayList<Article> articles)
    {
        super.onPostExecute(articles);
    }
}
