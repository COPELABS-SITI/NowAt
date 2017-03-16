package pt.ulusofona.copelabs.now.task;

import android.os.AsyncTask;
import android.util.Log;

import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.util.Blob;

import java.io.IOException;

import pt.ulusofona.copelabs.now.helpers.Utils;
import pt.ulusofona.copelabs.now.ndn.NDNChronoSync;


/**
 * AsyncTask to perform a registeration of this user's prefix.
 * <p/>
 * ALSO: Starts the task to register for ChronoSync.
 */
public class RegisterPrefixTask extends AsyncTask<Void, Void, String> {

    private String m_retVal = "not changed";

    public NDNChronoSync ndnActivity;

    private String TAG = RegisterPrefixTask.class.getSimpleName();  // TAG for logging

    public RegisterPrefixTask(NDNChronoSync ndnActivity) {
        this.ndnActivity = ndnActivity;
    }

    @Override
    protected String doInBackground(Void... params) {

            Log.d(TAG, "Register Prefix Task (doInBackground)");


            // Create keychain
            KeyChain keyChain;
            try {
                keyChain = Utils.buildTestKeyChain();
            } catch (SecurityException e) {
                m_retVal = "ERROR: " + e.getMessage();
                e.printStackTrace();
                return m_retVal;
            }

            // Register keychain with the face
            keyChain.setFace(ndnActivity.mFace);
            try {
                ndnActivity.mFace.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
            } catch (SecurityException e) {
                m_retVal = "ERROR: " + e.getMessage();
                e.printStackTrace();
                return m_retVal;
            }

            Name base_name = new Name(ndnActivity.applicationNamePrefix);


            insert(base_name);
            //insert(base_name1);
           /* try {
                // Register the prefix

                ndnActivity.mFace.registerPrefix(base_name, new OnInterestCallback() {
                    @Override

                    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {

                        Name interestName = interest.getName();
                        String lastComp = interestName.get(interestName.size() - 1).toEscapedString();
                        Log.i("NDN", "Interest received: " + lastComp);
                        int comp = Integer.parseInt(lastComp) - 1;

                        Data data = new Data();
                        data.setName(new Name(interestName));
                        Blob blob;
                        if (ndnActivity.dataHistory.size() > comp) {
                            blob = new Blob(ndnActivity.dataHistory.get(comp).getBytes());
                            data.setContent(blob);
                        } else {
                            return;
                        }
                        try {
                            face.putData(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }, new OnRegisterFailed() {
                    @Override
                    public void onRegisterFailed(Name prefix) {
                        Log.d(TAG, "Register Prefix Task: Registration failed");
                    }
                });
            } catch (IOException | SecurityException e) {
                m_retVal = "ERROR: " + e.getMessage();
                e.printStackTrace();
                return m_retVal;
            }*/

            return m_retVal;

    }

    public void insert(Name base_name){

        try {
            // Register the prefix

            ndnActivity.mFace.registerPrefix(base_name, new OnInterestCallback() {
                @Override

                public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {

                    Name interestName = interest.getName();
                    Log.i("NDN", "Interest FilterID " + interestFilterId);
                    String lastComp = interestName.get(interestName.size() - 1).toEscapedString();
                    Log.i("NDN", "Interest received: " + lastComp);
                    int comp = Integer.parseInt(lastComp) - 1;

                    Data data = new Data();
                    data.setName(new Name(interestName));
                    Blob blob;
                    if (ndnActivity.dataHistory.size() > comp) {
                        blob = new Blob(ndnActivity.dataHistory.get(comp).getBytes());
                        data.setContent(blob);
                    } else {
                        return;
                    }
                    try {
                        face.putData(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }, new OnRegisterFailed() {
                @Override
                public void onRegisterFailed(Name prefix) {
                    Log.d(TAG, "Register Prefix Task: Registration failed");
                }
            });
        } catch (IOException | SecurityException e) {
            m_retVal = "ERROR: " + e.getMessage();
            e.printStackTrace();
            //return m_retVal;
        }

    }

    @Override
    protected void onPostExecute(final String result) {
        if (m_retVal.contains("ERROR:")) {
            // If error, end the activity
            Log.d(TAG, "Error Register Prefix Task");
        } else {
            // Start task to register with ChronoSync
            Log.d(TAG, "Register Prefix Task ended (onPostExecute)");
            Log.d(TAG, "About to trigger Register ChronoSync");
            new RegisterChronoSyncTask(ndnActivity).execute();
        }
    }
}