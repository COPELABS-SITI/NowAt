package pt.ulusofona.copelabs.now.ndn;

import net.named_data.jndn.Face;

/**
 * This class is part of Now@ application. It provides information about basic parameters necessary
 * to configure NDN. The principal parameters are Face, ApplicationBroadCastPrefix and
 * ApplicationNamePrefix
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:09 PM
 */


public class NDNParameters {

    /**
     * Used for debug.
     */
    private String TAG = NDNParameters.class.getSimpleName();

    /**
     * Face used to communicate with NDN.
     */
    private Face mFace;

    /**
     * ApplicationNamePrefix requested by ChronoSync.
     */

    private String mApplicationNamePrefix;

    /**
     * ApplicationBroadcastPrefix requested by ChronoSync.
     */
    private String mApplicationBroadcastPrefix;

    /**
     * Boolean used to stop the synchronization process.
     */
    private boolean mActivityStop;

    /**
     * UUID of the application.
     */
    private String mUUID;

    /**
     * Construct method to NDNPArameters
     *
     * @param face NDN Face
     */
    public NDNParameters(Face face) {
        mFace = face;
    }

    /**
     * Set ApplicationNamePrefix value.
     *
     * @param applicationNamePrefixt String with the ApplicationNamePrefix.
     */
    public void setApplicationNamePrefix(String applicationNamePrefixt) {
        mApplicationNamePrefix = applicationNamePrefixt;
    }

    /**
     * Set Activity status.
     *
     * @param condition Boolean used to set the status of the synchronization task.
     */
    public void setActivityStop(boolean condition) {
        mActivityStop = condition;
    }

    /**
     * Get Application Broadcast Prefix of an interest
     *
     * @return String ApplicationBroadcastPrefix
     */
    public String getApplicationBroadcastPrefix() {
        return mApplicationBroadcastPrefix;
    }

    public void setApplicationBroadcastPrefix(String applicationBroadcastPrefix) {
        mApplicationBroadcastPrefix = applicationBroadcastPrefix;
    }

    /**
     * Get Application Name Prefix of an interest
     *
     * @return String ApplicationNamePrefix
     */
    public String getmApplicationNamePrefix() {
        return mApplicationNamePrefix;
    }

    /**
     * Get Face
     *
     * @return Face Object
     */
    public Face getFace() {
        return mFace;
    }

    /**
     * Get status of the activity
     *
     * @return boolean
     */
    public boolean getAtivityStop() {
        return mActivityStop;
    }

    /**
     * Get th UUID of the application
     *
     * @return String UUID
     */
    public String getUUID() {
        return mUUID;
    }

    /**
     * Set UUID of the application.
     *
     * @param UUID UUID of the application.
     */
    public void setUUID(String UUID) {
        mUUID = UUID;
    }
}
