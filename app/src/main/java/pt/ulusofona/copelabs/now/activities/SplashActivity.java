package pt.ulusofona.copelabs.now.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
/**
 * Splash Activity class is part of Now@ application. This is used to show initial information
 * about Now@ when it is started
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/14/17 2:29 PM
 *
 * @author Omar Aponte (COPELABS/ULHT)
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, NowMainActivity.class);
        startActivity(intent);
        finish();
    }
}
