package pt.ulusofona.copelabs.now.models;
/**
 * Message class is part of Now@ application. It provides support to manage information about Message.
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

public class Message {
    private String mUser;
    private String mMessage;
    private String mInterest;
    private String mDate;

    /**
     * Constuctor of Message class.
     *
     * @param mUser User name
     * @param mMessage Content of the message
     * @param mInterest Interest of the message
     * @param mDate Data when was created
     */
    public Message(String mUser, String mMessage, String mInterest, String mDate){
        this.mUser = mUser;
        this.mMessage = mMessage;
        this.mInterest = mInterest;
        this.mDate=mDate;

    }

    /**
     * Get user name
     * @return User name string
     */
    public String getmUser(){
        return mUser;
    }

    /**
     * Get Message content
     * @return message content string
     */
    public String getmMessage(){
        return mMessage;
    }

    /**
     * Get interest of the message
     * @return interest of the message string
     */
    public String getmInterest(){
        return mInterest;
    }

    /**
     * Get Date when was created the message
     * @return date of the message string
     */
    public String getmDate(){
        return mDate;
    }
}
