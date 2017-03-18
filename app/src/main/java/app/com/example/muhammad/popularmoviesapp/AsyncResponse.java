package app.com.example.muhammad.popularmoviesapp;

import java.util.ArrayList;

/**
 * Created by Muhammad on 9/18/2016.
 */
public interface AsyncResponse {

    //One method to be called in AsyncTask to store result and called again in Main to access result
    void processFinish(ArrayList<MovieInfo> output);
}
