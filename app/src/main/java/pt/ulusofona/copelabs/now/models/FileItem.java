package pt.ulusofona.copelabs.now.models;

/**
 * This class provides support to file items.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:08 PM
 */
public class FileItem implements Comparable<FileItem> {

    /**
     * Name of the file.
     */
    private String mName;

    /**
     * Data the contains the file.
     */
    private String mData;
    /**
     * Creation date of the file.
     */
    private String mDate;
    /**
     * Path of the file.
     */
    private String mPath;
    /**
     * String used to identify the type of icon.
     */
    private String mImage;

    /**
     * Constructor of the file item.
     *
     * @param name  Name of the file.
     * @param data  Data the contains the file.
     * @param date  Creation date of the file.
     * @param path  Path of the file.
     * @param image Type of icon.
     */
    public FileItem(String name, String data, String date, String path, String image) {
        mName = name;
        mData = data;
        mDate = date;
        mPath = path;
        mImage = image;

    }

    /**
     * Get file's name.
     *
     * @return String with the name of the file.
     */
    public String getName() {
        return mName;
    }

    /**
     * Get file's data.
     *
     * @return String with the dat of the file.
     */
    public String getData() {
        return mData;
    }

    /**
     * Get file's date.
     *
     * @return String with the date of the file.
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Get file's path.
     *
     * @return String with the path of the file.
     */
    public String getPath() {
        return mPath;
    }

    /**
     * Get image icon.
     *
     * @return String with the type of icon of the image.
     */

    public String getImage() {
        return mImage;
    }

    /**
     * Function used to compare FileItem objects
     *
     * @param o File item.
     * @return
     */

    public int compareTo(FileItem o) {
        if (this.mName != null)
            return this.mName.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
