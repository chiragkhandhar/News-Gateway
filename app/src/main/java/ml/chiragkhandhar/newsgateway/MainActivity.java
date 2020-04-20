package ml.chiragkhandhar.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    Menu menu;
    Map<String, ArrayList<Source>> globalHM;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpComponents();
        new SourceDownloader(this).execute();
    }

    private void setUpComponents()
    {


    }

    public void setSources(Map<String, ArrayList<Source>> hashMap)
    {
       menu.clear();
       globalHM = hashMap;
       int i = 0;
       for(String temp: hashMap.keySet())
           menu.add(menu.NONE,i++,menu.NONE,showCamelCase(temp));
    }

    public void printMap(Map<String, ArrayList<Source>> hashMap)
    {
        for(String key: hashMap.keySet())
        {
            Log.d(TAG, "printMap: bp:==============================================");
            Log.d(TAG, "printMap: bp: key: " + key);
            Log.d(TAG, "printMap: bp: Number of Value: " + hashMap.get(key).size());
            Log.d(TAG, "printMap: bp:==============================================");
            for(Source s:hashMap.get(key))
            {
                Log.d(TAG, "printMap: bp: ID :" + s.getId());
                Log.d(TAG, "printMap: bp: Name :" + s.getName());
                Log.d(TAG, "printMap: bp: Category :" + s.getCategory());
                Log.d(TAG, "printMap: bp:---------------------------------------------");
            }
        }
    }

    public String showCamelCase(String str)
    {
        String CamelCase="";
        String[] parts = str.split("_");
        for(String part:parts)
        {
            String as = part.toLowerCase();
            int a = as.length();
            CamelCase = String.format("%s%s%s", CamelCase, as.substring(0, 1).toUpperCase(), as.substring(1, a));
        }
        return CamelCase;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        Toast.makeText(this, "Number: " + globalHM.get(item.getTitle().toString().toLowerCase()).size(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
