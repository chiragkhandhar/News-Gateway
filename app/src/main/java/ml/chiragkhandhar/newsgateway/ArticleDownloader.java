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
    private NewsService newsService;
    private ArrayList<Article> articleArrayList = new ArrayList<>();

    private static final String TAG = "ArticleDownloader";

    ArticleDownloader(NewsService newsService)
    {
        this.newsService = newsService;
    }

    @Override
    protected ArrayList<Article> doInBackground(String... strings)
    {
        String source = strings[0];
        String URL = "https://newsapi.org/v2/everything?sources=" + source + "&language=en&pageSize=100&apiKey=" + BuildConfig.API_KEY;
        Log.d(TAG, "doInBackground: URL: bp:" + URL);
        String data = getArticleDataFromURL(URL);
        articleArrayList = parseJSON(data);
        return articleArrayList;
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
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | parseJSON: bp: " + e);
        }
        Log.d(TAG, "bp: parseJSON: Number of Articles: " + tempList.size());
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
            if(article.has("title"))
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
            if(article.has("author"))
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
            if(article.has("description"))
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
            if(article.has("publishedAt"))
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
            if(article.has("url"))
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
            if(article.has("urlToImage"))
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
        Log.d(TAG, "bp: onPostExecute: Number of Articles: " + articles.size());
        newsService.setArticles(articles);
        super.onPostExecute(articles);
    }
}
