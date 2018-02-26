package it.angelic.mpw;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * che farloccata, ma DialogPref e` astratta..
 */
public class PoolDialogPreference extends DialogPreference {


    public PoolDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onClick() {
        super.onClick();
    }

}