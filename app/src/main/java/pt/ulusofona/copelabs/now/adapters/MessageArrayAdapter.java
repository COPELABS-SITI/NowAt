package pt.ulusofona.copelabs.now.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.copelabs.now.R;
import pt.ulusofona.copelabs.now.models.Message;

import java.util.ArrayList;
/**
 * This class is part of Now@ application. It extends to ArrayAdapter. This class returns a view for
 * each object in a collection of data.
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:06 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */


public class MessageArrayAdapter extends ArrayAdapter<Message> {

    private ArrayList <Message> datos;

    /**
     * Constructor of MessageArrayAapter
     * @param context Context of the application
     * @param datos ArrayList of data collection
     */
    public MessageArrayAdapter(Context context, ArrayList datos) {

        super(context, R.layout.message_inf, datos);
        this.datos=datos;
        // TODO Auto-generated constructor stub
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.message_inf, null);

        TextView lblMessage = (TextView)item.findViewById(R.id.lblmessage);
        lblMessage.setText(datos.get(position).getmMessage());

        TextView lblSubtitulo = (TextView)item.findViewById(R.id.lbluser);
        lblSubtitulo.setText(datos.get(position).getmUser());

        TextView lblInterest = (TextView)item.findViewById(R.id.lblInterest);
        lblInterest.setText("#"+datos.get(position).getmInterest());

        TextView lblDate = (TextView)item.findViewById(R.id.lbldate);
        lblDate.setText(datos.get(position).getmDate()+"");

        return(item);
    }
}
