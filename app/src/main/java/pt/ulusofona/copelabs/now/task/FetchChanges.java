package pt.ulusofona.copelabs.now.task;

import android.os.AsyncTask;
import android.util.Log;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;

import java.io.IOException;
import java.util.Observable;


import pt.ulusofona.copelabs.now.ndn.ChronoSync;

/**
 * This class extends to a Observable class and it contains the class used to fetch data
 * when it is notify that a new data was created by another user.
 *
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */
public class FetchChanges extends Observable {

    /**
     * Used for debug.
     */
    private String TAG = FetchChanges.class.getSimpleName();

    /**
     * Constructor of FetchChanges class.
     *
     * @param ChronoSync    CHronoSync object.
     * @param namePrefixStr Name prefix to be requested.
     */
    public FetchChanges(ChronoSync ChronoSync, String namePrefixStr) {
        new FetchChangesTask(ChronoSync, namePrefixStr).execute();
    }

    /**
     * This class is an AsyncTask Class which handles the action of fetch new data created
     * by others users.
     */
    public class FetchChangesTask extends AsyncTask<Void, Void, Void> implements OnTimeout {

        boolean m_shouldStop = false;
        /**
         * Name to be fetch.
         */
        private String namePrefixStr;
        /**
         * ChronoSync object.
         */
        private ChronoSync mChronoSync;

        /**
         * Used to notify when the data was fetched.
         */
        private String m_retVal;

        /**
         * Constructor of FetchChangesTask class.
         *
         * @param ChronoSync    ChronoSync object.
         * @param namePrefixStr Name to be sinchronized.
         */
        public FetchChangesTask(ChronoSync ChronoSync, String namePrefixStr) {
            this.mChronoSync = ChronoSync;
            this.namePrefixStr = namePrefixStr;

        }


        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "Fetch Task (doInBackground) for prefix: " + namePrefixStr);
            String nameStr = namePrefixStr;


            try {
                mChronoSync.getNDN().getFace().expressInterest(new Name(nameStr), new OnData() {
                    @Override
                    public void
                    onData(Interest interest, Data data) {
                        m_retVal = data.getContent().toString();
                        m_shouldStop = true;
                        Log.d(TAG, "Got content: " + m_retVal);
                        setChanged();
                        notifyObservers(m_retVal);
                    }

                }, this);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void data) {
            Log.d(TAG, "Fetch Task (onPostExecute)");
        }

        /**
         * When the request rises the timeOut, a new task of fetch is called.
         *
         * @param interest
         */
        @Override
        public void onTimeout(Interest interest) {
            m_retVal = null;
            m_shouldStop = true;
            Log.d(TAG, "Got Timeout " + namePrefixStr);
            if (!mChronoSync.getNDN().getAtivityStop()) {
                new FetchChangesTask(mChronoSync, namePrefixStr).execute();
            }
        }
    }
}
