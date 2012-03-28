package fr.mildlyusefulsoftware.mpdremote.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.objects.MPDSong;

import android.util.Log;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.util.MPDRemoteUtils;

public class MPDService {

	private static MPDService instance;
	private MPD mpd;

	private MPDService() {
		try {
			mpd = connect("192.168.1.18", 6600);
		} catch (UnknownHostException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		} catch (MPDConnectionException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		}
	}

	public static MPDService getInstance() {
		if (instance == null) {
			instance = new MPDService();
		}
		return instance;
	}

	public List<Song> getCurrentPlayList() {

		List<MPDSong> mpdSongList = mpd.getMPDPlaylist().getSongList();

		List<Song> songs = new ArrayList<Song>();
		CollectionUtils.collect(mpdSongList, new Transformer() {

			@Override
			public Object transform(Object mpdSong) {
				Song s = new Song();
				s.setTitle(((MPDSong) mpdSong).getTitle());
				s.setId(((MPDSong) mpdSong).getId());
				return s;
			}
		}, songs);
		return songs;
	}

	public void playSong(Song s) {
		if (s != null) {
			MPDSong mpdSong = new MPDSong();
			mpdSong.setId(s.getId());

			try {
				mpd.getMPDPlayer().playId(mpdSong);
			} catch (MPDPlayerException e) {
				Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
			} catch (MPDConnectionException e) {
				Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
			}
		}
	}

	private MPD connect(String ip, int port) throws UnknownHostException,
			MPDConnectionException {
		return new MPD(ip, port);
	}
}
