/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.models;


public class Message {
    private String mUser;
    private String mMessage;
    private String mInterest;
    private String mDate;

    public Message(String mUser, String mMessage, String mInterest, String mDate){
        this.mUser = mUser;
        this.mMessage = mMessage;
        this.mInterest = mInterest;
        this.mDate=mDate;

    }
    public String getmUser(){
        return mUser;
    }

    public String getmMessage(){
        return mMessage;
    }

    public String getmInterest(){
        return mInterest;
    }

    public String getmDate(){
        return mDate;
    }
}
