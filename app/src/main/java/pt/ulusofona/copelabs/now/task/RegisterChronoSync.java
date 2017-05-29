package pt.ulusofona.copelabs.now.task;

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
import pt.ulusofona.copelabs.now.ndn.NDNChronoSync;

/**
 * Created by copelabs on 06/04/2017.
 */

public class RegisterChronoSync extends Observable {

    private ChronoSync mChronoSinc;

    public RegisterChronoSync(ChronoSync ChronoSync){
        mChronoSinc=ChronoSync;
        new RegisterChronoSyncTask(mChronoSinc).execute();
    }


    public class RegisterChronoSyncTask extends AsyncTask<Void, Void, Void> {

        int attempt = 1;  // Keep track on current attempt. Try for max 3 attempts.

        private ChronoSync mChronoSinc;

        private String TAG = RegisterChronoSyncTask.class.getSimpleName();  // TAG for logging

        // Constructors
        RegisterChronoSyncTask(ChronoSync ChronoSync) {
            this(ChronoSync, 1);

        }

        RegisterChronoSyncTask(ChronoSync ChronoSync, int attempt) {
            this.attempt = attempt;
            this.mChronoSinc = ChronoSync;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d(TAG, "ChronoSync Task (doInBackground): Attempt: " + attempt);

                KeyChain testKeyChain = Utils.buildTestKeyChain();

         mChronoSinc.mSync = new ChronoSync2013(new ChronoSync2013.OnReceivedSyncState() {

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

                                if (syncSeq == highestSeq + 1) {
                                    // New request
                                    mChronoSinc.getHighestRequested().put(syncPrefix, syncSeq);
                                } else if (syncSeq <= highestSeq) {
                                    // Duplicate request, ignore
                                    Log.d(TAG, "Avoiding starting new task for: " + syncPrefix + "/" + syncSeq);

                                } else if (syncSeq - highestSeq > 1) {
                                    // Gaps found. Recover missing pieces
                                    Log.d(TAG, "Gaps in SYNC found. Sending Interest for missing pieces.");
                                    highestSeq++;
                                    while (highestSeq <= syncSeq) {
                                        setValue(syncPrefix+"/"+highestSeq);

                                        //new FetchChangesTask(ndnActivity, syncPrefix + "/" + highestSeq).execute();
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
                            //new FetchChangesTask(ndnActivity, syncNameStr).execute();

                        }

                    }
                }, new ChronoSync2013.OnInitialized() {
                    @Override
                    public void onInitialized() {
                        new Runnable() {
                            @Override
                            public void run() {
                                // Done registering ChronoSync
                                Log.d(TAG, "Chronosync registration succed");
                            }
                        };


                        Log.d(TAG, "ChronoSync onInitialized");
                    }
                }, new Name(mChronoSinc.getNDN().getmApplicationNamePrefix()),

                        // App data prefix
                        new Name(mChronoSinc.getNDN().getApplicationBroadcastPrefix()), // Broadcast prefix
                        0L,
                        mChronoSinc.getNDN().getFace(),
                        testKeyChain,
                        testKeyChain.getDefaultCertificateName(),
                        5000.0, new OnRegisterFailed() {

                    @Override
                    public void onRegisterFailed(Name prefix) {
                        // Handle failure of this register attempt. Try again.
                        Log.d(TAG, "ChronoSync registration failed, Attempt: " + attempt);
                        Log.d(TAG, "Starting next attempt");


                        //try to connect the chronosync
                        if(attempt < 100000) {
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

                }
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!mChronoSinc.getNDN().getAtivityStop()) {
                        try {
                            Thread.sleep(100);
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
            }).start();
        }
    }

    public void setValue(String syncNameStr){
        setChanged();
        notifyObservers(syncNameStr);
    }
}
