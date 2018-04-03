package pt.ulusofona.copelabs.now.adapters;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.copelabs.now.R;

import java.util.ArrayList;

import pt.ulusofona.copelabs.now.interfaces.NowMainActivityInterface;
import pt.ulusofona.copelabs.now.models.Message;

/**
 * This class is part of Now@ application. It extends to ArrayAdapter. This class returns a view for
 * each object in a collection of data.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:06 PM
 */


public class MessageArrayAdapter extends ArrayAdapter<Message> {

    /**
     * Used for debug.
     */
    private static final String TAG = MessageArrayAdapter.class.getSimpleName();
    /**
     * List of messages to be displayed.
     */
    private ArrayList<Message> datos;

    /**
     * Context of the application.
     */
    private Context mContext;

    /**
     * Interface to communicate with the main activity.
     */
    private NowMainActivityInterface mNowMainActivityInterface;

    /**
     * ImageView used to display photos.
     */
    private ImageView imgBook;

    /**
     * Constructor of MessageArrayAapter
     *
     * @param context Context of the application
     * @param datos   ArrayList of data collection
     */
    public MessageArrayAdapter(Context context, ArrayList datos, NowMainActivityInterface nowMainActivityInterface) {

        super(context, R.layout.message_info_img, datos);
        this.datos = datos;
        mContext = context;
        mNowMainActivityInterface = nowMainActivityInterface;
        // TODO Auto-generated constructor stub
    }

    /**
     * This method is used to set the view of the cells.
     *
     * @param position    Position of the cell.
     * @param convertView View of the cell.
     * @param parent
     * @return
     */
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item;

        if (datos.get(position).getImage() == null) {

            item = inflater.inflate(R.layout.message_inf2, null);

            TextView lblMessage = (TextView) item.findViewById(R.id.lblmessage);
            lblMessage.setText(datos.get(position).getmMessage());

            TextView lblSubtitulo = (TextView) item.findViewById(R.id.lbluser);
            lblSubtitulo.setText(datos.get(position).getmUser());

            TextView lblInterest = (TextView) item.findViewById(R.id.lblInterest);
            lblInterest.setText("#" + datos.get(position).getmInterest());

            TextView lblDate = (TextView) item.findViewById(R.id.lbldate);
            lblDate.setText(datos.get(position).getmDate());
            imgBook = (ImageView) item.findViewById(R.id.imageView5);

            if (datos.get(position).getSave()) {
                //Log.d(TAG, "Save: " + datos.get(position).getmMessage() +"  "+ datos.get(position).getSave());
                imgBook.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_bookmark, null));
            } else {
                //Log.d(TAG, "Save: " + datos.get(position).getmMessage() +"  "+ datos.get(position).getSave());
                imgBook.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_bookmark_outline, null));
            }

            imgBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mNowMainActivityInterface.messageSelected(position);
                    //Log.d(TAG, "Position: "+ position);
                }
            });

        } else {

            item = inflater.inflate(R.layout.message_info_img, null);

            TextView lblSubtitulo = (TextView) item.findViewById(R.id.lbluser);
            lblSubtitulo.setText(datos.get(position).getmUser());

            TextView lblInterest = (TextView) item.findViewById(R.id.lblInterest);
            lblInterest.setText("#" + datos.get(position).getmInterest());

            TextView lblDate = (TextView) item.findViewById(R.id.lbldate);
            lblDate.setText(datos.get(position).getmDate());

            ImageView img = (ImageView) item.findViewById(R.id.imageView2);
            img.setImageBitmap(datos.get(position).getImage());

            //TODO Implement save button og message with image
            ImageView btn = (ImageView) item.findViewById(R.id.imageView4);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //mNowMainActivityInterface.messageSelected(position);
                    //Log.d(TAG, "Position: "+ position);
                    Toast toast = Toast.makeText(mContext, "This option is not available on this version", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }

        return (item);
    }

}
