package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class GenreListViewAdapter extends ArrayAdapter<String> {
    private ArrayList<String> genres;

    public GenreListViewAdapter(ArrayList<String> genres, Context context) {
        super(context, R.layout.genres_row, genres);
        this.genres = genres;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.genres_row, parent, false);

        String genre = genres.get(position);

        TextView genreView = view.findViewById(R.id.genre_name);

        genreView.setText(genre);

        return view;
    }
}