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
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class SourceDownloader extends AsyncTask<Void,Void, ArrayList<Source>>
{
    @SuppressLint("StaticFieldLeak")
    private
    MainActivity mainActivity;
    private static final String TAG = "SourceDownloader";
    private Map<String, ArrayList<Source>> hashMap = new TreeMap<>();

    SourceDownloader(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    protected ArrayList<Source> doInBackground(Void... voids)
    {
        ArrayList<Source> finalData;
        String URL = "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=" + BuildConfig.API_KEY;
        String data = getSourceDatafromURL(URL);
        finalData = parseData(data);
        return finalData;
    }

    private String getSourceDatafromURL(String URL)
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
            Log.e(TAG, "EXCEPTION | SourceDownloader: getSourceDatafromURL: bp:", e);
            return sb.toString();
        }

        return sb.toString();
    }

    private ArrayList<Source> parseData(String data)
    {
        ArrayList<Source> tempList = new ArrayList<>();
        Source source;

        try
        {
            JSONObject temp = new JSONObject(data);
            JSONArray sources = (JSONArray) temp.get("sources");
            Log.d(TAG, "parseData: bp: Total Sources: " + sources.length());
            for(int i = 0; i < sources.length(); i++)
            {
                source = new Source();
                JSONObject sourceObj = (JSONObject) sources.get(i);
                source.setId(getIDfromData(sourceObj));
                source.setName(getNamefromData(sourceObj));
                source.setCategory(getCategoryfromData(sourceObj));
                tempList.add(source);
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | parseData: bp: " + e);
        }
        return  tempList;
    }

    private String getIDfromData(JSONObject sourceObj)
    {
        String ID = "";
        try
        {
            ID = sourceObj.getString("id");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getIDfromData: bp: " + e);
        }
        return ID;
    }

    private String getNamefromData(JSONObject sourceObj)
    {
        String name = "";
        try
        {
            name = sourceObj.getString("name");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getNamefromData: bp: " + e);
        }
        return name;
    }

    private String getCategoryfromData(JSONObject sourceObj)
    {
        String category = "";
        try
        {
            category = sourceObj.getString("category");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getCategoryfromData: bp: " + e);
        }
        return category;
    }

    @Override
    protected void onPostExecute(ArrayList<Source> sourceArrayList)
    {
        // New arraylist
        ArrayList<Source> arr = new ArrayList<>();

        // Get hashmap arraylist and modify it
        ArrayList<Source> temp;

        hashMap.put("all",sourceArrayList);
        for(Source source:sourceArrayList)
        {
            arr.clear();
            Source source1 = new Source();
            source1.setId(source.getId());
            source1.setName(source.getName());
            source1.setCategory(source.getCategory());

            if(hashMap.containsKey(source.getCategory().toLowerCase()))
            {
                temp = hashMap.get(source.getCategory());
                temp.add(source1);
                hashMap.put(source.getCategory(),temp);
            }
            else
            {
                hashMap.put(source.getCategory(),new ArrayList<Source>());
                temp = hashMap.get(source.getCategory());
                temp.add(source1);
                hashMap.put(source.getCategory(),temp);
            }
        }
        super.onPostExecute(sourceArrayList);
        Log.d(TAG, "onPostExecute: " + hashMap);
        mainActivity.setSources(hashMap);
    }
}
