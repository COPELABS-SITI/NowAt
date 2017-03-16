package pt.ulusofona.copelabs.now.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.copelabs.now.R;
import pt.ulusofona.copelabs.now.models.Message;

import java.util.ArrayList;

/**
 * Created by copelabs on 19/01/2017.
 */

public class MessageArrayAdapter extends ArrayAdapter<Message> {

    private ArrayList <Message> datos;

    public MessageArrayAdapter(Context context, ArrayList datos) {

        super(context, R.layout.message_inf, datos);
        this.datos=datos;
        // TODO Auto-generated constructor stub
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.message_inf, null);

        TextView lblSSID = (TextView)item.findViewById(R.id.lblmessage);
        lblSSID.setText(datos.get(position).getmMessage());

        TextView lblSubtitulo = (TextView)item.findViewById(R.id.lbluser);
        lblSubtitulo.setText(datos.get(position).getmUser());

        TextView lblInterest = (TextView)item.findViewById(R.id.lblInterest);
        lblInterest.setText(datos.get(position).getmInterest());

        TextView lblDate = (TextView)item.findViewById(R.id.lbldate);
        lblDate.setText(datos.get(position).getmDate()+"");

        ImageView imgView =(ImageView)item.findViewById(R.id.iv_avatar);
        imgView.setImageResource(R.drawable.ic_message_outline);
        imgView.setColorFilter(ContextCompat.getColor(imgView.getContext(),R.color.colorPrimary));

        return(item);
    }
}
