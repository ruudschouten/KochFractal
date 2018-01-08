package eu.blappole.ui;

public class FilenameGetter {
    public static final String FILEPATH = System.getProperty("user.dir");
    public static final String FILEPREFIX = "Koch";
    public static final String FILETYPE = ".kch";

    public static final String FILENAME = FILEPREFIX + FILETYPE;
    public static final String REALTIMEPATH = FILEPATH + "/" + FILENAME;
    public static final String SYNCPATH = FILEPATH + "/" + FILEPREFIX + "Sync" + FILETYPE;
}
