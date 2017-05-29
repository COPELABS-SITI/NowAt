package pt.ulusofona.copelabs.now.models;

/**
 * Created by copelabs on 03/04/2017.
 */

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
