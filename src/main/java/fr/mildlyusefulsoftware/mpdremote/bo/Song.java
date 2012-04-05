package fr.mildlyusefulsoftware.mpdremote.bo;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

	private int id;
	private String title;
	private String filename;
	private int length;

	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
		public Song createFromParcel(Parcel in) {
			return new Song(in);
		}

		public Song[] newArray(int size) {
			return new Song[size];
		}
	};

	public Song() {
		super();
	}

	private Song(Parcel in) {
		id = in.readInt();
		title = in.readString();
		filename = in.readString();
		length = in.readInt();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(id);
		dest.writeValue(title);
		dest.writeValue(filename);
		dest.writeValue(length);
	}
}
