package ml.chiragkhandhar.newsgateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    Menu menu;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    ListView drawerList;
    Map<String, ArrayList<Source>> globalHM;
    ArrayList<Source> sourceList;

    private static final String TAG = "MainActivity";
    TextView nn_msg1, nn_msg2;
    Button tryAgain;

    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ARTICLE_LIST = "ARTICLE_LIST";
    static final String SOURCE = "SOURCE";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp_Components();

        Intent serviceIntent = new Intent(this, NewsService.class);
        startService(serviceIntent);


        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Source temp = sourceList.get(position);
                        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
                        intent.putExtra(SOURCE, temp);
                        sendBroadcast(intent);
                        Toast.makeText(MainActivity.this, temp.getName() + " Selected", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(drawerList);
                    }
                }
        );

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        if(networkChecker())
        {
            new SourceDownloader(this).execute();
            nn_msg1.setVisibility(View.GONE);
            nn_msg2.setVisibility(View.GONE);
            tryAgain.setVisibility(View.GONE);
        }
        else
        {
            nn_msg1.setVisibility(View.VISIBLE);
            nn_msg2.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
        }
    }

     public void setUp_Categories(View v)
    {
        if(networkChecker())
        {
            new SourceDownloader(this).execute();
            nn_msg1.setVisibility(View.GONE);
            nn_msg2.setVisibility(View.GONE);
            tryAgain.setVisibility(View.GONE);
        }
        else
        {
            nn_msg1.setVisibility(View.VISIBLE);
            nn_msg2.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
        }
    }

    private void setUp_Components()
    {
        nn_msg1 = findViewById(R.id.nonetworkIcon);
        nn_msg2 = findViewById(R.id.nonetworkMsg2);
        tryAgain = findViewById(R.id.tryAgain);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer_list);
        sourceList = new ArrayList<>();
    }

    public boolean networkChecker()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null)
            return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void setSources(Map<String, ArrayList<Source>> hashMap)
    {
       menu.clear();
       globalHM = hashMap;

       for(String category: hashMap.keySet())
           menu.add(showCamelCase(category));

       sourceList.addAll(Objects.requireNonNull(globalHM.get("all")));

       drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item,sourceList));

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
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
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
        if(drawerToggle.onOptionsItemSelected(item))
        {
            Log.d(TAG, "onOptionsItemSelected: bp: drawerToggle: " + item);
            return true;
        }

        setTitle(item.getTitle());

        sourceList.clear();
        ArrayList<Source> drawerTempList = globalHM.get(item.getTitle().toString().toLowerCase());

        Toast.makeText(this, drawerTempList.size() + " Sources Loaded ", Toast.LENGTH_SHORT).show();

        if(drawerTempList != null)
            sourceList.addAll(drawerTempList);

        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        Intent i = new Intent(this,NewsService.class);
        stopService(i);
        super.onDestroy();
    }
}
