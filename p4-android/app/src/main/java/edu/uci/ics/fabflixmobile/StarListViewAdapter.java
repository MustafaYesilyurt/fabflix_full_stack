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

public class StarListViewAdapter extends ArrayAdapter<String> {
    private ArrayList<String> stars;

    public StarListViewAdapter(ArrayList<String> stars, Context context) {
        super(context, R.layout.stars_row, stars);
        this.stars = stars;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.stars_row, parent, false);

        String star = stars.get(position);

        TextView starView = view.findViewById(R.id.star_name);

        starView.setText(star);

        return view;
    }
}
