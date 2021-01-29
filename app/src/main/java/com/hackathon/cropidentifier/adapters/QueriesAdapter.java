package com.hackathon.cropidentifier.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hackathon.cropidentifier.R;
import com.hackathon.cropidentifier.pojo.Crop;

import java.util.List;

public class QueriesAdapter extends RecyclerView.Adapter<QueriesAdapter.viewHolder> {

    private Context context;
    viewHolder holder;
    List<Crop> allCrops;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    public QueriesAdapter() {
    }

    public QueriesAdapter(Context context, List<Crop> allCrops) {
        this.context = context;
        this.allCrops = allCrops;
    }

    @Override
    public int getItemCount() {
        return allCrops.size();
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_row, parent, false);
        holder = new viewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {

        Crop crop = allCrops.get(allCrops.size() - position - 1);

        Glide.with(context).load(crop.getImage()).into(holder.iv);

        holder.tv_id.setText(String.valueOf(crop.getId()));
        holder.tv_address.setText(crop.getAddress());
        holder.tv_coordinates.setText(crop.getLatitude() + ", " + crop.getLongitude());
        holder.tv_desc.setText(crop.getDesc());

        if (crop.getConfidence() > 65)
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://www.google.com/search?tbm=isch&q=" + crop.getCrop()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
        if (crop.getConfidence() > 65)
            holder.tv_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(crop.getDesc());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("geo:0,0?q=" + crop.getLatitude() + "," + crop.getLongitude() + "(Your field)");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);

            }
        });


    }

    static class viewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv_id, tv_address, tv_coordinates, tv_desc;
        View root;

        viewHolder(final View itemview) {
            super(itemview);

            root = itemview.findViewById(R.id.root);

            iv = itemview.findViewById(R.id.iv_image);
            tv_id = itemview.findViewById(R.id.tv_id);
            tv_address = itemview.findViewById(R.id.tv_address);
            tv_coordinates = itemview.findViewById(R.id.tv_coordinates);
            tv_desc = itemview.findViewById(R.id.tv_desc);
        }
    }

}
