/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 4/3/18 5:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.ndn;

import java.util.HashMap;

/**
 * This class is used to  control the ChronoSync creates in the application. Every time that a user
 * selects a category a new Instance of ChronoSync is created and is saved in this class.
 *
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:05 PM18.
 */

public abstract class ChronoSyncManager {

    /**
     * HashMap used to save the objects.
     */
    private static HashMap<String, ChronoSync> map = new HashMap<>();

    /**
     * This fuction register a new ChronoSync.
     *
     * @param category   Category selected.
     * @param chronoSync ChronoSync object.
     */
    public static void registerChronoSync(String category, ChronoSync chronoSync) {
        map.put(category, chronoSync);
    }

    /**
     * This function is used to get a specific chronoSync object.
     *
     * @param category Category selcted.
     * @return ChronoSync Object.
     */
    public static ChronoSync getChronoSync(String category) {
        return map.get(category);
    }
}
