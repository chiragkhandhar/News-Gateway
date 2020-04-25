package ml.chiragkhandhar.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NewsService extends Service
{
    private static final String TAG = "NewsService";
    private boolean isRunning = true;
    private ArrayList<Article> articleArrayList = new ArrayList<>();
    private ServiceReceiver serviceReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter1 = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver, filter1);

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                while (isRunning)
                {
                    while(articleArrayList.isEmpty())
                    {
                        try
                        {
                            Thread.sleep(250);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_NEWS_STORY);
                    intent.putExtra(MainActivity.ARTICLE_LIST, articleArrayList);
                    sendBroadcast(intent);
                    articleArrayList.clear();
                }
                Log.i(TAG, "NewsService was properly stopped");
            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        isRunning = false;
        unregisterReceiver(serviceReceiver);
        super.onDestroy();
    }

    public void setArticles(ArrayList<Article> articles)
    {
        articleArrayList.clear();
        Log.d(TAG, "setArticles: bp: Number of Articles: " + articles.size());
        articleArrayList.addAll(articles);
    }


    public void printLst(ArrayList<Article> tempList)          // for testing purpose
    {
        Log.d(TAG, "printLst: bp: Number of Articles: (Before)" + tempList.size());
        Log.d(TAG, "printLst: bp: =========================================================================================================");
        int i = 0;
        for(Article a : tempList)
        {
            Log.d(TAG, "printLst: bp: ---------------Article " + i++ + "---------------");
            Log.d(TAG, "printLst: bp: Title:" + a.getTitle());
            Log.d(TAG, "printLst: bp: Author:" + a.getAuthor());
            Log.d(TAG, "printLst: bp: Published on:" + a.getPublishedAt());
            Log.d(TAG, "printLst: bp: URL:" + a.getUrl());
            Log.d(TAG, "printLst: bp: Image URL:" + a.getUrlToImage());
            Log.d(TAG, "printLst: bp: Desc:" + a.getDescription());
            Log.d(TAG, "printLst: bp:---------------------------------------------");
        }
        Log.d(TAG, "printLst: bp: =========================================================================================================");
        Log.d(TAG, "printLst: bp: Number of Articles: (After)" + tempList.size());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    class ServiceReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())
            {
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    Source temp = null;
                    if (intent.hasExtra(MainActivity.SOURCE))
                    {
                        temp = (Source) intent.getSerializableExtra(MainActivity.SOURCE);
                    }
                    assert temp != null;
                    new ArticleDownloader(NewsService.this).execute(temp.getId());
                    break;

            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
}


