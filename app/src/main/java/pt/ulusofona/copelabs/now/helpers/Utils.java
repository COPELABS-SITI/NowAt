package pt.ulusofona.copelabs.now.helpers;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.identity.IdentityManager;
import net.named_data.jndn.security.identity.MemoryIdentityStorage;
import net.named_data.jndn.security.identity.MemoryPrivateKeyStorage;

import java.util.Calendar;
import java.util.List;

/**
 * Created by copelabs on 07/03/2017.
 */

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
     *@return date string
     */
    public static String getDate(){

        Calendar c = Calendar.getInstance();

        int minute = c.get(Calendar.MINUTE);
        int hora = c.get(Calendar.HOUR);
        int second = c.get(Calendar.SECOND);

        return hora+":"+minute+":"+second;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {

        ActivityManager activityManager;
        activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                Log.d("processInfo.processName", processInfo.processName);
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }


        return false;
    }
}
