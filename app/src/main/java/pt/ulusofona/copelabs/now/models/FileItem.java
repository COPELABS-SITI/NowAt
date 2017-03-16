package pt.ulusofona.copelabs.now.models;

public class FileItem implements Comparable<FileItem>{
	private String mName;
	private String mData;
	private String mDate;
	private String mPath;
	private String mImage;
	
	public FileItem(String n, String d, String dt, String p, String img)
	{
		mName = n;
		mData = d;
		mDate = dt;
		mPath = p;
		mImage = img;
		
	}
	public String getmName()
	{
		return mName;
	}
	public String getmData()
	{
		return mData;
	}
	public String getmDate()
	{
		return mDate;
	}
	public String getmPath()
	{
		return mPath;
	}
	public String getImage() {
		return mImage;
	}
	
	public int compareTo(FileItem o) {
		if(this.mName != null)
			return this.mName.toLowerCase().compareTo(o.getmName().toLowerCase());
		else 
			throw new IllegalArgumentException();
	}
}
