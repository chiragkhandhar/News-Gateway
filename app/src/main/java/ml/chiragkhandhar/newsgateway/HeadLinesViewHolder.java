package ml.chiragkhandhar.newsgateway;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HeadLinesViewHolder extends RecyclerView.ViewHolder
{
    TextView title, publishedAt;
    ImageView image;
    HeadLinesViewHolder(@NonNull View itemView)
    {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        publishedAt = itemView.findViewById(R.id.publishedAt);
        image = itemView.findViewById(R.id.image);
    }
}
