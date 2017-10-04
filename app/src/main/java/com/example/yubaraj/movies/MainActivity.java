package com.example.yubaraj.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.yubaraj.movies.model.MovieModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private URL url;
    private HttpURLConnection httpURLConnection = null;
    private BufferedReader reader = null;
    private ListView movieList;
    private MovieModel movieModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieList = (ListView) findViewById(R.id.list_movies);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");

        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

    }

    public class JsonTask extends AsyncTask<String, String, List<MovieModel>> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {

            List<MovieModel> movieList = new ArrayList<>();
            try {
                url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream stream = httpURLConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String jsonData = buffer.toString();
                JSONObject parentObject = new JSONObject(jsonData);
                JSONArray parentArray = parentObject.getJSONArray("movies");
                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject getData = parentArray.getJSONObject(i);
                    //Using gson it is easy one but the all attributes notations must be same as in json object
                    // we can just do it by using annotation @SerilizedName("name of attribute in json")
                    // Here in this scnerio the castList must be serilized
                    movieModel= gson.fromJson(getData.toString(),MovieModel.class);
                    //Here, once I used  movieModel= new gson.fromJson(...) the new word fried me little :)

                    //Below code is done to parse data without any library like Gson

//                    movieModel = new MovieModel();
//                    movieModel.setName(getData.getString("movie"));
//                    movieModel.setDirection(getData.getString("director"));
//                    movieModel.setDuration(getData.getString("duration"));
//                    movieModel.setRating((float) getData.getLong("rating"));
//                    movieModel.setImage(getData.getString("image"));
//                    movieModel.setYear(getData.getInt("year"));
//                    movieModel.setStory(getData.getString("story"));
//                    movieModel.setTagLine(getData.getString("tagline"));
//
//                    List<MovieModel.Cast> castList = new ArrayList<>();
//                    for (int j = 0; j < (getData.getJSONArray("cast")).length(); j++) {
//                        MovieModel.Cast cast = new MovieModel.Cast();
//                        JSONObject castObject = getData.getJSONArray("cast").getJSONObject(j);
//                        cast.setName(castObject.getString("name"));
//                        castList.add(cast);
//                    }
//                    movieModel.setCastList(castList);
                    movieList.add(movieModel);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    progressDialog.dismiss();
                    httpURLConnection.disconnect();
                }
            }
            if (reader != null) {
                try {
                    progressDialog.dismiss();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return movieList; // Must return movieList otherwise nullpointer exception appears to kiss us :)
        }

        @Override
        protected void onPostExecute(List<MovieModel> result) {
            progressDialog.dismiss();
            super.onPostExecute(result);
            MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.view_design, result);
            movieList.setAdapter(adapter);
        }

    }

    public class MovieAdapter extends ArrayAdapter {
        private int resource;
        private List<MovieModel> movieModelList;
        private LayoutInflater layoutInflater;

        public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;
            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                // We use holder to save CPU uses and Memory and increase increase in listView
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(resource, null);
                holder.movImage = (ImageView) convertView.findViewById(R.id.pv_mPicture);
                holder.movName = (TextView) convertView.findViewById(R.id.tv_mName);
                holder.movYear = (TextView) convertView.findViewById(R.id.tv_mYear);
                holder.movDirector = (TextView) convertView.findViewById(R.id.tv_mDirector);
                holder.movDuration = (TextView) convertView.findViewById(R.id.tv_mDuration);
                holder.movRating = (RatingBar) convertView.findViewById(R.id.rb_mRating);
                holder.movCasts = (TextView) convertView.findViewById(R.id.tv_mCasts);
                holder.movStory = (TextView) convertView.findViewById(R.id.tv_mStory);
                holder.movTagline = (TextView) convertView.findViewById(R.id.tv_mTagline);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            final ProgressBar progressBar;
            progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            //setting image to imageView using Universal Image Loader
            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), holder.movImage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);

                }
            });

            holder.movName.setText(movieModelList.get(position).getName());
            holder.movYear.setText("Year: " + movieModelList.get(position).getYear());
            holder.movDirector.setText("Director: " + movieModelList.get(position).getDirection());
            holder.movDuration.setText("Duration: " + movieModelList.get(position).getDuration());
            holder.movRating.setRating(movieModelList.get(position).getRating() / 2);

            StringBuffer buffer = new StringBuffer();
            for (MovieModel.Cast cast : movieModelList.get(position).getCastList()) {
                buffer.append(cast.getName() + ",");
            }
            holder.movCasts.setText(buffer);
            holder.movStory.setText(movieModelList.get(position).getStory());
            holder.movTagline.setText(movieModelList.get(position).getTagLine());


            return convertView;
        }

        // This class is used to eliminate the slow speed and more cpu and memory consuming by getView() method
        public class ViewHolder {
            private ImageView movImage;
            private TextView movName;
            private TextView movYear;
            private TextView movDuration;
            private TextView movDirector;
            private TextView movTagline;
            private TextView movCasts;
            private TextView movStory;
            private RatingBar movRating;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new JsonTask().execute("https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesData.txt");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}