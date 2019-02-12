package com.cbleary.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.cbleary.criminalintent.CrimeListFragment.Callbacks;

/**
 * Created by cbleary on 3/23/16.
 */
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    //Must implement to handle call back from crimelistfragment.  Used when screen size is large
    //enough to support multiple fragments at once.
    @Override
    public void onCrimeSelected(Crime crime) {
        //If null, then it must be the single fragment view instantiated
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else{
            //Two fragment layout
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            //Grab the detail fragment contain and replace it with a crime fragment.
            //Unforunately, we lose the pager abilities doing it this way.  However,
            //user still have ability to rapidly select different crimes using the
            //CrimeListFragment.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    //Called by Crime Fragment to update UI on CrimeListFragment.
    //Only needed for tablets.  If slow, consider check to prevent update on phone layout.
    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                                                    .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
