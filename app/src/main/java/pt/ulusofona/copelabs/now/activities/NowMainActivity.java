package pt.ulusofona.copelabs.now.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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

import pt.ulusofona.copelabs.now.adapters.HorizontalAdapterHolder;
import pt.ulusofona.copelabs.now.adapters.MessageArrayAdapter;
import com.example.copelabs.now.R;
import pt.ulusofona.copelabs.now.helpers.Utils;
import pt.ulusofona.copelabs.now.models.User;
import pt.ulusofona.copelabs.now.ndn.ChronoSync;
import pt.ulusofona.copelabs.now.models.Message;
import pt.ulusofona.copelabs.now.ndn.NDNParameters;

import net.named_data.jndn.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;


/**
 * Created by copelabs on 07/03/2017.
 */

public class NowMainActivity extends AppCompatActivity implements Observer, NowMainActivityInterface{

    private static final int REQUEST_PATH = 1;

    private TextView mLblNamePrefix;

    private String TAG = NowMainActivity.class.getSimpleName();

    private ArrayList <Message> mMenssages = new ArrayList<>();

    private ArrayList <ChronoSync> mChronosyncs = new ArrayList<>();

    private MessageArrayAdapter mMenssageAdapter;

    private String interestSelected = null;

    private Spinner mSpinner;

    private ArrayAdapter<String> adapter;

    private ArrayList<String> mInterestsSelected = new ArrayList<>();

    private Face face = new Face("localhost");

    private int mPosition;

    private User mUser;

    private ChronoSync ChronoSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMenssageAdapter = new MessageArrayAdapter(this, mMenssages);

        mMenssages.clear();

        ListView lstOpciones = (ListView)findViewById(R.id.listView);

        lstOpciones.setAdapter(mMenssageAdapter);

        mLblNamePrefix = (TextView) findViewById(R.id.textView3);

        List<String> mHorizontalList = Arrays.asList(getResources().getStringArray(R.array.interests));

        final RecyclerView mHorizontalRecyclerView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);

        final HorizontalAdapterHolder mHorizontalAdapterHolder = new HorizontalAdapterHolder(mHorizontalList, this, this);

        mHorizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        mHorizontalRecyclerView.setAdapter(mHorizontalAdapterHolder);

        mUser = new User(Utils.generateRandomName());

        face = new Face("127.0.0.1");

        ImageButton mBtnSend = (ImageButton) findViewById(R.id.imageButton);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                if(interestSelected.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please select an Interest", Toast.LENGTH_SHORT).show();
                } else{
                    sendMessage(mPosition, mUser, interestSelected);
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

            return(true);

        case R.id.find_file:

            Intent intentFileChooser = new Intent(this, FileChooserActivity.class);
            startActivityForResult(intentFileChooser,REQUEST_PATH);
            return (true);

        case R.id.user_profile:

            showUserName(mUser);
            return(true);

        case R.id.more:

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pt.ulusofona.copelabs.ndn");
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }


    public void showUserName(final User user){

        final AlertDialog.Builder textBox = new AlertDialog.Builder(this);
        textBox.setTitle("Now@ User Name");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(user.getName());
        textBox.setView(input);
        textBox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Store toast_message in JSON object
                user.setName(input.getText().toString());
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

    public void sendMessage(int position, User user, String interest){
        final JSONObject jObject = new JSONObject();  // JSON object to store toast_message
        final EditText input = (EditText)findViewById(R.id.editText);

                // Store toast_message in JSON object
                try {
                    jObject.put("data", input.getText().toString());
                    jObject.put("type", "text");
                    jObject.put("user", user.getName());
                    jObject.put("interest", interest);
                    jObject.put("date",Utils.getDate());
                    sendData(jObject.toString(), position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mMenssages.add(new Message(user.getName(),input.getText().toString(),interest,Utils.getDate()));

                mMenssageAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                input.setText("");
    }




    public void onCreateRoute(String interest){

        NDNParameters NDN = new NDNParameters(face);
        NDN.setUUID(UUID.randomUUID().toString());
        NDN.setApplicationBroadcastPrefix("/ndn/multicast/now/"+interest);
        NDN.setApplicationNamePrefix("/ndn/multicast/"+interest+"/"+ NDN.getUUID());

        ChronoSync = new ChronoSync(NDN);
        ChronoSync.addObserver(this);
        mChronosyncs.add(ChronoSync);

        mLblNamePrefix.setText("User : " + mUser.getName() + "\n" + "UUID: "+ NDN.getUUID() + "\n" + "Prefix: "+ NDN.getmApplicationNamePrefix());

    }


    public void sendData(String jsonData, int position) {

        mChronosyncs.get(position).getDataHistory().add(jsonData);  // Add action to history
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

    @Override
    public void update(Observable o, Object arg) {
       if(o instanceof ChronoSync) {
           Log.d(TAG,"Data reveived");
           dataReceiver(String.valueOf(arg));
           updateListView();
       }
    }

    public void updateListView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates uim
                mMenssageAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * Helper Function to parse and draw the action mentioned in the passed JSON string
     *
     * @param string the json representation of the action to be performed
     */
    public void dataReceiver(String string) {
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

                        break;
                    }

                    default:
                        throw new JSONException("Unrecognized string: " + string);
                }

                    if (addToHistory) {
                        ChronoSync.getDataHistory().add(string);
                    }


            } catch (JSONException e) {
                Log.d(TAG, "JSON string error: " + string);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateValueSelected(String interest) {
        Log.d(TAG,"Interest " + interest);

        if(mInterestsSelected.contains(interest)) {
            mInterestsSelected.remove(interest);
        } else {
             onCreateRoute(interest.toLowerCase());
             interestSelected = interest;
             mInterestsSelected.add(interest);
             mSpinner.setAdapter(adapter);
             mSpinner.setSelection(mPosition);
        }

    }


}
