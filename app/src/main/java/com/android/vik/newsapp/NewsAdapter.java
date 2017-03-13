package com.android.vik.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

        final News currentArticle = getItem(position);

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.article_thumbnail);

        Picasso.with(getContext()).load(currentArticle.getThumbNailURL()).resize(250, 250).into(imageView);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.article_title);
        titleTextView.setText(currentArticle.getTitle());

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse(currentArticle.getWebUrl()));
                getContext().startActivity(sendIntent);
            }
        });
        return listItemView;
    }

}
