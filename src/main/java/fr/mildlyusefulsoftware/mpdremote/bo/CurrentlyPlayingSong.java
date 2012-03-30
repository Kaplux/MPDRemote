package fr.mildlyusefulsoftware.mpdremote.bo;

public class CurrentlyPlayingSong {

	private long elapsedTime;

	private Song song;

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

}
