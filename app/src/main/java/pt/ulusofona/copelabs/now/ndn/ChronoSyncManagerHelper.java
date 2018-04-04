/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 4/3/18 5:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.ndn;

import java.util.HashMap;

/**
 * This class is used to  control the ChronoSyncManager creates in the application. Every time that a user
 * selects a category a new Instance of ChronoSyncManager is created and is saved in this class.
 *
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:05 PM18.
 */

public abstract class ChronoSyncManagerHelper {

    /**
     * HashMap used to save the objects.
     */
    private static HashMap<String, ChronoSyncManager> map = new HashMap<>();

    /**
     * This fuction register a new ChronoSyncManager.
     *
     * @param category   Category selected.
     * @param chronoSyncManager ChronoSyncManager object.
     */
    public static void registerChronoSync(String category, ChronoSyncManager chronoSyncManager) {
        map.put(category, chronoSyncManager);
    }

    /**
     * This function is used to get a specific chronoSync object.
     *
     * @param category Category selcted.
     * @return ChronoSyncManager Object.
     */
    public static ChronoSyncManager getChronoSync(String category) {
        return map.get(category);
    }
}
