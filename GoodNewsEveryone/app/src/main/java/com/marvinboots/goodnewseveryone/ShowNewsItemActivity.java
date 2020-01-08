package com.marvinboots.goodnewseveryone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.marvinboots.goodnewseveryone.db.NewsItemDao;
import com.marvinboots.goodnewseveryone.db.NewsItemsDB;
import com.marvinboots.goodnewseveryone.model.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class ShowNewsItemActivity extends AppCompatActivity {

    private NewsItem temp;

    public static final String NOTE_EXTRA_Key = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_news_item);

        TextView newsItemTitle = findViewById(R.id.news_item_title);
        TextView newsItemDescription = findViewById(R.id.news_item_description);
        TextView newsItemDate = findViewById(R.id.news_item_date);
        TextView newItemAuthor = findViewById(R.id.news_item_author);
        ImageView newsItemPreviewImage = findViewById(R.id.preview_image);
        TextView newItemGuid = findViewById(R.id.news_item_guid);

        NewsItemDao dao = NewsItemsDB.getInstance(this).newsItemDao();
        int id = Objects.requireNonNull(getIntent().getExtras()).getInt(NOTE_EXTRA_Key, 0);
        temp = dao.getNewsItemById(id);
        newsItemTitle.setText(temp.getNewsItemTitle());
        newsItemDescription.setText(temp.getNewsItemDescription());
        newsItemDate.setText(temp.getNewsItemPubDate());
        newItemGuid.setText("Link");
        newItemGuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp.getNewsOrigLink()));
                startActivity(browserIntent);
            }
        });
        Picasso.get()//with(mContext)
                .load(temp.getNewsItemImage())
                .placeholder(R.mipmap.whatastory)
                .centerInside()
                .error(R.mipmap.placeholde)
                .into(newsItemPreviewImage);


    }

}
