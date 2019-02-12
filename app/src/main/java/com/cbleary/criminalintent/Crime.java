package com.cbleary.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by cbleary on 3/13/16.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;


    public String getDateString(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE,  MMMM dd,  yyyy");
        return formatter.format(mDate);
    }

    public Date getDate(){
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Crime(){
        this(UUID.randomUUID()); //Generate random Identifier
    }

    public Crime(UUID uuid) {
        mId = uuid;
        mDate = new Date();
    }
    //Since Id is unique.  Filename will also be unique.
    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}
