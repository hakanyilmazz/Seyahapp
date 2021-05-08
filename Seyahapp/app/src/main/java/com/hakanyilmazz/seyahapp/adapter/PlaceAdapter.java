package com.hakanyilmazz.seyahapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hakanyilmazz.seyahapp.R;
import com.hakanyilmazz.seyahapp.model.Place;

import java.util.ArrayList;

public class PlaceAdapter extends ArrayAdapter<Place> {

    private ArrayList<Place> placeList;
    private Context context;

    public PlaceAdapter(@NonNull Context context, ArrayList<Place> placeList) {
        super(context, R.layout.place_list_row, placeList);

        this.placeList = placeList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View customView = layoutInflater.inflate(R.layout.place_list_row, parent, false);

        TextView nameTextView = customView.findViewById(R.id.nameTextView);

        nameTextView.setText(placeList.get(position).getName());

        return customView;
    }

}
