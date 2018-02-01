package it.angelic.mpw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import it.angelic.mpw.model.CurrencyEnum;
import it.angelic.mpw.model.PoolEnum;

/**
 * A login screen that offers login via email/password.
 */
public class ChoosePoolActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mWalletView;
    private View mProgressView;
    private View mLoginFormView;
    private Spinner poolSpinner;
    private Spinner currencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pool);
        // Set up the login form.
        mWalletView = (AutoCompleteTextView) findViewById(R.id.wallet);
        // populateAutoComplete();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChoosePoolActivity.this);

        poolSpinner = (Spinner) findViewById(R.id.spinnerPoolChooser);
        poolSpinner.setAdapter(new ArrayAdapter<PoolEnum>(this, android.R.layout.simple_spinner_item, PoolEnum.values()));

        currencySpinner = (Spinner) findViewById(R.id.spinnerCurrencyChooser);
        currencySpinner.setAdapter(new ArrayAdapter<CurrencyEnum>(this, android.R.layout.simple_spinner_item, CurrencyEnum.values()));

        poolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currencySpinner.setAdapter(new ArrayAdapter<CurrencyEnum>(ChoosePoolActivity.this, android.R.layout.simple_spinner_item, PoolEnum.values()[position].getSupportedCurrencies()));
                String prevWallet = prefs.getString("wallet_addr_" + ((PoolEnum) poolSpinner.getAdapter().getItem(position)).name() + "_" + ((CurrencyEnum) currencySpinner.getSelectedItem()).name(), "");
                mWalletView.setText(prevWallet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String xCode = "wallet_addr_" + ((PoolEnum) poolSpinner.getSelectedItem()).name() + "_" + ((CurrencyEnum) currencySpinner.getAdapter().getItem(position)).name();
                String prevWallet = prefs.getString(xCode, "");
                mWalletView.setText(prevWallet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //convoluto
        mWalletView.setText(prefs.getString("wallet_addr_"
                + ((PoolEnum) poolSpinner.getSelectedItem()).name()
                + "_"
                + ((CurrencyEnum) currencySpinner.getSelectedItem()).name(), ""));

        resetLastSettings(prefs);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void resetLastSettings(SharedPreferences prefs) {

        String prevPool = prefs.getString("poolEnum", "");
        String prevCur = prefs.getString("curEnum", "");

        int prevPoolIdx = 0;
        for (int u = 0; u < poolSpinner.getAdapter().getCount(); u++) {
            if (prevPool.equalsIgnoreCase(((PoolEnum) poolSpinner.getItemAtPosition(u)).name()))
                prevPoolIdx = u;
        }

        poolSpinner.setSelection(prevPoolIdx);

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
        if (TextUtils.isEmpty(email)) {
            mWalletView.setError(getString(R.string.error_field_required));
            focusView = mWalletView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mWalletView.setError(getString(R.string.error_invalid_email));
            focusView = mWalletView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, (PoolEnum) poolSpinner.getSelectedItem(), (CurrencyEnum) currencySpinner.getSelectedItem());
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("0x");
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void addWalletsToAutoComplete(List<String> walletsAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(ChoosePoolActivity.this,
                        android.R.layout.simple_dropdown_item_1line, walletsAddressCollection);

        mWalletView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mWalletAddr;
        private final PoolEnum mPool;
        private final CurrencyEnum mCur;

        UserLoginTask(String email, PoolEnum pool, CurrencyEnum cur) {
            mWalletAddr = email;
            mPool = pool;
            mCur = cur;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            //TODO test connessione?
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChoosePoolActivity.this);
                prefs.edit().putString("poolEnum", mPool.name()).commit();
                prefs.edit().putString("curEnum", mCur.name()).commit();
                prefs.edit().putString("wallet_addr_" + mPool.name() + "_" + mCur.name(), mWalletAddr).commit();
                //retrocompatibility
                prefs.edit().putString("wallet_addr", mWalletAddr).commit();
            } catch (Exception e) {
                Log.e(Constants.TAG, "ERROR writing base pref", e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                addWalletsToAutoComplete(new ArrayList<String>() {{
                    add(mWalletAddr);
                }});
                Intent miner = new Intent(ChoosePoolActivity.this, MainActivity.class);
                startActivity(miner);

            } else {
                // mPasswordView.setError(getString(R.string.error_incorrect_password));
                // mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

