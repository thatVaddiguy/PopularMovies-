package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by BOX on 9/11/2016.
 */
public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        Movie movieIntent = (Movie)intent.getSerializableExtra("Movie");

        if (movieIntent!=null){

            TextView titleText = (TextView)findViewById(R.id.detail_title);
            titleText.setText(movieIntent.getTitle());

            ImageView detailImage = (ImageView)findViewById(R.id.detail_image);
            Picasso.with(this).load("https://image.tmdb.org/t/p/w185"+movieIntent.getPoster_path()).placeholder(R.drawable.notification_template_icon_bg).into(detailImage);

            TextView releaseDateText = (TextView)findViewById(R.id.detail_release_date);
            releaseDateText.setText(movieIntent.getRelease_date());

            TextView popularityText = (TextView)findViewById(R.id.detail_popularity);
            popularityText.setText("Popularity: "+movieIntent.getPopularity());

            TextView overviewText = (TextView)findViewById(R.id.detail_overview);
            overviewText.setText(movieIntent.getOverview());

            TextView voteAverageText = (TextView)findViewById(R.id.detail_vote_average);
            voteAverageText.setText("Vote Average: "+movieIntent.getVote_average());

            TextView voteCountText = (TextView)findViewById(R.id.detail_vote_count);
            voteCountText.setText("Vote Count: "+movieIntent.getVote_count());
        }else {
            Toast.makeText(this,"Error no data was found",Toast.LENGTH_LONG).show();
        }
    }
}
