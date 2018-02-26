package it.angelic.mpw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.net.URL;
import java.net.URLConnection;

import fr.ganfra.materialspinner.MaterialSpinner;
import it.angelic.mpw.model.CurrencyEnum;
import it.angelic.mpw.model.PoolEnum;
import it.angelic.mpw.model.db.PoolDbHelper;

/**
 * A login screen that offers login via email/password.
 */
public class ChoosePoolActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private TextView mWalletView;
    private View mProgressView;
    private View mLoginFormView;
    private MaterialSpinner poolSpinner;
    private MaterialSpinner currencySpinner;
    private CheckBox skipIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        setContentView(R.layout.activity_choose_pool);

        // Set up the login form.
        mWalletView = findViewById(R.id.wallet);
        // populateAutoComplete();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChoosePoolActivity.this);

        poolSpinner = findViewById(R.id.spinnerPoolChooser);

        skipIntro = findViewById(R.id.skipIntro);
        skipIntro.setChecked(prefs.getBoolean("skipIntro", false));

        if (skipIntro.isChecked()) {
            Intent miner = new Intent(ChoosePoolActivity.this, MainActivity.class);
            startActivity(miner);
            finish();
        }


        //admob
        MobileAds.initialize(this, "ca-app-pub-2379213694485575~9889984422");

        ArrayAdapter poolSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PoolEnum.values());
        poolSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        poolSpinner.setAdapter(poolSpinnerAdapter);


        currencySpinner = findViewById(R.id.spinnerCurrencyChooser);
        ArrayAdapter curAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CurrencyEnum.values());
        curAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(curAdapter);

        poolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ArrayAdapter arra = new ArrayAdapter<>(ChoosePoolActivity.this, android.R.layout.simple_spinner_item,
                        ((PoolEnum) poolSpinner.getAdapter().getItem(position)).getSupportedCurrencies());
               arra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                currencySpinner.setAdapter(arra);
                String prevWallet = prefs.getString("wallet_addr_"
                        + ((PoolEnum) poolSpinner.getAdapter().getItem(position)).name()
                        + "_"
                        + ((CurrencyEnum) currencySpinner.getAdapter().getItem(currencySpinner.getSelectedItemPosition())).name(), "");
                mWalletView.setText(prevWallet);
                Log.i(Constants.TAG, "poolSpinner list: " + ((PoolEnum) poolSpinner.getAdapter().getItem(position)).name());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //read saved wallet from pref
                String xCode = "wallet_addr_"
                        + ((PoolEnum) poolSpinner.getItemAtPosition(poolSpinner.getSelectedItemPosition())).name()
                        + "_"
                        + ((CurrencyEnum) currencySpinner.getAdapter().getItem(position)).name();
                String prevWallet = prefs.getString(xCode, "");
                mWalletView.setText(prevWallet.length() == 0 ? getString(R.string.no_wallet_set) : prevWallet);
                Log.i(Constants.TAG, "currencySpinner list: " + currencySpinner.getItemAtPosition(currencySpinner.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //convoluto
       /* mWalletView.setText(prefs.getString("wallet_addr_"
                + ((PoolEnum) poolSpinner.getSelectedItem()).name()
                + "_"
                + ((CurrencyEnum) currencySpinner.getSelectedItem()).name(), ""));
        */
        restoreLastSettings(prefs);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //ADS
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * BEWARE listeners!! your souls shall be called in your absence as well
     *
     * @param prefs
     */
    private void restoreLastSettings(SharedPreferences prefs) {

        String prevPool = prefs.getString("poolEnum", "");
        final String prevCur = prefs.getString("curEnum", "");
        Log.i(Constants.TAG, "Restoring previous pool: " + prevPool + "Restoring previous currency: " + prevCur);
        try {//cur dipende da pool, quindi tutto o nulla
            for (int u = 0; u < poolSpinner.getAdapter().getCount(); u++) {
                if (prevPool.equalsIgnoreCase(((PoolEnum) poolSpinner.getItemAtPosition(u)).name())) {
                    poolSpinner.setSelection(u);
                    currencySpinner.setSelection(0);
                    Log.i(Constants.TAG, "Restoring previous pool: " + poolSpinner.getSelectedItem());
                    break;
                }
            }
            poolSpinner.invalidate();
            //take a breath THEN re-set currency
            //currency reneed DELAYED adapter
            poolSpinner.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PoolEnum pp = (PoolEnum) poolSpinner.getItemAtPosition(poolSpinner.getSelectedItemPosition());
                    if (pp != null) {
                        ArrayAdapter arra = new ArrayAdapter<>(ChoosePoolActivity.this, android.R.layout.simple_spinner_item, pp.getSupportedCurrencies());
                        arra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        currencySpinner.setAdapter(arra);
                        currencySpinner.invalidate();

                        for (int u = 0; u < currencySpinner.getAdapter().getCount(); u++) {
                            if (prevCur.equalsIgnoreCase(((CurrencyEnum) currencySpinner.getItemAtPosition(u)).name())) {
                                currencySpinner.setSelection(u);
                                Log.i(Constants.TAG, "Restoring previous cur: " + currencySpinner.getSelectedItem());
                            }
                        }
                    }
                }
            }, 1000);

        } catch (Exception ce) {
            Log.e(Constants.TAG, "Could not restore pool settings: " + ce.getMessage());
        }

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mWalletView.setError(null);

        // Store values at the time of the login attempt.
        String email = mWalletView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
       /* if (!isWalletValid(email)) {
            mWalletView.setError(getString(R.string.error_invalid_email));
            focusView = mWalletView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            //focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email,
                    (PoolEnum) poolSpinner.getItemAtPosition(poolSpinner.getSelectedItemPosition()),
                    (CurrencyEnum) currencySpinner.getItemAtPosition(currencySpinner.getSelectedItemPosition()));
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isWalletValid(String email) {
        //se non va bene son cazzi delle preference
        return email.contains("0x");
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mWalletAddr;
        private final PoolEnum mPool;
        private final CurrencyEnum mCur;
        private boolean connectError = false;

        UserLoginTask(String email, PoolEnum pool, CurrencyEnum cur) {
            mWalletAddr = email;
            mPool = pool;
            mCur = cur;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChoosePoolActivity.this);
                prefs.edit().putString("poolEnum", mPool.name()).apply();
                prefs.edit().putString("curEnum", mCur.name()).apply();
                prefs.edit().putBoolean("skipIntro", skipIntro.isChecked()).apply();
                Log.w(Constants.TAG, "SAVED  pool: " + mPool.name() + " currency: " + mCur.name());
                //wallet can be empty, changed in preference
                //retrocompatibility
                if (mWalletAddr != null && mWalletAddr.length() > 0 && !mWalletAddr.equalsIgnoreCase(getString(R.string.no_wallet_set)))
                    prefs.edit().putString("wallet_addr", mWalletAddr).commit();
                else//remove &let user choose
                    prefs.edit().remove("wallet_addr").commit();


            } catch (Exception e) {
                Log.e(Constants.TAG, "ERROR writing base pref", e);
                return false;
            }

            //la chiusa del DB serve
            try {
                PoolDbHelper mDbHelper = new PoolDbHelper(ChoosePoolActivity.this, mPool, mCur);
                mDbHelper.cleanOldData(mDbHelper.getWritableDatabase());
                mDbHelper.close();
            } catch (Exception e) {
                Log.e(Constants.TAG, "ERROR cleaning/DB operation: ", e);
            }

            try {
                URL myUrl = new URL(Utils.getHomeStatsURL(PreferenceManager.getDefaultSharedPreferences(ChoosePoolActivity.this)));
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(2000);
                connection.connect();

            } catch (Exception e) {
                connectError = true;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent miner = new Intent(ChoosePoolActivity.this, MainActivity.class);
                startActivity(miner);
                finish();
            } else if (connectError) {
                //TODO show err
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

