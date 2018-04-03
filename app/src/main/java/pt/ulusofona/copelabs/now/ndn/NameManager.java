/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 4/3/18 2:35 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.ndn;

/**
 * This class contains all the names used  for tha application, also is used to create the prefixes
 * based on the categories selected by the user.
 * Created by copelabs on 03/04/2018.@author Omar Aponte (COPELABS/ULHT)
 *
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:05 PM
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

    /**
     * This method generates a name using the local information and the infromation of the
     * category selected by the user.
     *
     * @param interest   Interest name selected by the user.
     * @param identifier Identifier of the user.
     * @return ApplicationDataPrefix.
     */
    public static String generateApplicationDataPrefix(String interest, String identifier) {
        return GENERAL_PREFIX + APPLICATION_STRATEGY + "/" + interest + "/" + identifier;
    }

    /**
     * This method is used to generate the ApplicationBroadcastPrefix.
     *
     * @param interest Interest name selected by the user.
     * @return String with the applicationBroadcastPrefix.
     */
    public static String generateApplicationBroadcastPrefix(String interest) {
        return GENERAL_PREFIX + APPLICATION_STRATEGY + APPLICATION_NAME + "/" + interest;
    }


}
