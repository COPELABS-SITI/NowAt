package pt.ulusofona.copelabs.now.adapters;

import java.util.List; 
 
import android.content.Context; 
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; 
import android.widget.ArrayAdapter;
import android.widget.ImageView; 
import android.widget.TextView;

import pt.ulusofona.copelabs.now.models.FileItem;
import com.example.copelabs.now.R;


public class FileArrayAdapter extends ArrayAdapter<FileItem>{

	private Context c;
	private int id;
	private List<FileItem> fileItems;
	
	public FileArrayAdapter(Context context, int textViewResourceId, List<FileItem> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		fileItems = objects;
	}
	public FileItem getItem(int i)
	 {
		 return fileItems.get(i);
	 }
	 @Override
       public View getView(int position, View convertView, ViewGroup parent) {
               View v = convertView;
               if (v == null) {
                   LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   v = vi.inflate(id, null);
               }
               
               /* create a new view of my layout and inflate it in the row */
       		//convertView = ( RelativeLayout ) inflater.inflate( resource, null );
       		
               final FileItem o = fileItems.get(position);
               if (o != null) {
                       TextView t1 = (TextView) v.findViewById(R.id.TextView01);
                       TextView t2 = (TextView) v.findViewById(R.id.TextView02);
                       TextView t3 = (TextView) v.findViewById(R.id.TextViewDate);
                       /* Take the ImageView from layout and set the city's image */
	               		ImageView imageCity = (ImageView) v.findViewById(R.id.fd_Icon1);
	               		String uri = "drawable/" + o.getImage();
	               	    int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
	               	    Drawable image = c.getResources().getDrawable(imageResource);
	               	    imageCity.setImageDrawable(image);
                       
                       if(t1!=null)
                       		t1.setText(o.getmName());
                       if(t2!=null)
                          	t2.setText(o.getmData());
                       if(t3!=null)
                          	t3.setText(o.getmDate());
                       
               }
               return v;
       }



}
