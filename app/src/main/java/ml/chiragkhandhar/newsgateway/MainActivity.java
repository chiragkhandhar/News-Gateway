package ml.chiragkhandhar.newsgateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity
{
    NewsReceiver newsReceiver;
    Menu menu;
    ImageButton home;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    ListView drawerList;
    Map<String, ArrayList<Source>> globalHM;
    ArrayList<Source> sourceList;
    ArrayList<Article> articleArrayList;

    List<Fragment> fragments;
    MyPageAdapter pageAdapter;
    ViewPager pager;

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
                        setTitle(temp.getName());
                        pager.setBackgroundResource(R.color.light_grey);
                        Snackbar.make(view,temp.getName() + " Selected", Snackbar.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(drawerList);
                    }
                }
        );

        home.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setTitle(getString(R.string.app_name));
                fragments.clear();
                pageAdapter.notifyDataSetChanged();
                int orientation = getResources().getConfiguration().orientation;


            }
        });

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
            home.setVisibility(View.VISIBLE);
        }
        else
        {
            nn_msg1.setVisibility(View.VISIBLE);
            nn_msg2.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
            home.setVisibility(View.GONE);
        }

        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter1);
    }

     public void setUp_Categories(View v)
    {
        if(networkChecker())
        {
            new SourceDownloader(this).execute();
            nn_msg1.setVisibility(View.GONE);
            nn_msg2.setVisibility(View.GONE);
            tryAgain.setVisibility(View.GONE);
            home.setVisibility(View.VISIBLE);
        }
        else
        {
            nn_msg1.setVisibility(View.VISIBLE);
            nn_msg2.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
            home.setVisibility(View.GONE);
        }
    }

    private void setUp_Components()
    {
        nn_msg1 = findViewById(R.id.nonetworkIcon);
        nn_msg2 = findViewById(R.id.nonetworkMsg2);
        tryAgain = findViewById(R.id.tryAgain);
        home = findViewById(R.id.home);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer_list);
        sourceList = new ArrayList<>();

        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        articleArrayList = new ArrayList<>();
        newsReceiver = new NewsReceiver();
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
       try
       {
           menu.clear();
           globalHM = hashMap;
           int i = 0;

           for(String category: hashMap.keySet())
           {
               SpannableString x = new SpannableString(showCamelCase(category));
               x.setSpan(new ForegroundColorSpan(getColor(selectColor(category))), i, category.length(), 0);
               menu.add(x);
           }


           sourceList.addAll(Objects.requireNonNull(globalHM.get("all")));
           drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item,sourceList));

           if (getSupportActionBar() != null)
           {
               getSupportActionBar().setDisplayHomeAsUpEnabled(true);
               getSupportActionBar().setHomeButtonEnabled(true);
           }
       }
       catch (Exception e)
       {
           Log.d(TAG, "bp: setSources: Menu object did not inflate");
           new SourceDownloader(this).execute();
       }
    }

    public static int selectColor(String category)
    {
        switch (category)
        {
            case "business":
                return R.color.blue;
            case "entertainment":
                return R.color.yellow;
            case "general":
                return R.color.colorPrimary;
            case "health":
                return R.color.red;
            case "science":
                return R.color.purple;
            case "sports":
                return R.color.green;
            case "technology":
                return R.color.grey;
            default:
                return R.color.black;
        }
    }

    public static String showCamelCase(String str)
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
            return true;

        setTitle(item.getTitle().toString());

        sourceList.clear();
        ArrayList<Source> drawerTempList = globalHM.get(item.getTitle().toString().toLowerCase());

        if(drawerTempList != null)
        {
            sourceList.addAll(drawerTempList);
        }

        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();
        Toast.makeText(this, drawerTempList.size() + " Sources Loaded ", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putSerializable("articleArrayList",articleArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(newsReceiver);
        Intent i = new Intent(this,NewsService.class);
        stopService(i);
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        articleArrayList = (ArrayList<Article>) savedInstanceState.getSerializable("articleArrayList");
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onResume()
    {
        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter1);
        super.onResume();
    }




    public void setFragments()
    {
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for (int i = 0; i < articleArrayList.size(); i++)
        {
            fragments.add(ArticleFragment.newInstance(articleArrayList.get(i), i+1, articleArrayList.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class MyPageAdapter extends FragmentPagerAdapter
    {
        private long baseId = 0;

        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) 
        {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) 
        {
            return fragments.get(position);
        }

        @Override
        public int getCount() 
        {
            return fragments.size();
        }

        @Override
        public long getItemId(int position)
        {
            return baseId + position;
        }

        void notifyChangeInPosition(int n)
        {
            baseId += getCount() + n;
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    class NewsReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            articleArrayList.clear();
            if (intent.hasExtra(ARTICLE_LIST))
            {
                articleArrayList = (ArrayList<Article>) intent.getSerializableExtra(ARTICLE_LIST);
                setFragments();
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
}

