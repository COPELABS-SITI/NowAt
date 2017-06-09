/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.ndn;

import android.util.Log;

import net.named_data.jndn.sync.ChronoSync2013;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import pt.ulusofona.copelabs.now.task.FetchChanges;
import pt.ulusofona.copelabs.now.task.RegisterChronoSync;
import pt.ulusofona.copelabs.now.task.RegisterPrefix;


public class ChronoSync extends Observable implements Observer  {

    private String TAG = ChronoSync.class.getSimpleName();

    public ChronoSync2013 mSync;

    private NDNParameters mNDN;

    private Map<String, Long> mHighestRequested;

    private ArrayList<String> mDataHistory;

    public ChronoSync (NDNParameters ndn){

        mNDN=ndn;

        startChronoSync();
    }


    @Override
    public void update(Observable o, Object arg) {

        if( o instanceof RegisterPrefix) {
            Log.d(TAG, "Registered");
            new RegisterChronoSync(this).addObserver(this);
        }else if(o instanceof RegisterChronoSync){
            Log.d(TAG, "A change occurred");
            new FetchChanges(this,String.valueOf(arg)).addObserver(this);
        }else if (o instanceof FetchChanges){
            Log.d(TAG,"New data fetched");
            setChanged();
            notifyObservers(String.valueOf(arg));

        }
    }

    public void startChronoSync() {

        mNDN.setActivityStop(false);

        mDataHistory = new ArrayList<>();  // History of packets generated

        // Keeping track of what seq #'s are requested from each user
        mHighestRequested = new HashMap<>();

        new RegisterPrefix(this).addObserver(ChronoSync.this);


    }
    public void increaseSequenceNos() {

        // Create a new thread to publish new sequence numbers
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSync != null) {
                        while (mSync.getSequenceNo() < mDataHistory.size() && mSync.getSequenceNo() != -1) {
                            Log.d(TAG, "Seq is now: " + mSync.getSequenceNo());
                            Log.d(TAG, "DataHistoy size:" + mDataHistory.size());
                            mSync.publishNextSequenceNo();
                            Log.d(TAG, "Published next seq number. Seq is now: " + mSync.getSequenceNo());
                        }
                    }
                } catch (IOException | net.named_data.jndn.security.SecurityException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public NDNParameters getNDN (){
        return mNDN;
    }


    public ArrayList<String> getDataHistory(){
        return mDataHistory;
    }

    public Map<String, Long> getHighestRequested(){
        return mHighestRequested;
    }



}
