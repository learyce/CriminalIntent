package com.cbleary.criminalintent.database;

/**
 * Created by cbleary on 4/7/16.
 */
public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "CRIMES"; //Define the name of the table. Since it's used to hold crimes....

        //Define Column Names
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
