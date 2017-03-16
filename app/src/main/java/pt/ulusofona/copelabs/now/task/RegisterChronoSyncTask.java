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

import pt.ulusofona.copelabs.now.helpers.Utils;
import pt.ulusofona.copelabs.now.ndn.NDNChronoSync;

/**
 * AsyncTask to perform registration for ChronoSync.
 * <p/>
 * ALSO: Starts the long running thread that keeps preocessing the events on the Face.
 */
public class RegisterChronoSyncTask extends AsyncTask<Void, Void, Void> {

    int attempt = 1;  // Keep track on current attempt. Try for max 3 attempts.

    private NDNChronoSync ndnActivity;
    private long sync=0;
    private String TAG = RegisterChronoSyncTask.class.getSimpleName();  // TAG for logging

    // Constructors
    public RegisterChronoSyncTask(NDNChronoSync ndnActivity) {
        this(ndnActivity, 1);

    }

    public RegisterChronoSyncTask(NDNChronoSync ndnActivity, int attempt) {
        this.attempt = attempt;
        this.ndnActivity = ndnActivity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Log.d(TAG, "ChronoSync Task (doInBackground): Attempt: " + attempt);

            KeyChain testKeyChain = Utils.buildTestKeyChain();

            ndnActivity.sync = new ChronoSync2013(new ChronoSync2013.OnReceivedSyncState() {

                @Override
                public void onReceivedSyncState(List syncStates, boolean isRecovery) {

                    for (Object syncStateOb : syncStates) {

                        ChronoSync2013.SyncState syncState = (ChronoSync2013.SyncState) syncStateOb;

                        String syncPrefix = syncState.getDataPrefix();

                        long syncSeq = syncState.getSequenceNo();

                        Log.d(TAG, "1 SyncPrefix " + syncPrefix);

                        Log.d(TAG, "2 SyncState sequenceNo " + syncSeq);

                       /* while (sync < syncSeq) {
                            new FetchChangesTask(ndnActivity, syncPrefix + "/" + sync).execute();
                            sync++;

                        }*/

                        //ndnActivity.highestRequested.put(syncPrefix, syncSeq);

                        Log.d(TAG, "MAP" + ndnActivity.highestRequested.values());
                        // Ignore the initial sync state and sync updates of this user
                       if (syncSeq == 0 || syncPrefix.contains(ndnActivity.UUID)) {
                            Log.d(TAG, "SYNC: prefix: " + syncPrefix + " seq: " + syncSeq + " ignored. (is Recovery: " + isRecovery + ")");
                            continue;
                        }

                        //long highestSeq1 = ndnActivity.highestRequested.get(syncPrefix);
                        //Log.d(TAG, "SyncState sequenceNo before if " + highestSeq1);
                        if (ndnActivity.highestRequested.keySet().contains(syncPrefix)) {
                            long highestSeq = ndnActivity.highestRequested.get(syncPrefix);

                            if (syncSeq == highestSeq + 1) {
                                // New request
                                ndnActivity.highestRequested.put(syncPrefix, syncSeq);
                            } else if (syncSeq <= highestSeq) {
                                // Duplicate request, ignore
                                Log.d(TAG, "Avoiding starting new task for: " + syncPrefix + "/" + syncSeq);

                            } else if (syncSeq - highestSeq > 1) {
                                // Gaps found. Recover missing pieces
                                Log.d(TAG, "Gaps in SYNC found. Sending Interest for missing pieces.");
                                highestSeq++;
                                while (highestSeq <= syncSeq) {
                                    new FetchChangesTask(ndnActivity, syncPrefix + "/" + highestSeq).execute();
                                    highestSeq++;
                                }
                                ndnActivity.highestRequested.put(syncPrefix, syncSeq);
                            }
                        }  else {
                            ndnActivity.highestRequested.put(syncPrefix, syncSeq);
                        }
                        String syncNameStr = syncPrefix + "/" + syncSeq;
                        Log.d(TAG, "SYNC: " + syncNameStr + " (is Recovery: " + isRecovery + ")");
                        new FetchChangesTask(ndnActivity, syncNameStr).execute();

                    }

                }
            }, new ChronoSync2013.OnInitialized() {
                @Override
                public void onInitialized() {
                    ndnActivity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            // Done registering ChronoSync

                            Log.d(TAG, "Chronosync registration succed");
                        }
                    });


                    Log.d(TAG, "ChronoSync onInitialized");
                }
            }, new Name(ndnActivity.applicationNamePrefix),

                    // App data prefix
                    new Name(ndnActivity.applicationBroadcastPrefix), // Broadcast prefix
                    0l,
                    ndnActivity.mFace,
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
                        new RegisterChronoSyncTask(ndnActivity, attempt + 1).execute();
                    }else{
                    ndnActivity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            // Done registered fail ChronoSync

                            Log.d(TAG, "Chronosync registration failed");

                        }
                    });
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
                while (!ndnActivity.activity_stop) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    try {
                        ndnActivity.mFace.processEvents();

                    } catch (IOException | EncodingException e) {
                        e.printStackTrace();
                        ndnActivity.mFace.shutdown();

                    }
                }
            }
        }).start();
    }
}