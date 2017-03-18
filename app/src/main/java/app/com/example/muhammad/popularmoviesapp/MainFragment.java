package app.com.example.muhammad.popularmoviesapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

/**
 * Created by Muhammad on 9/20/2016.
 */
public class MainFragment extends Fragment implements AsyncResponse {

    private ImageAdapter imageAdapter;

    GetMoviesInfo GMI = new GetMoviesInfo(getActivity());

    GridView gridView;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //this to set delegate/listener back to this class
        GMI.delegate = this;

        if (isOnline())
        {
            //Execute the async task
            GMI.execute();
        }
        else
        {
            Toast.makeText(getActivity(), "Internet is needed for this app to run!", Toast.LENGTH_LONG).show();
        }


        return rootView;
    }

    private void updateMovies()
    {
        GetMoviesInfo GMI2 = new GetMoviesInfo(getActivity());

        GMI2.delegate = this;
        if (isOnline())
        {
            //Execute the async task
            GMI2.execute();
        }
        else
        {
            Toast.makeText(getActivity(), "Internet is needed for this app to run!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateMovies();
    }


    // Checks if the device is connected to the internet
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    // This is where the onPostExecute(result) will be accesible
    @Override
    public void processFinish(final ArrayList<MovieInfo> output)
    {
        // Set the GridView and call the custom ImageAdapter with AsyncTask's result.
        gridView = (GridView) rootView.findViewById(R.id.gridView_movies);
        imageAdapter = new ImageAdapter(getActivity(), output);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MovieInfo currentMovie = output.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("CurrentMovie", currentMovie);
                startActivity(intent);
            }
        });
    }

    /* This custom imageAdapter creates image PlaceHolder for
     *  all the images needed by checking the size of ArrayList<MovieInfo>
     *  object passed in. It uses Picasso to download the image, using the
     *  URL found the object, and loads it into the imageView.
     * */
    public class ImageAdapter extends BaseAdapter
    {
        private Context mContext;
        private ArrayList<MovieInfo> movie_list;

        public ImageAdapter(Context c, ArrayList<MovieInfo> list_movie)
        {
            mContext = c;
            movie_list = list_movie;
        }

        @Override
        public int getCount() {
            return movie_list.size();
        }

        @Override
        public Object getItem(int position) {
            return movie_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // Override these two methods (getViewTypeCount AND getItemViewType) so the images will show in the right order
        @Override
        public int getViewTypeCount()
        {
            return getCount();
        }

        @Override
        public int getItemViewType(int position)
        {
            return position;
        }

        // create a new ImageView for each item referenced by Adapter
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView;
            if (convertView == null)
            {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);

                Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185/" + movie_list.get(position).getMoviePoster()).into(imageView);
            } else {

                imageView = (ImageView) convertView;
            }
            ///e1mjopzAS2KNsvpbpahQ1a6SkSn.jpg
            return imageView;
        }

    }

    /* This AsyncTask connects to TheMovieDB server and gets the
    *  jsonString which contains all the needed movies information.
    *  This information is then stored into an object of MoviesInfo,
    *  which is added to an ArrayList. This AsyncTask does not take
    *  in a parameter, does not give a progress and returns an ArrayList
    *  containing MovieInfo objects.
    * */
    public class GetMoviesInfo extends AsyncTask<Void, Void, ArrayList<MovieInfo>>
    {
        private Context context;

        // An ArrayList of MovieInfo objects which will store all the information retrieved from the server
        private ArrayList<MovieInfo> AT_movie_list = new ArrayList<>();

        // An instance of MovieInfo to hold each movies information
        private MovieInfo movieInfo;

        // Instance of interface class to hold the result of AsyncTask from onPostExecute
        public AsyncResponse delegate = null;

        // Log tag name for this class
        public final String LOG_TAG = GetMoviesInfo.class.getSimpleName();

        //Constructor
        public GetMoviesInfo(Context context)
        {
            this.context = context;
        }


        /* This method takes in jsonString containg all the movie data and
        *  extracts useful information which is then stored in an instance of MovieInfo.
        *  It stores each MovieInfo object into an ArrayList and returns teh ArrayList
        * */
        private ArrayList getMoviesDataFromJson(String moviesJsonString)
                throws JSONException
        {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            String[] resultStrs = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++)
            {
                // This is where we will store the info of current movie from the JSON string
                movieInfo = new MovieInfo();

                // Get the JSON Object representing the current movie
                JSONObject currentMovie = moviesArray.getJSONObject(i);

                // Get the title, poster path, overview, vote, release date
                movieInfo.setMovieTitle(currentMovie.getString(TMDB_ORIGINAL_TITLE));
                movieInfo.setMoviePoster(currentMovie.getString(TMDB_POSTER));
                movieInfo.setMovieOverview(currentMovie.getString(TMDB_OVERVIEW));
                movieInfo.setMovieUserRating(currentMovie.getDouble(TMDB_VOTE_AVERAGE));
                movieInfo.setMovieReleaseDate(currentMovie.getString(TMDB_RELEASE_DATE));

                // Store each movie poster in the posterPath array
                AT_movie_list.add(movieInfo);

                //resultStrs[i] = "Title: " + movieInfo.getMovieTitle() + "\n PosterPath: " + movieInfo.getMoviePoster() + "\n Overview: " + movieInfo.getMovieOverview() + "\n Rating: " + movieInfo.getMovieUserRating() + "\n ReleaseDate: " + movieInfo.getMovieReleaseDate();
            }

            // Showing in logs if the extraction is successful
            for (MovieInfo result : AT_movie_list)
            {
                Log.v(LOG_TAG, "Returned movie title in getMoviesFromJSON: " + result.getMovieTitle());
            }

            return AT_movie_list;
        }

        BufferedReader reader = null;

        @Override
        protected ArrayList doInBackground(Void... params)
        {
            // Call GetMovie(), which connects to the server and gets the json string containin movie information
            return GetMovies();
        }

        /* This method connects to the server using a base URL and Api Key.
        *  This call is sent to the server and in return we get a jsonString
        *  containing all the movies data tha was requested. This jsonString
        *  is send as an string to getMoviesDataFromJson to extarct all the
        *  useful information from the jsonString.
        * */
        public ArrayList GetMovies()
        {
            // Get the value stored in shared preference on how to sort the movies
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = sharedPreferences.getString(getString(R.string.pref_movie_sort_key), getString(R.string.pref_sort_popular));



            HttpURLConnection urlConnection = null;
            final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String MOVIES_POPULAR = "popular?";
            final String MOVIE_TOP_RATED = "top_rated?";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri;

            if (sortBy.equals(getString(R.string.pref_sort_popular)))
            {
                builtUri = Uri.parse(MOVIES_BASE_URL+MOVIES_POPULAR).buildUpon().appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY).build();
            }
            else
            {
                builtUri = Uri.parse(MOVIES_BASE_URL+MOVIE_TOP_RATED).buildUpon().appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY).build();
            }

            String popularMoviesJasonString = null;

            try {
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read inputStream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                {
                    return null;
                }

                popularMoviesJasonString = buffer.toString();
                Log.v(LOG_TAG, "Popular Movies JSON String: " + popularMoviesJasonString);
            } catch (IOException e)
            {
                Log.e("MainActivity", "Error", e);

                return null;
            }
            finally
            {
                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    }
                    catch (final IOException e)
                    {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }

            try
            {
                return getMoviesDataFromJson(popularMoviesJasonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> result)
        {
            //This stores the returned result from doInBackground in the AsyncResponse interface instance
            delegate.processFinish(result);
        }

    }

}
