package ml.chiragkhandhar.newsgateway;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class ArticleFragment extends Fragment
{
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

            if(!temp.getTitle().equals(""))
            {
                TextView title = fragment_layout.findViewById(R.id.title);
                title.setText(temp.getTitle());
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
            }



            TextView pageNum = fragment_layout.findViewById(R.id.pageNo);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            return fragment_layout;
        }
        else
            return null;
    }
}
