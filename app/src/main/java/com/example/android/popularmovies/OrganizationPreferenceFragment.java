package com.example.android.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by BOX on 9/11/2016.
 */
public class OrganizationPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.organization_preference);
    }
}
