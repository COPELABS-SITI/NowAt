/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 4/3/18 11:48 AM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.interfaces;


import java.util.List;

import pt.ulusofona.copelabs.now.models.Message;

/**
 * Interface implemented by the NowMainActivity, this is used to comunicate whit other components
 * of the application.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 */
public interface NowMainActivityInterface {
    /**
     * Notify when a interest is selected or deselected from the interest bar.
     *
     * @param interest
     */
    void updateValueSelected(String interest);

    /**
     * This method is used to notify when a message is selected.
     *
     * @param message message selected.
     */
    void messageSelected(int message);

    /**
     * This method is used to notify about the message saved in the data base.
     *
     * @param messages List of messages saved in the data base.
     */
    void getDataRequested(List<Message> messages);

    /**
     * Notify when a section of a file is ready to be sent.
     *
     * @param data             Data to be sent.
     * @param fileCompleteFile Size of the entire file.
     * @param section          Section of the data.
     * @param fileName         Name of the file.
     */
    void segmentationResult(String data, int fileCompleteFile, int section, String fileName);
}
