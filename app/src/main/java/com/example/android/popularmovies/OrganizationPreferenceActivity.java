package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by BOX on 9/11/2016.
 */
public class OrganizationPreferenceActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_preference);

        getFragmentManager().beginTransaction().replace(R.id.org_frame,new OrganizationPreferenceFragment()).commit();
    }
}
