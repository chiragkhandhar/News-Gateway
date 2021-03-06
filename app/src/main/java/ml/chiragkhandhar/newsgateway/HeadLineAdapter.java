package ml.chiragkhandhar.newsgateway;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class HeadLineAdapter extends RecyclerView.Adapter<HeadLinesViewHolder>
{
    private static final String TAG = "HeadLineAdapter";
    private List<Article> headlinesList;
    private MainActivity mainActivity;

    HeadLineAdapter(List<Article> headlinesList, MainActivity mainActivity)
    {
        this.headlinesList = headlinesList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public HeadLinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.headline_vh,parent,false);
        itemView.setOnClickListener(mainActivity);
        return new HeadLinesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadLinesViewHolder holder, int position)
    {
        final Article temp = headlinesList.get(position);
        holder.title.setText(temp.getTitle());
        holder.publishedAt.setText(temp.getPublishedAt());
        ImageView picture = holder.image;
        ImageButton share = holder.share;

        if(isNull(temp.getUrlToImage()))
            picture.setBackgroundResource(R.drawable.placeholder);
        else
        {
            Glide.with(mainActivity)
                    .load(temp.getUrlToImage())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(picture);
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!isNull(temp.getUrl()))
                {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Checkout this new story from News Gateway:\n" + temp.getUrl();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New Gateway");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    mainActivity.startActivity(sharingIntent);
                }

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return headlinesList.size();
    }

    private boolean isNull(String data)
    {
        return data == null || data.equals("null");
    }


}
