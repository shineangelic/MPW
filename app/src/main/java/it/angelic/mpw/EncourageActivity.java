package it.angelic.mpw;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class EncourageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encourage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Button copy = findViewById(R.id.buttonCopy);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(android.content.Intent.ACTION_SEND);
                email.setType("application/octet-stream");

                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"shineangelic@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Mining Pool Watcher issue/request");
                startActivity(Intent.createChooser(email, "Send Email"));
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("NoobPool Client", "0xbba4e04fe3692ae8ddc8599a65f64cdc00606a13");
                clipboard.setPrimaryClip(clip);
                Snackbar.make(view, "Developer ETH wallet copied to clipboard", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //ADS
        AdView mAdView = findViewById(R.id.adViewEncourage);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
