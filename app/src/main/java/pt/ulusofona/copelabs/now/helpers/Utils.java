package pt.ulusofona.copelabs.now.helpers;
/**
 * This class is part of Now@ application. it provides general functions that are used for
 * help specific operations through the application.
 *
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:07 PM
 * @author Omar Aponte (COPELABS/ULHT)
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.identity.IdentityManager;
import net.named_data.jndn.security.identity.MemoryIdentityStorage;
import net.named_data.jndn.security.identity.MemoryPrivateKeyStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public abstract class Utils {

    /**
     * Setup an in-memory KeyChain with a default identity.
     *
     * @return keyChain object
     * @throws net.named_data.jndn.security.SecurityException
     */
    public static KeyChain buildTestKeyChain() throws net.named_data.jndn.security.SecurityException {
        MemoryIdentityStorage identityStorage = new MemoryIdentityStorage();
        MemoryPrivateKeyStorage privateKeyStorage = new MemoryPrivateKeyStorage();
        IdentityManager identityManager = new IdentityManager(identityStorage, privateKeyStorage);
        KeyChain keyChain = new KeyChain(identityManager);
        try {
            keyChain.getDefaultCertificateName();
        } catch (net.named_data.jndn.security.SecurityException e) {
            keyChain.createIdentity(new Name("/test/identity"));
            keyChain.getIdentityManager().setDefaultIdentity(new Name("/test/identity"));
        }
        return keyChain;
    }

    /**
     * Setup a name for the device
     *
     * @return name string
     */
    public static String generateRandomName() {
        String seed = "1234567890";
        int length = 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int random = (int) (Math.random() * seed.length());
            sb.append(seed.charAt(random));
        }
        return sb.toString();
    }

    /**
     * This method provides the date
     *
     * @return date string
     */
    public static String getDate() {

        Calendar c = Calendar.getInstance();

        int minute = c.get(Calendar.MINUTE);
        int hora = c.get(Calendar.HOUR);
        int second = c.get(Calendar.SECOND);

        return hora + ":" + minute + ":" + second;
    }

    /**
     * This method show a Dialog using a listview to display the information.
     *
     * @param listInforation List of information to display.
     * @param context Context of the application.
     */
    public static void showListPrefix(ArrayList<String> listInforation, Context context, String title) {
        String[] information = listInforation.toArray(new String[listInforation.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setItems(information, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        builder.show();
    }

    public static String generateSmallUuid() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
