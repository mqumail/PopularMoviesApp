package app.com.example.muhammad.popularmoviesapp;

import java.io.Serializable;

/**
 * Created by Muhammad on 9/2/2016.
 */
public class MovieInfo implements Serializable {

    String movieTitle;
    String moviePoster;
    String movieOverview;
    double movieUserRating;
    String movieReleaseDate;

    public String getMovieTitle()
    {
        return movieTitle;
    }
    public String getMoviePoster()
    {
        return moviePoster;
    }
    public String getMovieOverview()
    {
        return movieOverview;
    }
    public Double getMovieUserRating()
    {
        return movieUserRating;
    }
    public String getMovieReleaseDate()
    {
        return movieReleaseDate;
    }

    public void setMovieTitle(String title)
    {
        movieTitle = title;
    }
    public void setMoviePoster(String poster)
    {
        moviePoster = poster;
    }
    public void setMovieOverview(String overview)
    {
        movieOverview = overview;
    }
    public void setMovieUserRating(Double rating)
    {
        movieUserRating = rating;
    }
    public void setMovieReleaseDate(String releaseDate)
    {
        movieReleaseDate = releaseDate;
    }
}
