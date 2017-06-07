package pt.ulusofona.copelabs.now.ndn;

import android.util.Log;

import net.named_data.jndn.Face;

/**
 * Created by copelabs on 04/04/2017.
 */

public class NDNParameters {

    private String TAG = NDNParameters.class.getSimpleName();

    private Face mFace;

    private String mApplicationNamePrefix;

    private String mApplicationBroadcastPrefix;

    private boolean mActivityStop;

    private String mUUID;

    private String f;
    public NDNParameters (Face face){
        mFace=face;
    }
    public void stop(){
        mActivityStop = true;

        if(mFace != null){
            mFace.shutdown();
            Log.d(TAG, "Shutting down face");
        }

    }

    public void setApplicationBroadcastPrefix (String applicationBroadcastPrefix){
        mApplicationBroadcastPrefix=applicationBroadcastPrefix;
    }
    public void setApplicationNamePrefix (String applicationNamePrefixt){
        mApplicationNamePrefix=applicationNamePrefixt;
    }
    public void setUUID(String UUID){
        mUUID=UUID;
    }

    public void setActivityStop(boolean condition){
        mActivityStop=condition;
    }

    public String getApplicationBroadcastPrefix(){
        return mApplicationBroadcastPrefix;
    }
    public String getmApplicationNamePrefix(){
        return mApplicationNamePrefix;
    }
    public Face getFace(){
        return mFace;
    }

    public boolean getAtivityStop(){
        return mActivityStop;
    }
    public String getUUID(){
        return mUUID;
    }
}
