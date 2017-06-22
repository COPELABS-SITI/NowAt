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
import java.util.Observable;

import pt.ulusofona.copelabs.now.helpers.Utils;
import pt.ulusofona.copelabs.now.ndn.ChronoSync;


public class RegisterPrefix extends Observable {

    private String TAG = RegisterPrefix.class.getSimpleName();

    private ChronoSync  mChronoSync;



    public RegisterPrefix(ChronoSync chornosync){
        mChronoSync=chornosync;
        new RegisterPrefixTask().execute();
    }

    private class RegisterPrefixTask extends AsyncTask<Void, Void, String> {

        private String m_retVal = "not changed";

        RegisterPrefixTask() {

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
            keyChain.setFace(mChronoSync.getNDN().getFace());
            try {
                mChronoSync.getNDN().getFace().setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
            } catch (SecurityException e) {
                m_retVal = "ERROR: " + e.getMessage();
                e.printStackTrace();
                return m_retVal;
            }

            Name base_name = new Name(mChronoSync.getNDN().getmApplicationNamePrefix());


            insert(base_name);
            return m_retVal;

        }

        private void insert(Name base_name){

            try {
                // Register the prefix

                mChronoSync.getNDN().getFace().registerPrefix(base_name, new OnInterestCallback() {
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
                        if (mChronoSync.getDataHistory().size() > comp) {
                            blob = new Blob(mChronoSync.getDataHistory().get(comp).getBytes());
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
                setChanged();
                notifyObservers();
                //new RegisterChronoSyncTask(ndnActivity).execute();
            }
        }
    }
}
