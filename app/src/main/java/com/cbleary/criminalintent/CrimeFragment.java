package com.cbleary.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cbleary on 3/14/16.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = CrimeFragment.class.getName();

    private static final String ARGS_CRIME_ID = "com.cbleary.criminalintent.crime_id_argument";
    private static final String DIALOG_DATE = "com.cbleary.criminalintent.dialog_date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    @Bind(R.id.crime_title) EditText mTitleField;
    @Bind(R.id.crime_date) Button mDateButton;
    @Bind(R.id.crime_solved) CheckBox mSolved;
    @Bind(R.id.crime_suspect) Button mSuspectButton;
    @Bind(R.id.crime_report) Button mReportButton;
    @Bind(R.id.crime_photo) ImageView mCrimePhotoView;
    @Bind(R.id.crime_camera)  ImageButton mCameraButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARGS_CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true); //Tell FragmentManager to expect callback
        mPhotoFile = CrimeLab.getCrimeLab(getActivity()).getPhotoFile(mCrime);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);//Leave the super call to preserve any implementation defined by the super class
        inflater.inflate(R.menu.fragment_crime, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                //Create a new Crime and Launch it's information page
                CrimeLab.getCrimeLab(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    //Because CrimeFragment extends the Generic Fragment Class, it doesn't have a layout defined by
    //default.  Therefore, it's necessary to define and return a view manually
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        ButterKnife.bind(this, v);

        //Handle EditText Events
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Leave blank.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());
                datePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });

        mSolved.setChecked(mCrime.isSolved());
        mSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        //Need to ensure that their is a valid contacts activity for suspect button to call
        PackageManager pm = getActivity().getPackageManager();
        if(pm.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            //if no activity exists on OS, disable suspect button. Not a pretty solution, but the
            //cases where this happens are infrequent enough that this is okay.
            mSuspectButton.setEnabled(false);
        }

        //IMAGE CONTENT
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // FINAL -> b/c accessed within inner class
        boolean canTakePhoto = (mPhotoFile != null && captureImage.resolveActivity(pm) != null);
        mCameraButton.setEnabled(canTakePhoto);

        if(canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        updatePhotoView();


        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) getActivity();
    }

    private void updateCrime(){
        CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARGS_CRIME_ID, crimeId);
        CrimeFragment crimeFragment =  new CrimeFragment();

        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case REQUEST_DATE :
                if(data != null) {
                    mCrime.setDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));
                    updateDate();
                }
                break;
            case REQUEST_CONTACT:
                if(data == null){
                    return;
                }
                Uri contactUri = data.getData();
                //Specify which fields to return values of
                String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
                //perform query (essentially a "where")
                Cursor c = getActivity().getContentResolver()
                        .query(contactUri, queryFields, null, null, null);

                try {
                    //Base case check
                    if(c.getCount() == 0)
                        return;

                    //Only one name and one column will be returned
                    c.moveToFirst();
                    String suspect = c.getString(0);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);
                } catch (Error e) {
                    Log.e(TAG, "onActivityResult: ", e );
                } finally{
                    c.close();
                }
                break;
            case REQUEST_PHOTO:
                updatePhotoView();
                break;
            default:
                return;
        }
        updateCrime(); //Only need it in one place.  If not resultcode -> okay return. if default: returnb
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDateString());
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String date = mCrime.getDateString();

        String suspect = mCrime.getSuspect();

        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), date, solvedString, suspect);
        return report;
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mCrimePhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mCrimePhotoView.setImageBitmap(bitmap);
        }
    }

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }
}
