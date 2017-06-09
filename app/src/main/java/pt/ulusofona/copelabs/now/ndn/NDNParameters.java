/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:09 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.ndn;
import android.util.Log;

import net.named_data.jndn.Face;


public class NDNParameters {

    private String TAG = NDNParameters.class.getSimpleName();

    private Face mFace= new Face("127.0.0.1");

    private String mApplicationNamePrefix;

    private String mApplicationBroadcastPrefix;

    private boolean mActivityStop;

    private String mUUID;

    public NDNParameters (){

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
