package pt.ulusofona.copelabs.now.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import net.named_data.jndn.Name;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.sync.ChronoSync2013;

import java.io.IOException;
import java.util.List;
import java.util.Observable;

import pt.ulusofona.copelabs.now.helpers.Utils;
import pt.ulusofona.copelabs.now.ndn.ChronoSync;

/**
 * This class is used to register the CrhonoSync and starts synchronizing the information
 * shared by the applications. Also, this class extends to Observable class.
 *
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */
public class RegisterChronoSync extends Observable {

    /**
     * ChronoSync used to synchronize data.
     */
    private ChronoSync mChronoSinc;

    /**
     * Constructor of RegisterChronoSync class.
     * @param ChronoSync ChronoSync.
     */
    public RegisterChronoSync(ChronoSync ChronoSync){
        mChronoSinc=ChronoSync;
        new RegisterChronoSyncTask(mChronoSinc).execute();
    }

    /**
     * This class extends to AsyncTask and it handles the action of receive new information from
     * others application that are using ChronoSync in order to synchronize information in the same
     * prefix.
     */
    public class RegisterChronoSyncTask extends AsyncTask<Void, Void, Void> implements ChronoSync2013.OnInitialized, OnRegisterFailed, ChronoSync2013.OnReceivedSyncState{

        /**
         * Dialog used to show the progress of the task.
         */
        private ProgressDialog dialog;

        /**
         * Number of attempts to register the chronoSync.
         */
        private int attempt = 1;

        /**
         * Contains the functions to keep tracking all the information shared.
         */
        private ChronoSync mChronoSinc;

        /**
         * Used for debug.
         */
        private String TAG = RegisterChronoSyncTask.class.getSimpleName();  // TAG for logging

        /**
         * Constructor of RegisterChronoSyncTask class.
         * @param ChronoSync ChronoSync
         */
        public RegisterChronoSyncTask(ChronoSync ChronoSync) {
            this(ChronoSync, 1);

        }

        /**
         * Constructor of RegisterChronoSyncTask class.
         * @param ChronoSync ChronoSync
         * @param attempt Number of attempt to be registered.
         */
        public RegisterChronoSyncTask(ChronoSync ChronoSync, int attempt) {
            this.attempt = attempt;
            this.mChronoSinc = ChronoSync;
        }

        /**
         * This method displays the dialog used to show the progress of the task.
         */
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mChronoSinc.getContext());
            dialog.setMessage("Registering prefixes...");
            dialog.setIndeterminate(true);
            dialog.show();
            super.onPreExecute();
        }
        /**
         * This method register the ChronoSync2013.
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d(TAG, "ChronoSync Task (doInBackground): Attempt: " + attempt);

                KeyChain testKeyChain = Utils.buildTestKeyChain();

                mChronoSinc.mSync = new ChronoSync2013(this,
                this, new Name(mChronoSinc.getNDN().getmApplicationNamePrefix()),

                        // App data prefix
                        new Name(mChronoSinc.getNDN().getApplicationBroadcastPrefix()),
                        // Broadcast prefix
                        0L,
                        mChronoSinc.getNDN().getFace(),
                        testKeyChain,
                        testKeyChain.getDefaultCertificateName(),
                        20000.0, this
                );
            } catch (IOException | net.named_data.jndn.security.SecurityException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Start the long running thread that keeps processing the events on the face every
            // few milliseconds

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
           new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if(!mChronoSinc.getNDN().getAtivityStop()) {
                            try {
                                Thread.sleep(100);
                                //Log.d(TAG, "**"+ mChronoSinc.getNDN().getmApplicationNamePrefix());
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }
                            try {
                                mChronoSinc.getNDN().getFace().processEvents();

                            } catch (IOException | EncodingException e) {
                                e.printStackTrace();
                                mChronoSinc.getNDN().getFace().shutdown();

                            }
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onInitialized() {
            new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Chronosync registration succed");
                }
            };
            Log.d(TAG, "ChronoSync onInitialized");
        }

        @Override
        public void onRegisterFailed(Name prefix) {
            Log.d(TAG, "ChronoSync registration failed, Attempt: " + attempt);
            Log.d(TAG, "Starting next attempt");
            //try to connect the chronosync
            if(attempt < 3) {
                new RegisterChronoSyncTask(mChronoSinc, attempt + 1).execute();
            }else{
                new Runnable() {
                    @Override
                    public void run() {
                        // Done registered fail ChronoSync
                        Log.d(TAG, "Chronosync registration failed");
                    }
                };
                attempt=1;
            }
        }

        @Override
        public void onReceivedSyncState(List syncStates, boolean isRecovery) {
            for (Object syncStateOb : syncStates) {

                ChronoSync2013.SyncState syncState = (ChronoSync2013.SyncState) syncStateOb;

                String syncPrefix = syncState.getDataPrefix();

                long syncSeq = syncState.getSequenceNo();

                Log.d(TAG, "1 SyncPrefix " + syncPrefix);

                Log.d(TAG, "2 SyncState sequenceNo " + syncSeq);

                Log.d(TAG, "MAP" + mChronoSinc.getHighestRequested().values());
                // Ignore the initial sync state and sync updates of this user
                if (syncSeq == 0 || syncPrefix.contains(mChronoSinc.getNDN().getUUID())) {
                    Log.d(TAG, "SYNC: prefix: " + syncPrefix + " seq: " + syncSeq + " ignored. (is Recovery: " + isRecovery + ")");
                    continue;
                }

                if (mChronoSinc.getHighestRequested().keySet().contains(syncPrefix)) {
                    long highestSeq = mChronoSinc.getHighestRequested().get(syncPrefix);
                    Log.d(TAG, "highestSeq: " + highestSeq);
                    if (syncSeq == highestSeq + 1) {
                        // New request
                        mChronoSinc.getHighestRequested().put(syncPrefix, syncSeq);
                    } else if (syncSeq <= highestSeq) {
                        // Duplicate request, ignore
                        Log.d(TAG, "Avoiding starting new task for: " + syncPrefix + "/" + syncSeq);
                        continue;
                    } else if (syncSeq - highestSeq > 1) {
                        // Gaps found. Recover missing pieces
                        Log.d(TAG, "Gaps in SYNC found. Sending Interest for missing pieces.");
                        highestSeq++;
                        while (highestSeq <= syncSeq) {
                            setValue(syncPrefix+"/"+highestSeq);

                            //new FetchChanges(mChronoSinc, syncPrefix + "/" + highestSeq);
                            highestSeq++;
                        }
                        mChronoSinc.getHighestRequested().put(syncPrefix, syncSeq);
                    }
                }  else {
                    mChronoSinc.getHighestRequested().put(syncPrefix, syncSeq);
                }
                String syncNameStr = syncPrefix + "/" + syncSeq;
                Log.d(TAG, "SYNC: " + syncNameStr + " (is Recovery: " + isRecovery + ")");
                setValue(syncNameStr);
                //new FetchChanges(mChronoSinc, syncNameStr);

            }


        }
    }


    public void setValue(String syncNameStr){
        setChanged();
        notifyObservers(syncNameStr);
    }
}
