/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 9/21/17 1:07 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import pt.ulusofona.copelabs.now.helpers.MessagesSQLiteHelper;
import pt.ulusofona.copelabs.now.models.Message;

/**
 * SaveDataTask is an AsyncTask class, used to save information into the database.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 */

public class SaveDataTask extends AsyncTask<Void, Void, Boolean> {

    /**
     * Used for debug.
     */
    private final static String TAG = SaveDataTask.class.getSimpleName();

    /**
     * Boolean that represents the status of the action.
     */
    private Boolean mStatus = false;
    /**
     * Helper which contains the information of the database.
     */
    private MessagesSQLiteHelper mMessagesSQLiteHelper;
    /**
     * List of message to be saved.
     */
    private List<Message> messages;

    /**
     * Consturctor of the SaveDataTask class.
     *
     * @param messagesSQLiteHelper Helper of the data base.
     * @param messages             List of message.
     */
    public SaveDataTask(MessagesSQLiteHelper messagesSQLiteHelper, List<Message> messages) {
        mMessagesSQLiteHelper = messagesSQLiteHelper;
        this.messages = messages;

    }

    /**
     * This method performs the action of save the messages in the data base.
     *
     * @param params
     * @return Boolean that represents the status of the action.
     */
    @Override
    protected Boolean doInBackground(Void... params) {

        SQLiteDatabase db = mMessagesSQLiteHelper.getWritableDatabase();

        if (db != null) {
            db.execSQL("DELETE FROM Messages");

            for (int i = 0; i < messages.size(); i++) {

                if (messages.get(i).getSave()) {
                    db.execSQL("INSERT INTO Messages (user,interest,content,data,id) " +
                            "VALUES ('" + messages.get(i).getmUser() + "','"
                            + messages.get(i).getmInterest() + "','"
                            + messages.get(i).getmMessage() + "','"
                            + messages.get(i).getmDate() + "','"
                            + messages.get(i).getmID() + "')");
                    Log.d(TAG, "INSERT INTO Messages (user,interest,content,data,id) " +
                            "VALUES ('" + messages.get(i).getmUser() + "','"
                            + messages.get(i).getmInterest() + "','"
                            + messages.get(i).getmMessage() + "','"
                            + messages.get(i).getmDate() + "','"
                            + messages.get(i).getmID() + "')");
                }
            }
            db.close();
            mStatus = true;
        } else {
            mStatus = false;
        }
        return mStatus;
    }

    /**
     * When after the action is performed, this method is call and is used to know if the data was
     * successfully saved or not.
     *
     * @param mStatus
     */
    @Override
    protected void onPostExecute(Boolean mStatus) {
        if (mStatus) {
            Log.d(TAG, "Data Saved");
        } else {

            Log.d(TAG, "Data not saved");


        }
    }
}
