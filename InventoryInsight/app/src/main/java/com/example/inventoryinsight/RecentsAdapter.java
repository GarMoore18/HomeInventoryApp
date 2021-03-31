package com.example.inventoryinsight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class RecentsAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Recents> arrayList;

    public RecentsAdapter(Context context, ArrayList<Recents> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false );

        ImageView item_image = convertView.findViewById(R.id.item_image);
        TextView item_name = convertView.findViewById(R.id.item_name);
        TextView item_barcode = convertView.findViewById(R.id.item_barcode);
        TextView item_location = convertView.findViewById(R.id.item_location);
        TextView quantity = convertView.findViewById(R.id.quantity);

        //Setting assets with correct information

        //TODO: FIX TO NULL INSTEAD OF EMPTY STRING
        if (!arrayList.get(position).getImage().equals("")) {
            byte[] decodedByte = Base64.decode(arrayList.get(position).getImage(), 0);
            Bitmap imgBitMap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            item_image.setImageBitmap(imgBitMap);
        } else {
            item_image.setImageDrawable(context.getDrawable(R.drawable.no_image_icon)); // TODO: GET THE REAL IMAGE
        }
        item_name.setText(arrayList.get(position).getName());
        item_barcode.setText(context.getString(R.string.upc_scroll) + arrayList.get(position).getBarcode());
        item_location.setText(arrayList.get(position).getLocation());
        quantity.setText(arrayList.get(position).getQuantity());

        return convertView;
    }
}
