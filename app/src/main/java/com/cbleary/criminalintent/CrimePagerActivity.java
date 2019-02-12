package com.cbleary.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cbleary on 3/25/16.
 */
public class CrimePagerActivity extends AppCompatActivity  implements CrimeFragment.Callbacks{
    private CrimeLab mCrimeLab;
    private static final String EXTRA_CRIME_ID = "com.cbleary.criminalintent.crime_id_extra";

    @Bind(R.id.activity_crime_pager_viewpager) ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        ButterKnife.bind(this);
        mCrimeLab = mCrimeLab.getCrimeLab(this);

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime c = mCrimeLab.getCrimes().get(position);
                return CrimeFragment.newInstance(c.getId());
            }

            @Override
            public int getCount() {
                return mCrimeLab.getCrimes().size();
            }
        });

        //Grab CrimeID and set current item to specific crime.
        UUID crimeID = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int positionOfCrime = mCrimeLab.getPosition(crimeID);

        if(positionOfCrime > 0){
            mViewPager.setCurrentItem(positionOfCrime);
        }

    }

    //Method for other activities to create intent for this activity
    public static Intent newIntent(Context contextPackage, UUID crimeID){
        Intent intent = new Intent(contextPackage, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        return intent;
    }

    //Required by Crime Fragment.  However, no implementation needed for the pager.
    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
