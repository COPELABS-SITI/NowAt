package pt.ulusofona.copelabs.now.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.copelabs.now.R;

/**
 * Created by copelabs on 08/03/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView txtView;

    MyViewHolder(View view) {
        super(view);
        txtView = (TextView) view.findViewById(R.id.txtView);

    }
}
