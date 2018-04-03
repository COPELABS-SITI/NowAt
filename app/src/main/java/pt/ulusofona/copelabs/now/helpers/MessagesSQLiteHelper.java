/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 9/17/17 8:43 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This Class exteds to SQLiteOpenHelper class, an is used to configure the dataBase of the
 * application.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 */

public class MessagesSQLiteHelper extends SQLiteOpenHelper {

    /**
     * Create table script.
     */
    private static final String sqlCreate = "CREATE TABLE Messages (user TEXT, interest TEXT, content TEXT, data TEXT, id TEXT)";

    /**
     * Drop table script.
     */
    private static final String dropTable = "DROP TABLE IF EXISTS Messages";

    /**
     * Constructor of the MessageSQLiteHelper class.
     *
     * @param context Context of the application.
     * @param name    name of the data Base.
     * @param factory
     * @param version version of dataBase.
     */
    public MessagesSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropTable);

        db.execSQL(sqlCreate);
    }
}
