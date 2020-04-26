package ml.chiragkhandhar.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Locale;

public class ArticleFragment extends Fragment
{
    private static final String TAG = "ArticleFragment";
    public ArticleFragment() {
        // Required empty public constructor
    }


    static ArticleFragment newInstance(Article article, int index, int max)
    {
        ArticleFragment f = new ArticleFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("ARTICLE_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View fragment_layout = inflater.inflate(R.layout.article_fragment, container, false);

        Bundle args = getArguments();
        if (args != null)
        {
            final Article temp = (Article) args.getSerializable("ARTICLE_DATA");

            if (temp == null)
            {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            if(temp.getUrl() != null)
                shareStory(fragment_layout,temp.getUrl());

            if(!temp.getTitle().equals(""))
            {
                TextView title = fragment_layout.findViewById(R.id.title);
                title.setText(temp.getTitle());
                title.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(temp.getUrl() != null)
                            openStory(temp.getUrl());
                    }
                });
            }


            if(!temp.getAuthor().equals(""))
            {
                TextView author = fragment_layout.findViewById(R.id.author);
                author.setText(temp.getAuthor());
            }

            if(!temp.getPublishedAt().equals(""))
            {
                TextView publishedAt = fragment_layout.findViewById(R.id.publishedAt);
                publishedAt.setText(temp.getPublishedAt());
            }

            if(!temp.getDescription().equals(""))
            {
                TextView desc = fragment_layout.findViewById(R.id.description);
                desc.setText(temp.getDescription());

                desc.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(temp.getUrl() != null)
                            openStory(temp.getUrl());
                    }
                });
            }

            if(temp.getUrlToImage() == null || temp.getUrlToImage().equals("null"))
            {
                ImageView picture = fragment_layout.findViewById(R.id.image);
                picture.setBackgroundResource(R.drawable.placeholder);

                picture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(temp.getUrl() != null)
                            openStory(temp.getUrl());
                    }
                });

            }
            else
            {


                ImageView picture = fragment_layout.findViewById(R.id.image);

                Glide.with(this)
                        .load(temp.getUrlToImage())
                        .placeholder(R.drawable.loading)
                        .fitCenter()
                        .error(R.drawable.error)
                        .into(picture);
            }



            TextView pageNum = fragment_layout.findViewById(R.id.pageNo);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            return fragment_layout;
        }
        else
            return null;
    }

    void shareStory(View v, final String URL)
    {
        ImageButton btn = v.findViewById(R.id.share);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.share_header) + URL;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    public void printArticle(Article a)          // for testing purpose
    {
        Log.d(TAG, "printLst: bp: =========================================================================================================");
        Log.d(TAG, "printLst: bp: Title:" + a.getTitle());
        Log.d(TAG, "printLst: bp: Author:" + a.getAuthor());
        Log.d(TAG, "printLst: bp: Published on:" + a.getPublishedAt());
        Log.d(TAG, "printLst: bp: URL:" + a.getUrl());
        Log.d(TAG, "printLst: bp: Image URL:" + a.getUrlToImage());
        Log.d(TAG, "printLst: bp: Desc:" + a.getDescription());
        Log.d(TAG, "printLst: bp:---------------------------------------------");

        Log.d(TAG, "printLst: bp: =========================================================================================================");
    }

    public void openStory(String URL)
    {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(i);
    }
}
