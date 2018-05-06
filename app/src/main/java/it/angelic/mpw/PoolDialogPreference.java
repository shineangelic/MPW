package it.angelic.mpw;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * che farloccata, ma DialogPref e` astratta..
 */
public class PoolDialogPreference extends DialogPreference {

    public PoolDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    public PoolDialogPreference(Context context) {
        this(context, null);
    }

    public PoolDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public PoolDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



}