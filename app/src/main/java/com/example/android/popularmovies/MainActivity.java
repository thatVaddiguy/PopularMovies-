package com.example.android.popularmovies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    final static String LOG_TAG = MainActivity.class.getSimpleName();

    GridView mainGrid;
    ArrayList<Movie> popularList;
    ArrayList<Movie> topVotedList;
    final static String POP_LIST = "popList";
    final static String TOP_VOTE_LIST = "topVoteList";

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(POP_LIST, popularList);
        outState.putSerializable(TOP_VOTE_LIST,topVotedList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        popularList = (ArrayList<Movie>)savedInstanceState.getSerializable(POP_LIST);
        topVotedList = (ArrayList<Movie>)savedInstanceState.getSerializable(TOP_VOTE_LIST);
        loadPreferenceList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (popularList == null || topVotedList==null){
            if (NetworkConnection.checkNetwork(MainActivity.this)){
                new MainSync().execute();
            }else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getString(R.string.network_alert_title));
                dialog.setMessage(getString(R.string.network_alert_message));
                dialog.setCancelable(false);
                dialog.show();
            }
        }else {
            loadPreferenceList();
        }
        mainGrid.setOnItemClickListener(MainActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mainGrid = (GridView)findViewById(R.id.topMovieGrid);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings){
            Intent optionIntent = new Intent(this,OrganizationPreferenceActivity.class);
            startActivity(optionIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Movie movie = (Movie) parent.getAdapter().getItem(position);

        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra("Movie",movie);
        startActivity(intent);
    }

    public class MainSync extends AsyncTask<Void,Void,Void>{

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            dialog.setTitle("Loading....");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String WebAddress;
            String WebAddressVote;
            String API_KEY = "18526e672ffd6e440a2de272efce8ab6";

            WebAddress = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+API_KEY;
            WebAddressVote = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key="+API_KEY;

            popularList = new ArrayList<>();
            topVotedList = new ArrayList<>();

            URLResult(WebAddress,popularList);
            URLResult(WebAddressVote,topVotedList);

            popularList.toString();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadPreferenceList();
            dialog.cancel();
        }
    }

    private void loadPreferenceList(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (sharedPreferences.getString("ORG_PREF_LIST","popular").equals("popular")){
            loadMovieAdapter(popularList);
        }else {
            loadMovieAdapter(topVotedList);
        }
    }

    private void loadMovieAdapter(ArrayList<Movie> _list){

        CustomGridAdapter adapter = new CustomGridAdapter(MainActivity.this,_list);
        mainGrid.setAdapter(adapter);
    }

    private void URLResult(String webAddress , ArrayList<Movie> _list){

        try {
            URL url = new URL(webAddress);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String results = IOUtils.toString(inputStream);
            jsonParser(results,_list);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void jsonParser(String s,ArrayList<Movie> movies){

        try {
            JSONObject mainObject = new JSONObject(s);

            JSONArray resultsArray = mainObject.getJSONArray("results");
            for (int i=0;i<resultsArray.length();i++){
                JSONObject indexObject = resultsArray.getJSONObject(i);
                Movie currentMovie = new Movie();
                currentMovie.setBackdrop_path(indexObject.getString("backdrop_path"));
                currentMovie.setId(indexObject.getInt("id"));
                currentMovie.setOriginal_title(indexObject.getString("original_title"));
                currentMovie.setOverview(indexObject.getString("overview"));
                currentMovie.setRelease_date(indexObject.getString("release_date"));
                currentMovie.setPoster_path(indexObject.getString("poster_path"));
                currentMovie.setPopularity(indexObject.getDouble("popularity"));
                currentMovie.setTitle(indexObject.getString("title"));
                currentMovie.setVote_average(indexObject.getInt("vote_average"));
                currentMovie.setVote_count(indexObject.getInt("vote_count"));

                movies.add(currentMovie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class CustomGridAdapter extends BaseAdapter{

        Context context;
        ArrayList<Movie> movieList;

        public CustomGridAdapter(Context context, ArrayList<Movie> movieList) {
            this.context = context;
            this.movieList = movieList;
        }

        @Override
        public int getCount() {
            return movieList.size();
        }

        @Override
        public Movie getItem(int i) {
            return movieList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.custom_item_row,parent,false);
            }
            Movie movieDb = getItem(position);

            ImageView imageViewcustom = (ImageView)convertView.findViewById(R.id.customImageView);
            Picasso.with(context).load("https://image.tmdb.org/t/p/w185"+movieDb.getPoster_path()).into(imageViewcustom);
            Log.v(LOG_TAG,movieDb.getPoster_path());
            return convertView;
        }
    }

}
