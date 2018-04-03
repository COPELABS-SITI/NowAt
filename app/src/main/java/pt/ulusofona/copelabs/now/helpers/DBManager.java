/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 9/18/17 10:50 AM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.ulusofona.copelabs.now.interfaces.NowMainActivityInterface;
import pt.ulusofona.copelabs.now.models.Message;
import pt.ulusofona.copelabs.now.task.RequestData;
import pt.ulusofona.copelabs.now.task.SaveDataTask;

/**
 * This class contains the function used to operate inside of the database.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 */

public class DBManager {

    /**
     * Variable used for debug.
     */
    private static final String TAG = DBManager.class.getSimpleName();

    /**
     * Name of the dataBase.
     */
    private static final String DB_NAME = "Nowmessages";

    /**
     * Vesion of the dataBase.
     */
    private static final int DB_VERSION = 1;

    private List<Message> mMessages;

    /**
     * Context of the application.
     */
    private Context mContext;

    /**
     * Interface used to communicate with the main activity.
     */
    private NowMainActivityInterface mInterface;

    /**
     * Constructor of the DBManager.
     *
     * @param context               Context of the application.
     * @param mainActivityInterface interface to communicate with the main activity.
     */
    public DBManager(Context context, NowMainActivityInterface mainActivityInterface) {
        mContext = context;
        mInterface = mainActivityInterface;
    }

    /**
     * This method deletes all the message saved in the dataBase.
     */
    public void deleteAll() {
        MessagesSQLiteHelper messagesSQLiteHelper =
                new MessagesSQLiteHelper(mContext, DB_NAME, null, DB_VERSION);
        SQLiteDatabase db = messagesSQLiteHelper.getWritableDatabase();

        if (db != null) {
            db.execSQL("DELETE FROM Messages");
        }
        db.close();

    }

    /**
     * This method saves messages to the dataBase.
     *
     * @param messages List which contains messages.
     */
    public void saveData(List<Message> messages) {

        MessagesSQLiteHelper messagesSQLiteHelper =
                new MessagesSQLiteHelper(mContext, DB_NAME, null, DB_VERSION);
        new SaveDataTask(messagesSQLiteHelper, messages).execute();
    }


    /**
     * This method gets every message saved in the dataBase.
     *
     * @return List with all the information of the messages saved.
     */
    public List getData() {
        ArrayList<Message> messages = new ArrayList<>();

        MessagesSQLiteHelper messagesSQLiteHelper =
                new MessagesSQLiteHelper(mContext, DB_NAME, null, DB_VERSION);
        SQLiteDatabase db = messagesSQLiteHelper.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery(" SELECT * FROM Messages", null);
            if (c.moveToFirst()) {
                do {
                    Message message = new Message(c.getString(0), c.getString(2), c.getString(1), c.getString(3), c.getString(4));
                    message.setSave(true);
                    messages.add(message);
                    Log.d(TAG, c.getString(0));
                } while (c.moveToNext());
            }
            c.close();
        }
        return messages;
    }

    /**
     * This method is used to request data every time that the application is resume.
     */
    public void requestData() {
        MessagesSQLiteHelper messagesSQLiteHelper =
                new MessagesSQLiteHelper(mContext, DB_NAME, null, DB_VERSION);
        new RequestData(messagesSQLiteHelper, mInterface).execute();
    }

}
