package pt.ulusofona.copelabs.now.activities;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat; 
import android.os.Bundle; 
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import pt.ulusofona.copelabs.now.adapters.FileArrayAdapter;
import pt.ulusofona.copelabs.now.models.FileItem;
import com.example.copelabs.now.R;



public class FileChooserActivity extends ListActivity {

	private File mCurrentDir;

    private FileArrayAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentDir = new File("/sdcard/");
        fill(mCurrentDir);
    }

    private void fill(File f) {

    	File[]dirs = f.listFiles(); 
		 this.setTitle("Current Dir: "+f.getName());
		 List<FileItem>dir = new ArrayList<FileItem>();
		 List<FileItem>fls = new ArrayList<FileItem>();

			 for(File ff: dirs)
			 { 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(ff.isDirectory()){
					
					
					File[] fbuf = ff.listFiles(); 
					int buf = 0;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0; 
					String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items";
					
					//String formated = lastModDate.toString();
					dir.add(new FileItem(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"ic_folder"));
				}
				else
				{
					String extension = null;

						extension = ff.getName();
					List<String> items = Arrays.asList(extension.split("\\s*.\\s*"));
					String extensionRemoved = extension.split("\\.")[1];
					Log.d("Extension1", extensionRemoved);
					if( "png".equalsIgnoreCase(extensionRemoved) || "jpg".equalsIgnoreCase(extensionRemoved) || "jpeg".equalsIgnoreCase(extensionRemoved)){
						fls.add(new FileItem(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_file_image"));
					}else {
						fls.add(new FileItem(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_file_unknown"));
					}
					}
			 }

		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new FileItem("..","Parent Directory","",f.getParent(),"ic_arrow_left"));
		 mAdapter = new FileArrayAdapter(FileChooserActivity.this, R.layout.file_view,dir);
		 this.setListAdapter(mAdapter);
    }

    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		FileItem o = mAdapter.getItem(position);
		if(o.getImage().equalsIgnoreCase("ic_folder")||o.getImage().equalsIgnoreCase("ic_file_word")){
				mCurrentDir = new File(o.getmPath());
				fill(mCurrentDir);
		}
		else
		{
			onFileClick(o);
		}
	}
    private void onFileClick(FileItem o)
    {
    	Intent intent = new Intent();
        intent.putExtra("GetPath", mCurrentDir.toString());
        intent.putExtra("GetFileName",o.getmName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
