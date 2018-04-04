/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 9/21/17 2:42 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import pt.ulusofona.copelabs.now.interfaces.NowMainActivityInterface;

/**
 * This class is an AsyncTask class, used to divide a file in peace that can be sent using NDN.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 */

public class SegmentationTask extends AsyncTask<Void, Void, String[]> {

    /**
     * Variable used for debug.
     */
    private final static String TAG = SegmentationTask.class.getSimpleName();

    /**
     * Interface used to notify when the task ends.
     */
    private NowMainActivityInterface mInterface;

    /**
     * Bitmap contains the image to be sent.
     */
    private Bitmap mImageBitmap;

    /**
     * Size of the file to be sent.
     */
    private int mSize;

    /**
     * Name of the file to be sent.
     */
    private String mFileName;

    /**
     * Dialog used to show the progress of the task.
     */
    private ProgressDialog dialog;

    /**
     * Context of the application.
     */
    private Context mContext;

    public SegmentationTask(NowMainActivityInterface nowMainActivityInterface, Bitmap imageBitmap, String fileName, Context context) {
        mImageBitmap = imageBitmap;
        mInterface = nowMainActivityInterface;
        mFileName = fileName;
        mContext = context;
    }

    /**
     * This method displays the dialog used to show the progress of the task.
     */
    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Preparing to send file...");
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
    }

    /**
     * This method divide the file in small sections.
     *
     * @param params
     * @return Array with all the sections of the file.
     */
    @Override
    protected String[] doInBackground(Void... params) {
        String array[];
        String stringFile = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mImageBitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
        byte[] b = baos.toByteArray();
        byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            stringFile = new String(encodedImage, StandardCharsets.UTF_8);
        }
        Log.d(TAG, stringFile);
        double maxsize = 3000;

        int fileSize = stringFile.length();
        Log.d(TAG, "Data: " + fileSize);
        double size = fileSize / maxsize;
        Log.d(TAG, size + "");
        mSize = fileSize;
        double ss = Math.ceil(size);

        Log.d(TAG, "Número : " + ss);

        array = new String[(int) ss];
        String aux = "";

        int control = 0;
        int j = 0;
        for (int i = 0; i < fileSize; i++) {
            aux = aux + stringFile.charAt(i);
            control++;
            if (control >= maxsize) {
                array[j] = aux;
                aux = "";
                control = 0;
                j++;
            }
        }

        array[j++] = aux;
        return array;
    }

    /**
     * When the file is finally divide, a new task is called is order to perform the action of send
     * each section.
     *
     * @param data Array with all the section of the file.
     */
    @Override
    protected void onPostExecute(String[] data) {
        new Send(data, mSize, 0, mFileName, data.length, 0).execute();
    }

    /**
     * This is class is used to send each section of the file.
     */
    class Send extends AsyncTask<Void, Void, Void> {

        /**
         * Data to be sent.
         */
        private String[] mData;
        /**
         * Size of the file to be sent.
         */
        private int mSize;
        /**
         * Section to be sent.
         */
        private int mSection;
        /**
         * Name of the file.
         */
        private String mName;
        /**
         * Total of sections of the file.
         */
        private int mTotal;
        /**
         * Counter used to identify the section already sent.
         */
        private int mSectionSent = 0;


        public Send(String[] data, int size, int section, String name, int total, int sectionSent) {
            mData = data;
            mSize = size;
            mSection = section;
            mName = name;
            mTotal = total;
            mSectionSent = sectionSent;
        }

        /**
         * This method performs a sleep timer with the intention of no overload the ChronoSyncManager.
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // thread to sleep for 1000 milliseconds
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }

        /**
         * When the timer finishes, the interface is called an is passed the information to be sent.
         * @param v
         */
        @Override
        protected void onPostExecute(Void v) {
            mInterface.segmentationResult(mData[mSectionSent], mTotal, mSection, mName);
            mSectionSent++;
            if (mSectionSent < mTotal) {
                dialog.setMessage("Sending... " + (mSectionSent * 100) / mTotal + "%");
                new Send(mData, mSize, mSectionSent, mFileName, mData.length, mSectionSent).execute();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

        }
    }

}
