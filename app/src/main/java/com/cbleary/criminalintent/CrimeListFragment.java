package com.cbleary.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cbleary on 3/23/16.
 */
public class CrimeListFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private CrimeAdapter mAdapter;
    private int mCrimeUpdated;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    @Bind(R.id.crime_recycler_view) RecyclerView mCrimeRecyclerView;

    @Nullable
    @Override
    //Because CrimeListFragment extends generic Fragment which doesn't have default layout
    // We must define and create a view manually rather than using super call.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        ButterKnife.bind(this, v);
        //The Layout Manager defines out views are position and how scrolling behavior works.
        //MUST SET FOR RECYCLERVIEW
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // Returns the fragmentActivity currently associated with this fragment

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        
        updateUI();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);//Leave the super call to preserve any implementation defined by the super class
        inflater.inflate(R.menu.fragment_crime_list, menu);

        //Performed on creation to preserve change across screen rotation
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubtitleVisible = false;
        setHasOptionsMenu(true); //Tell FragmentManager to expect callback
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                //Create a new Crime and Launch it's information page
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).newCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);

                return true;
            case  R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();//Recreate the  toolbar
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    public void updateUI(){
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if(!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public class CrimeHolder extends RecyclerView.ViewHolder  {
        @Bind(R.id.list_item_crime_title_textview) TextView mTitleTextView; //Remember to be specific when naming member variables
        @Bind(R.id.list_item_crime_date_textview) TextView mDateTextView;
        @Bind(R.id.list_item_crime_solved_checkbox) CheckBox mSolvedCheckBox;
        private Crime mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onCrimeSelected(mCrime);

                }
            });
            mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCrime.setSolved(isChecked);
                    CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
                }
            });
        }
        public void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDateString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }
    }

    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        public List<Crime> mCrimeList;

        public CrimeAdapter(List<Crime> crimes){
            mCrimeList = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(v);
        }

        public void setCrimes(List<Crime> crimes){
            mCrimeList = crimes;
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimeList.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimeList.size();
        }
    }

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) getActivity();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

}
