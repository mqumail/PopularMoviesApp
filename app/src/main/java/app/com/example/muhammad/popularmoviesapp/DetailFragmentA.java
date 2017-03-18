package app.com.example.muhammad.popularmoviesapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Muhammad on 9/21/2016.
 */
public class DetailFragmentA extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_detail_a, container, false);

        Intent intent = getActivity().getIntent();
        MovieInfo movieInfo = (MovieInfo)intent.getSerializableExtra("CurrentMovie");

        // Set the title
        TextView tv = (TextView) rootView.findViewById(R.id.textViewTitle);
        tv.setText(movieInfo.getMovieTitle());

        // Set the Thumbnail
        ImageView iv = (ImageView) rootView.findViewById(R.id.imageViewMovieThumbnail);
        Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185/" + movieInfo.getMoviePoster()).into(iv);

        // Set the Release Date
        TextView tv2 = (TextView) rootView.findViewById(R.id.textViewMovieReleaseDate);
        // Extract just the year from the release date, first four digits are year
        String movieYear = movieInfo.getMovieReleaseDate().substring(0, Math.min(movieInfo.getMovieReleaseDate().length(), 4));
        tv2.setText(movieYear);

        // Set the User Rating
        TextView tv3 = (TextView) rootView.findViewById(R.id.textViewUserRating);
        tv3.setText(movieInfo.getMovieUserRating().toString()+"/10");

        // Set the Overview
        TextView tv4 = (TextView) rootView.findViewById(R.id.textViewOverview);
        tv4.setText(movieInfo.getMovieOverview());

        return rootView;
    }

}
