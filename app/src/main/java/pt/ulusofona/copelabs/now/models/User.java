/*
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */

package pt.ulusofona.copelabs.now.models;


public class User {

    private String mName;

    public User(String name){
        this.mName=name;
    }

    public String getName(){
        return mName;
    }

    public void setName (String name){
        mName=name;
    }
}
