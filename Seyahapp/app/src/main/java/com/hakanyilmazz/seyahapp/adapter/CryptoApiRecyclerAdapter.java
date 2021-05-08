package com.hakanyilmazz.seyahapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hakanyilmazz.seyahapp.R;
import com.hakanyilmazz.seyahapp.model.CryptoModel;

import java.util.ArrayList;

public class CryptoApiRecyclerAdapter extends RecyclerView.Adapter<CryptoApiRecyclerAdapter.RowHolder> {

    private final ArrayList<CryptoModel> cryptoList;

    public CryptoApiRecyclerAdapter(ArrayList<CryptoModel> cryptoList) {
        this.cryptoList = cryptoList;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.crypto_api_recycler, parent, false);

        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        holder.bind(cryptoList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    public class RowHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textPrice;

        public RowHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(CryptoModel cryptoModel, Integer position) {
            textName = itemView.findViewById(R.id.nameText);
            textPrice = itemView.findViewById(R.id.priceText);

            textName.setText(cryptoModel.currency);
            textPrice.setText(cryptoModel.price + " $");
        }

    }

}
