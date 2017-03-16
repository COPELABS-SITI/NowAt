package pt.ulusofona.copelabs.now.ndn;

import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;

import net.named_data.jndn.Face;

import java.util.ArrayList;

/**
 * Interface for Activity tht uses NDN
 */
public abstract class NDN {
    public Face mFace;

    public abstract Handler getHandler();

    public abstract ProgressDialog getProgressDialog();

    public boolean activity_stop;

    public String applicationNamePrefix;

    public ArrayList<String> dataHistory;

    public abstract void handleDataReceived(String data);

    // Set the boolean flag that stops all long running loops
    public void create (){

    }
    public void stop() {

        activity_stop  = true;

        // Shut down face if it is not null
        if (mFace != null) {
            mFace.shutdown();
            Log.d(NDN.class.getSimpleName(), "Shutting down Face");
        }
    }

}
