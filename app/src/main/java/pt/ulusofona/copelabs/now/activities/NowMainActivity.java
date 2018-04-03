package pt.ulusofona.copelabs.now.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import pt.ulusofona.copelabs.now.helpers.DBManager;
import pt.ulusofona.copelabs.now.helpers.Utils;
import pt.ulusofona.copelabs.now.interfaces.NowMainActivityInterface;
import pt.ulusofona.copelabs.now.models.User;
import pt.ulusofona.copelabs.now.ndn.ChronoSync;
import pt.ulusofona.copelabs.now.models.Message;
import pt.ulusofona.copelabs.now.ndn.ChronoSyncManager;
import pt.ulusofona.copelabs.now.ndn.NDNParameters;
import pt.ulusofona.copelabs.now.ndn.NameManager;
import pt.ulusofona.copelabs.now.task.SegmentationTask;

import net.named_data.jndn.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * This class is part of Now@ application. It extends to AppCompactActivity.
 * It provides a user interface to use Now@. The principal components are a ListView
 * where is displayed all post that user exchanges and a Holder with categories that user
 * can selected.
 * From this class is started ChonoSync, principal component of this application.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:05 PM
 */

public class NowMainActivity extends AppCompatActivity implements Observer, NowMainActivityInterface {

    /**
     * Value used to retrieve the results from camera activity.
     */
    static final int REQUEST_IMAGE_CAPTURE = 1;


    static final String USER = "user";
    /**
     * Displays the name of the user.
     */
    private TextView mUserTextView;
    /**
     * Used for debug.
     */
    private String TAG = NowMainActivity.class.getSimpleName();
    /**
     * Contains every message created by the user.
     */
    private ArrayList<Message> mMenssages = new ArrayList<>();

    /**
     * Contains the ChronoSync created.
     */
    private ArrayList<ChronoSync> mChronosyncs = new ArrayList<>();
    /**
     * Adapter used to display the messages.
     */
    private MessageArrayAdapter mMenssageAdapter;
    /**
     * Interest selected in the spinner.
     */
    private String interestSelected = null;
    /**
     * Spinner used to select the category to which a message belongs.
     */
    private Spinner mSpinner;
    /**
     * Adapter used to display the information in the spinner.
     */
    private ArrayAdapter<String> adapter;
    /**
     * List of categories selected.
     */
    private ArrayList<String> mInterestsSelected = new ArrayList<>();
    /**
     * List of categories Subscribed.
     */
    private ArrayList<String> mInteresSubscribed = new ArrayList<>();

    /**
     * List of prefixes used in the application.
     */
    private ArrayList<String> mPrefixes = new ArrayList<>();
    /**
     * HashMap used to save the files shared in the application.
     */
    private HashMap<String, ArrayList> mFiles = new HashMap<>();

    /**
     * Face used to communicate with NDN.
     */
    private Face mFace;
    /**
     * User information.
     */
    private User mUser;


    /**
     * EditText used to write the content of the message.
     */
    private EditText mEditText;

    /**
     * used to save all the information in order to communicate with NDN.
     */
    private NDNParameters mNDNParmiters;
    /**
     * Map used to keep in track all the messages that arrives to the application.
     */
    private Map<String, Message> mData = new HashMap();

    private int mMessageSent = 0;
    private DBManager dbManager = new DBManager(this, this);
    private Bitmap mImageBitmap;
    private int mMessageID = 0;
    private String idMessage;
    private ChronoSyncManager mChronoSyncMngr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_main_2);
        initialConfiguration();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        dbManager.saveData(mMenssages);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageBitmap = imageBitmap;
            String fileName = System.currentTimeMillis() + mUser.getName();

            new SegmentationTask(this, imageBitmap, fileName, this).execute();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * This method creates the first configuration when the app is started.
     * Here is set up the user interface, create a Face and Users information.
     */
    public void initialConfiguration() {

        idMessage = UUID.randomUUID().toString() + mMessageID;
        //Create a new Face
        mFace = new Face("127.0.0.1");

        //Create a new User
        //mUser = new User(Utils.generateRandomName());
        mUser = new User(Utils.generateSmallUuid());
        mEditText = (EditText) findViewById(R.id.editText);

        mUserTextView = (TextView) findViewById(R.id.textView3);
        mUserTextView.setText("User : " + mUser.getName());

        //Set up adapter for messages
        mMenssageAdapter = new MessageArrayAdapter(this, mMenssages, this);
        //mMenssages.clear();

        //Start listview with messages
        ListView lstMessages = (ListView) findViewById(R.id.listView);
        lstMessages.setAdapter(mMenssageAdapter);
        List<String> mHorizontalList = Arrays.asList(getResources().getStringArray(R.array.interests));

        //Set up the RecyclerView with the interests
        final RecyclerView mHorizontalRecyclerView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        final HorizontalAdapterHolder mHorizontalAdapterHolder = new HorizontalAdapterHolder(mHorizontalList, this, this);
        mHorizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mHorizontalRecyclerView.setAdapter(mHorizontalAdapterHolder);

        //Set button to send message
        ImageButton mBtnSend = (ImageButton) findViewById(R.id.imageButton);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mInterestsSelected.isEmpty()) {
                    Log.d(TAG, "Please select an Interest");
                    Toast.makeText(getApplicationContext(), "Please select an Interest", Toast.LENGTH_SHORT).show();
                } else if (mEditText.getText().toString().isEmpty()) {
                    Log.d(TAG, "Please write a message");
                    Toast.makeText(getApplicationContext(), "Please write a message", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder sb = new StringBuilder(interestSelected);
                    sb.deleteCharAt(0);
                    String interest = sb.toString();
                    jsonMessageConstructor(mUser, interest, mEditText.getText().toString(), mEditText.getText().length(), false, -1, "");
                    mEditText.setText("");
                }

            }
        });
        ImageButton mBtnPicture = (ImageButton) findViewById(R.id.imageButton2);
        mBtnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterestsSelected.isEmpty()) {
                    Log.d(TAG, "Please select an Interest");
                    Toast.makeText(getApplicationContext(), "Please select an Interest", Toast.LENGTH_SHORT).show();
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        //Set the spinner
        mSpinner = (Spinner) findViewById(R.id.spinner3);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mInterestsSelected);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View v, int position, long id) {
                        interestSelected = parent.getItemAtPosition(position).toString();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        mChronoSyncMngr = new ChronoSyncManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.information:
                //Show Dialog with the prefixes subscribed
                Utils.showListPrefix(mPrefixes, this, "Prefixes list");
                return (true);

            case R.id.user_profile:
                //Show Dialog whit username information
                Intent intent = new Intent(this, FileChooserActivity.class);
                startActivity(intent);
                //showUserName(mUser);
                return (true);

            case R.id.delete:
                mMenssages.clear();
                mMenssageAdapter.notifyDataSetChanged();
                dbManager.deleteAll();
                return (true);

            case R.id.more:
                //Start NDN-Opp. If it does not install in the device, GoolePlay will be launched to
                //download it
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pt.ulusofona.copelabs.ndn");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Log.d(TAG, "Does not exist");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pt.ulusofona.copelabs.now")));
                }
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }


    /**
     * This method shows the user name and allows to change it.
     *
     * @param user User object
     */
    public void showUserName(final User user) {

        final AlertDialog.Builder textBox = new AlertDialog.Builder(this);
        textBox.setTitle("Now@ User Name");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(user.getName());
        textBox.setView(input);
        textBox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                user.setName(input.getText().toString());
                mUserTextView.setText("User: " + user.getName());
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

    /**
     * This method build a JSON Object whit the information that will be sent
     *
     * @param user     User object
     * @param interest String interest of the message
     * @param message  String content of the message
     */
    public void jsonMessageConstructor(User user, String interest, String message, int size, Boolean image, int section, String fileName) {
        final JSONObject jObject = new JSONObject();  // JSON object to store toast_message
        idMessage = UUID.randomUUID().toString() + mMessageID++;
        try {
            jObject.put("data", message);
            jObject.put("size", size);
            jObject.put("type", "text");
            jObject.put("user", user.getName());
            jObject.put("interest", interest);
            jObject.put("date", Utils.getDate());
            jObject.put("id", idMessage);
            jObject.put("sec", section);
            jObject.put("name", fileName);
            sendData(jObject.toString(), interest.toLowerCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Add message to list


        //mMessageSent=mMessageSent+message;
        mMessageSent++;
        if (mMessageSent == size) {
            mMenssages.add(0, new Message(user.getName(), message, interest, Utils.getDate(), String.valueOf(idMessage) + user.getName(), mImageBitmap));
            mMessageSent = 0;
        }

        if (section == -1) {
            mMenssages.add(0, new Message(user.getName(), message, interest, Utils.getDate(), String.valueOf(idMessage) + user.getName()));
            mMessageSent = 0;
        }


        mData.put(idMessage, new Message(user.getName(), message, interest, Utils.getDate(), String.valueOf(idMessage) + user.getName(), mImageBitmap));
        //Notify changes
        mMenssageAdapter.notifyDataSetChanged();

        Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
        hideSoftKeyboard();
    }

    /**
     * This method subscribes the interests on Chronosync
     *
     * @param interest String interest
     */
    public void subscribeInterest(String interest) {

        mNDNParmiters = new NDNParameters(mFace);
        mNDNParmiters.setUUID(UUID.randomUUID().toString());
        mNDNParmiters.setUUID(mUser.getName());
        mNDNParmiters.setApplicationBroadcastPrefix(NameManager.generateApplicationBroadcastPrefix(interest));
        mNDNParmiters.setApplicationNamePrefix(NameManager.generateApplicationDataPrefix(interest,mNDNParmiters.getUUID()));

        mPrefixes.add(mNDNParmiters.getApplicationBroadcastPrefix());
        mPrefixes.add(mNDNParmiters.getmApplicationNamePrefix());

        ChronoSync chronoSync = new ChronoSync(mNDNParmiters,this);
        chronoSync.addObserver(this);

        mChronosyncs.add(chronoSync);

        mInteresSubscribed.add(interest);

        mChronoSyncMngr.registerChronoSync(interest,chronoSync);
        //mChonoSyncMap.put(interest, chronoSync);
    }

    /**
     * This method takes the data and the interest to subscribe into ChronoSync
     *
     * @param jsonData String based on json structure
     * @param interest String interest selected
     */
    public void sendData(String jsonData, String interest) {

        //mChonoSyncMap.get(interest.toLowerCase()).getDataHistory().add(jsonData);
        //mChonoSyncMap.get(interest.toLowerCase()).increaseSequenceNos();


        mChronoSyncMngr.getChronoSync(interest.toLowerCase()).getDataHistory().add(jsonData);
        mChronoSyncMngr.getChronoSync(interest.toLowerCase()).increaseSequenceNos();
        Log.d(TAG, "Stroke generated: " + jsonData);
    }

    /**
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ChronoSync) {
            Log.d(TAG, "Data reveived size message " + mMenssages.size());
            parseJSONReceiver(String.valueOf(arg));
            updateListView();
        }
    }

    /**
     * This method update the ListView using a  separate thread.
     */
    public void updateListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //stuff that updates uim
                mMenssageAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * Function to parse and draw the action mentioned in the passed JSON string
     *
     * @param string       the json representation of the action to be performed.
     */
    public void parseJSONReceiver(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            try {
                String interest1 = jsonObject.getString("interest");
                if (mInteresSubscribed.contains(interest1.toLowerCase())) {

                    String type = jsonObject.get("type").toString();

                    switch (type) {

                        case "text": {
                            String message = jsonObject.getString("data");
                            String username = jsonObject.getString("user");
                            String interest = jsonObject.getString("interest");
                            String date = jsonObject.getString("date");
                            String ID = jsonObject.getString("id");
                            int size = jsonObject.getInt("size");
                            int section = jsonObject.getInt("sec");
                            String name = jsonObject.getString("name");

                            if (mData.containsKey(ID)) {
                                Log.d(TAG, "data exist " + ID);
                            } else {
                                Log.d(TAG, "data new " + ID);
                                mData.put(ID, new Message(username, message, interest, date, ID));
                                //mMenssages.add(0,new Message(username,message,interest,date,ID));
                                Log.d(TAG, "size of list: " + mMenssages.size());

                                if (section == -1) {
                                    mMenssages.add(0, new Message(username, message, interest, date, ID, null));
                                    break;
                                }

                                if (mFiles.containsKey(name)) {
                                    ArrayList arrayList = mFiles.get(name);
                                    arrayList.add(section, message);
                                    mFiles.put(name, arrayList);
                                    Log.d(TAG, "size:" + size);
                                    if (arrayList.size() == size) {
                                        String file = "";
                                        for (int i = 0; i < arrayList.size(); i++) {
                                            file = file + arrayList.get(i).toString();

                                        }
                                        byte[] array = file.getBytes();
                                        byte[] decodeString = Base64.decode(array, Base64.DEFAULT);
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                                        Log.d(TAG, "Decoding" + decodeString);
                                        mMenssages.add(0, new Message(username, message, interest, date, ID, bitmap));
                                        updateListView();
                                        mFiles.remove(ID);
                                    }

                                } else {
                                    ArrayList array = new ArrayList();
                                    Log.d(TAG, "Section: " + section);
                                    array.add(section, message);
                                    mFiles.put(name, array);
                                }
                            }
                            break;
                        }
                        default:
                            throw new JSONException("Unrecognized string: " + string);
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG, "JSON string error: " + string);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is called when de user selects a interest from the interest bar.
     *
     * @param interest Interest selected.
     */
    @Override
    public void updateValueSelected(String interest) {
        Log.d(TAG, "Interest " + interest);

        if (mInterestsSelected.contains("#" + interest)) {
            Log.d(TAG, "Delete");
            mInterestsSelected.remove("#" + interest);
            mSpinner.setAdapter(adapter);
            mChronoSyncMngr.getChronoSync(interest.toLowerCase()).getNDN().setActivityStop(true);
            //mChonoSyncMap.get(interest.toLowerCase()).getNDN().setActivityStop(true);

            //mChonoSyncMap.get(interest.toLowerCase()).mSync.shutdown();
            //mChonoSyncMap.remove(interest.toLowerCase());
            //mNDNParmiters.setActivityStop(true);

        } else if (mInteresSubscribed.contains(interest.toLowerCase())) {
            Log.d(TAG, "Contains");
            //mNDNParmiters.setActivityStop(false);
            //subscribeInterest(interest.toLowerCase());
            //mChonoSyncMap.get(interest.toLowerCase()).getNDN().setActivityStop(false);
            mChronoSyncMngr.getChronoSync(interest.toLowerCase()).getNDN().setActivityStop(false);
            interestSelected = interest;
            mInterestsSelected.add("#" + interest);
            mSpinner.setAdapter(adapter);
            mSpinner.setSelection(mSpinner.getFirstVisiblePosition());
        } else {
            Log.d(TAG, "Create");
            subscribeInterest(interest.toLowerCase());
            interestSelected = interest;
            mInterestsSelected.add("#" + interest);
            mSpinner.setAdapter(adapter);
            mSpinner.setSelection(mSpinner.getFirstVisiblePosition());
        }
    }

    @Override
    public void messageSelected(int message) {
        Log.d(TAG, "Message: " + message);
        if (mMenssages.get(message).getSave()) {
            mMenssages.get(message).setSave(false);
        } else {
            mMenssages.get(message).setSave(true);
        }
        mMenssageAdapter.notifyDataSetChanged();

    }

    @Override
    public void getDataRequested(List<Message> messages) {
        if (mMenssages.size() > 0) {
            for (int i = 0; i < mMenssages.size(); i++) {
                if (mMenssages.get(i).getSave()) {
                    mMenssages.remove(i);
                    Log.d(TAG, "Remove");
                }
            }
        }
        mMenssages.addAll(messages);
        mMenssageAdapter.notifyDataSetChanged();
    }

    /**
     * This method is called when a segmentation task is performed.
     *
     * @param data             Content of the message.
     * @param fileCompleteFile Size of the file to be sent.
     * @param section          Section of the actual peace of data inside of the entire data.
     * @param fileName         Name of the file.
     */
    @Override
    public void segmentationResult(final String data, final int fileCompleteFile, int section, String fileName) {
        Log.d(TAG, "Data: " + data);

        StringBuilder sb = new StringBuilder(interestSelected);
        sb.deleteCharAt(0);
        final String interest = sb.toString();
        jsonMessageConstructor(mUser, interest, data, fileCompleteFile, true, section, fileName);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        dbManager.requestData();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        Log.d(TAG, "save");
        savedInstanceState.putString(USER, mUser.getName());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "Restore");
        // Restore state members from saved instance
        mUserTextView.setText(savedInstanceState.getString(USER));
    }

    /**
     * This function is used to hide the keyboard.
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}



