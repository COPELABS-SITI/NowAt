package pt.ulusofona.copelabs.now.models;
/**
 * User class is part of Now@ application. It provides support to manage information about user.
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 * @author Omar Aponte (COPELABS/ULHT)
 */

public class User {

    private String mName;

    /**
     * Constructor method of User class.
     * @param name Name of the user.
     */
    public User(String name){
        mName=name;
    }

    /**
     * Get name of user
     * @return user name string
     */
    public String getName(){
        return mName;
    }

    /**
     * Set name of user
     * @param name user name string
     */
    public void setName (String name){
        mName=name;
    }
}
