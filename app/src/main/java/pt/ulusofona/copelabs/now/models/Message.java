package pt.ulusofona.copelabs.now.models;

/**
 * Created by copelabs on 19/01/2017.
 */

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
