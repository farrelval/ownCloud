package com.example.owncloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CloudListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Cloud> cloudList;

    public CloudListAdapter(Context context, int layout, ArrayList<Cloud> foodsList) {
        this.context = context;
        this.layout = layout;
        this.cloudList = foodsList;
    }

    @Override
    public int getCount() {
        return cloudList.size();
    }

    @Override
    public Object getItem(int position) {
        return cloudList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtFormat;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.txtFormat = (TextView) row.findViewById(R.id.txtFormat);
            holder.imageView = (ImageView) row.findViewById(R.id.images_view);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Cloud cloud = cloudList.get(position);

        holder.txtName.setText(cloud.getName());
        holder.txtFormat.setText(cloud.getFormat());

        byte[] foodImage = cloud.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}