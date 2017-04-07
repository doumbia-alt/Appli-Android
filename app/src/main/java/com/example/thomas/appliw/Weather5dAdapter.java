package com.example.thomas.appliw;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Weather5dAdapter extends ArrayAdapter<Weather5d> {

    public Weather5dAdapter(Context context, List<Weather5d> rows) {
        super(context, 0, rows);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            //appel du layout row_first pour avoir la disposition voulue
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_first,parent, false);
        }

        RowViewHolder viewHolder = (RowViewHolder) convertView.getTag();
        //declaration
        if(viewHolder == null){
            viewHolder = new RowViewHolder();
            viewHolder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Weather5d> tweets
        //affichage
        //TODO recuperation de la position (id) pour chaque iteration du tableau LIST (json)
        Weather5d row = getItem(position);
        viewHolder.pseudo.setText(row.getPseudo());
        viewHolder.text.setText(row.getText());
        viewHolder.avatar.setImageDrawable(new ColorDrawable(row.getColor()));

        return convertView;
    }

    private class RowViewHolder{
        public TextView pseudo;
        public TextView text;
        public ImageView avatar;

    }
}