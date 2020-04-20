package ml.chiragkhandhar.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
//        int i = 0;
//        for(Source temp: sourceArrayList)
//        {
//            menu.add(menu.NONE, i, menu.NONE, temp.getCategory());
//            i++;
//        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        Toast.makeText(this, "Number: " + globalHM.get(item.getTitle().toString().toLowerCase()).size(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
