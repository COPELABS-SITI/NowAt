/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 4/3/18 2:35 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.ndn;

/**
 * Created by copelabs on 03/04/2018.
 */

public abstract class NameManager {

    /**
     * Application name used to integrate prefixes.
     */
    public static final String APPLICATION_NAME = "/now";

    /**
     * Application name used to integrate prefixes.
     */
    public static final String APPLICATION_STRATEGY = "/broadcast";

    /**
     * General Prefix name.
     */
    public static final String GENERAL_PREFIX = "/ndn";


    public static String generateApplicationDataprefix(String interest, String identifier){
        return GENERAL_PREFIX+APPLICATION_STRATEGY+"/"+interest+"/"+identifier;
    }

    public static String generateApplicationBroadcastPrefix(String interest){
        return GENERAL_PREFIX+APPLICATION_STRATEGY+APPLICATION_NAME+"/"+interest;
    }


}
