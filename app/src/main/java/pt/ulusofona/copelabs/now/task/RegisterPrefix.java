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

/**
 * This class extends to an Observable class and it contains the functions used to register
 * prefixes in NDN.
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */
public class RegisterPrefix extends Observable {

    /**
     * Used for debug.
     */
    private String TAG = RegisterPrefix.class.getSimpleName();

    /**
     * ChronoSync object.
     */
    private ChronoSync  mChronoSync;

    /**
     * Constructor of RegisterPrefix class.
     * @param chornosync ChronoSync object.
     */
    public RegisterPrefix(ChronoSync chornosync){
        mChronoSync=chornosync;
        new RegisterPrefixTask().execute();
    }

    /**
     * THis class extends to AsyncTask and it performs the registration of the prefix into NDN.
     */
    private class RegisterPrefixTask extends AsyncTask<Void, Void, String> {

        private String m_retVal = "not changed";

        /**
         * Constructor of register prefix task.
         */
        public RegisterPrefixTask(){}

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

        /**
         * This method performs the registration of the prefix.
         * @param base_name Name to be registered.
         */
        private void insert(Name base_name){

            try {
                // Register the prefix
                mChronoSync.getNDN().getFace().registerPrefix(base_name, new OnInterestCallback() {
                    @Override

                    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
                        Name interestName = interest.getName();
                        Log.d(TAG,interest.getName().toString());
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

        /**
         * When the registration ends, the observable is notified about that action.
         * @param result String with the status of the operation.
         */
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
