/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 4/3/18 5:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.ndn;

import java.util.HashMap;

/**
 * Created by copelabs on 03/04/2018.
 */

public class ChronoSyncManager {

    private HashMap<String,ChronoSync> map = new HashMap<>();

    public ChronoSyncManager (){}

    public void registerChronoSync(String category,ChronoSync chronoSync){
        map.put(category,chronoSync);
    }

    public ChronoSync getChronoSync(String category){
        return map.get(category);
    }
}
