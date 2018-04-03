/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 9/21/17 1:55 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import pt.ulusofona.copelabs.now.interfaces.NowMainActivityInterface;
import pt.ulusofona.copelabs.now.helpers.MessagesSQLiteHelper;
import pt.ulusofona.copelabs.now.models.Message;

/**
 * This class is an AsyncTAsk used to requeste the data save in the database.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 */

public class RequestData extends AsyncTask<Void, Void, ArrayList<Message>> {
    /**
     * Used for debug.
     */
    private String TAG = RequestData.class.getSimpleName();

    /**
     * Helpers which contains the information about the data base.
     */
    private MessagesSQLiteHelper mMessagesSQLiteHelper;

    /**
     * Interface used to notify the data requested.
     */
    private NowMainActivityInterface mInterface;

    /**
     * RequestData Constructor.
     *
     * @param messagesSQLiteHelper     helper of the data base.
     * @param nowMainActivityInterface interface to notify the data.
     */
    public RequestData(MessagesSQLiteHelper messagesSQLiteHelper, NowMainActivityInterface nowMainActivityInterface) {
        mMessagesSQLiteHelper = messagesSQLiteHelper;
        mInterface = nowMainActivityInterface;
    }

    /**
     * This function selects all data save in the data base.
     *
     * @param params
     * @return message.
     */
    @Override
    protected ArrayList<Message> doInBackground(Void... params) {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = mMessagesSQLiteHelper.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery(" SELECT * FROM Messages", null);
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    Message message = new Message(c.getString(0), c.getString(2), c.getString(1), c.getString(3), c.getString(4));
                    message.setSave(true);
                    messages.add(message);
                    Log.d(TAG, c.getString(0));
                } while (c.moveToNext());
            }
        }
        return messages;
    }

    /**
     * This method is called to notify to the main activity with all messages found in the database.
     *
     * @param messages
     */
    @Override
    protected void onPostExecute(ArrayList<Message> messages) {
        mInterface.getDataRequested(messages);
    }

}
