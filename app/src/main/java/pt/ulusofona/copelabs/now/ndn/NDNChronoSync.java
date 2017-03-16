package pt.ulusofona.copelabs.now.ndn;

import android.util.Log;

import net.named_data.jndn.sync.ChronoSync2013;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.ulusofona.copelabs.now.task.RegisterPrefixTask;

/**
 * Interface for Activity that uses ChronoSync
 */
public abstract class NDNChronoSync extends NDN {

    public ChronoSync2013 sync;

    public String UUID;

    private String Interest;

    // Keeping track of what seq #'s are requested from each user
    public Map<String, Long> highestRequested;

    public String applicationBroadcastPrefix;

    private String TAG = NDNChronoSync.class.getSimpleName();  // TAG for logging

    /**
     * Start initialize sequence: Ping -> RegisterPrefix -> ChronoSync Registration
     */
    public void initialize() {

        // Start Ping sequence
        activity_stop = false;

        dataHistory = new ArrayList<>();  // History of packets generated

        // Keeping track of what seq #'s are requested from each user
        highestRequested = new HashMap<>();

        new RegisterPrefixTask(this).execute();

    }


    public void set(String prefix){



    }

    /**
     * Bring sequence #'s up to par when a new piece of data is produced.
     */
    public void increaseSequenceNos() {

        // Create a new thread to publish new sequence numbers
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sync != null) {
                        while (sync.getSequenceNo() < dataHistory.size() && sync.getSequenceNo() != -1) {
                            Log.d(TAG, "Seq is now: " + sync.getSequenceNo());
                            Log.d(TAG, "DataHistoy size:" + dataHistory.size());
                            sync.publishNextSequenceNo();
                            Log.d(TAG, "Published next seq number. Seq is now: " + sync.getSequenceNo());
                        }
                    }
                } catch (IOException | net.named_data.jndn.security.SecurityException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
