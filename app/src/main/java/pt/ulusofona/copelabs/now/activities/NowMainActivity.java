package pt.ulusofona.copelabs.now.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulusofona.copelabs.now.adapters.MessageArrayAdapter;
import com.example.copelabs.now.R;
import pt.ulusofona.copelabs.now.helpers.Utils;
import pt.ulusofona.copelabs.now.ndn.NDNChronoSync;
import pt.ulusofona.copelabs.now.models.Message;

import net.named_data.jndn.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Created by copelabs on 07/03/2017.
 */

public class NowMainActivity extends AppCompatActivity{

    private static final int REQUEST_PATH = 1;

    private TextView textName;

    private String TAG = NowMainActivity.class.getSimpleName();

    public Handler mHandler = new Handler();

    private ArrayList <Message> mMenssages = new ArrayList<>();

    private ArrayList <NDNChronoSync> mChronosyncs = new ArrayList<>();

    private MessageArrayAdapter mMenssageAdapter;

    private String mUserName;

    private String interestSelected = "";

    private Spinner mSpinner;

    private ArrayAdapter<String> adapter;

    private ArrayList<String> mInterestsSelected = new ArrayList<>();

    private Face face = new Face("localhost");

    private NDNChronoSync mChronoSync;

    private int mPosition;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMenssageAdapter = new MessageArrayAdapter(this, mMenssages);

        mMenssages.clear();

        ListView lstOpciones = (ListView)findViewById(R.id.listView);

        lstOpciones.setAdapter(mMenssageAdapter);

        textName = (TextView) findViewById(R.id.textView3);

        List<String> mHorizontalList = Arrays.asList(getResources().getStringArray(R.array.interests));

        final RecyclerView mHorizontalRecyclerView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);

        final HorizontalAdapter mHorizontalAdapterHolder = new HorizontalAdapter(mHorizontalList);

        mHorizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        mHorizontalRecyclerView.setAdapter(mHorizontalAdapterHolder);

        mUserName = Utils.generateRandomName();

        face = new Face("127.0.0.1");


        ImageButton mBtnSend = (ImageButton) findViewById(R.id.imageButton);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                if(interestSelected.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please select an Interest", Toast.LENGTH_SHORT).show();
                } else{
                    sendMessage(mPosition);
                }

            }
        });

        mSpinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mInterestsSelected);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View v, int position, long id) {

                        interestSelected = parent.getItemAtPosition(position).toString();
                        mPosition=position;

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                        //interestSelected=mSpinner.getSelectedItem().toString();
                    }

                });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.new_message:

            //add the function to perform here

            return(true);

        case R.id.find_file:

            Intent intentFileChooser = new Intent(this, FileChooserActivity.class);
            startActivityForResult(intentFileChooser,REQUEST_PATH);
            return (true);

        case R.id.user_profile:

            //add the function to perform here
            //mUserName();

            return(true);

        case R.id.more:

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pt.ulusofona.copelabs.ndn");
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
            //add the function to perform here
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }


        public void newchrono(){
              mChronoSync = new NDNChronoSync() {

                @Override
                public Handler getHandler() {
                    return mHandler;
                }

                @Override
                public ProgressDialog getProgressDialog() {
                    return null;
                }

                @Override
                public void handleDataReceived(String data) {
                    callback2(data);
                }
            };
            mChronosyncs.add(mChronoSync);

        }


    public void userName(){

        final AlertDialog.Builder textBox = new AlertDialog.Builder(this);
        textBox.setTitle("Now@ User Name");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(mChronoSync.UUID);
        textBox.setView(input);
        textBox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Store toast_message in JSON object
                mChronoSync.UUID = input.getText().toString();
                Toast.makeText(getApplicationContext(), "Name was changed", Toast.LENGTH_SHORT).show();
            }
        });
        textBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.cancel();
            }
        });
        textBox.show();


    }

    public void sendMessage(int position){
        final JSONObject jObject = new JSONObject();  // JSON object to store toast_message
        final EditText input = (EditText)findViewById(R.id.editText);

                // Store toast_message in JSON object
                try {
                    jObject.put("data", input.getText().toString());
                    jObject.put("type", "text");
                    jObject.put("user", mUserName);
                    jObject.put("interest", interestSelected);
                    jObject.put("date",Utils.getDate());
                    sendData(jObject.toString(), position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mMenssages.add(new Message(mUserName,input.getText().toString(),interestSelected,Utils.getDate()));

                mMenssageAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                input.setText("");
    }




    public void onCreateRoute(String interest){

        mChronoSync.mFace=face;
        mChronoSync.UUID = UUID.randomUUID().toString();
        mChronoSync.applicationNamePrefix="/ndn/multicast/"+interest+"/"+ mChronoSync.UUID;
        mChronoSync.applicationBroadcastPrefix="/ndn/multicast/now/"+interest;
        textName.setText("User : " + mUserName + "\n" + "UUID: "+ mChronoSync.UUID + "\n" + "Prefix: "+ mChronoSync.applicationNamePrefix );
        mChronoSync.initialize();

    }

    public void ndnStop(){

        mChronoSync.stop();
    }


    public void sendData(String jsonData, int position) {

        mChronosyncs.get(position).dataHistory.add(jsonData);  // Add action to history
        mChronosyncs.get(position).increaseSequenceNos();
        Log.d(TAG, "Stroke generated: " + jsonData);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        if (requestCode == REQUEST_PATH){
            if (resultCode == RESULT_OK) {
                String curFileName = data.getStringExtra("GetFileName");

            }
        }
    }


    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<String> horizontalList;

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txtView;

            MyViewHolder(View view) {
                super(view);
                txtView = (TextView) view.findViewById(R.id.txtView);

            }
        }


        HorizontalAdapter(List<String> horizontalList) {
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.horizontal_item_view, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.txtView.setText(horizontalList.get(position));


            holder.txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(holder.txtView.getCurrentTextColor() == getResources().getColor(R.color.colorPrimary)) {

                        holder.itemView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.backgroud_holder_disabled, null));
                        holder.txtView.setTextColor(getResources().getColor(R.color.white));

                        // Keep track of the ChronoSync + Interest.
                        newchrono();

                        onCreateRoute(horizontalList.get(position).toLowerCase());
                        interestSelected = horizontalList.get(position);
                        mInterestsSelected.add(horizontalList.get(position));
                        mSpinner.setAdapter(adapter);
                        mSpinner.setSelection(mPosition);

                    }else{
                        mInterestsSelected.remove(horizontalList.get(position));
                        holder.itemView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.backgorud_holder_item_selected, null));
                        holder.txtView.setTextColor(getResources().getColor(R.color.colorPrimary));
                        //ndnStop();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }


    /**
     * Helper Function to parse and draw the action mentioned in the passed JSON string
     *
     * @param string the json representation of the action to be performed
     */
    public void callback2(String string) {
        parseJSON(string, true);
    }

    /**
     * Function to parse and draw the action mentioned in the passed JSON string
     *
     * @param string       the json representation of the action to be performed
     * @param addToHistory if true, add the action to history
     */
    public void parseJSON(String string, boolean addToHistory) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            try {

                String type = jsonObject.get("type").toString();

                switch (type) {

                    case "text": {
                        // Create a toast of the toast_message text
                        String message = jsonObject.getString("data");

                        String username = jsonObject.getString("user");

                        String interest = jsonObject.getString("interest");

                        String date = jsonObject.getString("date");
                        mMenssages.add(new Message(username,message,interest,date));

                        mMenssageAdapter.notifyDataSetChanged();

                        break;
                    }

                    default:
                        throw new JSONException("Unrecognized string: " + string);
                }

                   /* if (addToHistory) {
                        history.add(string);
                    }*/


            } catch (JSONException e) {
                Log.d(TAG, "JSON string error: " + string);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
