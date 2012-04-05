package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;

class PlaylistAdapter extends ArrayAdapter<Song> {

	Context context;
	int itemLayout;

	public PlaylistAdapter(Context context, List<Song> objects,int itemLayout) {
		super(context, 0, objects);
		this.context = context;
		this.itemLayout=itemLayout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(itemLayout, null);
		if (!isEmpty()) {
			Song song = getItem(position);
			TextView songTitleView = (TextView) view
					.findViewById(R.id.songTitle);
			if (song != null) {
				songTitleView.setText(song.getTitle());
			}
		}
		return view;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
	

}
