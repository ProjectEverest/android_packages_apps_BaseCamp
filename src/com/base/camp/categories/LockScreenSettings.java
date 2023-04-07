/*
 * Copyright (C) 2023 The EverestOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.base.camp.categories;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.everest.support.preferences.SecureSettingSwitchPreference;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
        
     private static final String CATEGORY_AMBIENT = "ambient_display";

    private static final String BLUR_RADIUS_KEY = "ls_media_filter_blur_radius";

    Preference mBlurRadiusPref;

     private static final String LOCKSCREEN_DOUBLE_LINE_CLOCK = "lockscreen_double_line_clock_switch";

    	private SwitchPreference mKGCustomClockColor;
    	private SecureSettingSwitchPreference mDoubleLineClock;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.base_camp_lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();
        Resources resources = getResources();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final PreferenceCategory ambientCat = (PreferenceCategory) prefScreen.findPreference(CATEGORY_AMBIENT);
        if (TextUtils.isEmpty(getResources().getString(com.android.internal.R.string.config_dozeDoubleTapSensorType)) &&
                TextUtils.isEmpty(getResources().getString(com.android.internal.R.string.config_dozeTapSensorType))) {
            prefScreen.removePreference(ambientCat);
        }

        mDoubleLineClock = (SecureSettingSwitchPreference ) findPreference(LOCKSCREEN_DOUBLE_LINE_CLOCK);
        mDoubleLineClock.setChecked((Settings.Secure.getInt(getContentResolver(),
             Settings.Secure.LOCKSCREEN_USE_DOUBLE_LINE_CLOCK, 1) != 0));
        mDoubleLineClock.setOnPreferenceChangeListener(this);

        mBlurRadiusPref = (Preference) findPreference(
                        BLUR_RADIUS_KEY);

        updateAlbumArtPref();
    }

    private void updateAlbumArtPref() {
        boolean gradientBlurFilterEnabled = Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_ALBUMART_FILTER, 0, UserHandle.USER_CURRENT) == 5;
        boolean blurFilterEnabled = Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_ALBUMART_FILTER, 0, UserHandle.USER_CURRENT) >= 3 && !gradientBlurFilterEnabled;
        mBlurRadiusPref.setEnabled(blurFilterEnabled);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

            if (preference == mDoubleLineClock) {
        	boolean value = (Boolean) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.LOCKSCREEN_USE_DOUBLE_LINE_CLOCK, value ? 1 : 0);
            return true; 
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.EVEREST;
    }

}
