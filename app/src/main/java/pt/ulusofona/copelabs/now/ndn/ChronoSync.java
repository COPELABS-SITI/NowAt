package pt.ulusofona.copelabs.now.ndn;

import android.content.Context;
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

/**
 * ChonoSync class is based on ChonoSync2013. This class extend to observer and take car of start
 * different tasks in order to send data and receive data on top of ChronoSync2013
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 */


public class ChronoSync extends Observable implements Observer {

    /**
     * ChronoSycn used to synchronize data.
     */
    public ChronoSync2013 mSync;
    /**
     * Used for debug.
     */
    private String TAG = ChronoSync.class.getSimpleName();
    /**
     * NDN parameters used to set up the ChronoSync.
     */
    private NDNParameters mNDN;

    /**
     * HashMap used to track all the requests.
     */
    private Map<String, Long> mHighestRequested;

    /**
     * ArrayList used to save all the data shared.
     */
    private ArrayList<String> mDataHistory;

    /**
     * Used to register the prefix into NDN.
     */
    private RegisterPrefix mRegisterPrefix;

    private Context mContext;
    /**
     * Constructor of Chronosync class
     *
     * @param ndn Object NDNParameters
     */
    public ChronoSync(NDNParameters ndn, Context context) {
        mNDN = ndn;
        startChronoSync();
        mContext = context;
    }


    /**
     * This methob is called when a register task ends successfully, a chronoSync is registerred or
     * when a new content is fetch.
     *
     * @param o   Object observable which called the method update.
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof RegisterPrefix) {
            Log.d(TAG, "Registered");
            new RegisterChronoSync(this).addObserver(this);
        } else if (o instanceof RegisterChronoSync) {
            Log.d(TAG, "A change occurred");
            new FetchChanges(this, String.valueOf(arg)).addObserver(this);
        } else if (o instanceof FetchChanges) {
            Log.d(TAG, "New data fetched");
            setChanged();
            notifyObservers(String.valueOf(arg));

        }
    }

    /**
     * This method start the ChonoSync process
     */
    public void startChronoSync() {


        mNDN.setActivityStop(false);

        mDataHistory = new ArrayList<>();  // History of packets generated

        mHighestRequested = new HashMap<>();

        mRegisterPrefix = new RegisterPrefix(this);
        mRegisterPrefix.addObserver(ChronoSync.this);


    }

    /**
     * This method contains a thread that is used to increase the sequence number of the data
     * created.
     */
    public void increaseSequenceNos() {
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

    /**
     * Get the NDNParameters
     *
     * @return NDNparameters Object
     */
    public NDNParameters getNDN() {
        return mNDN;
    }

    /**
     * Get all the data that is synchronized by ChronoSync
     *
     * @return ArrayList whit the Data
     */
    public ArrayList<String> getDataHistory() {
        return mDataHistory;
    }

    /**
     * Get HighestRequested that keeping track the sequence number fro each user
     *
     * @return Map
     */
    public Map<String, Long> getHighestRequested() {
        return mHighestRequested;
    }

    public Context getContext(){
        return mContext;
    }

}
