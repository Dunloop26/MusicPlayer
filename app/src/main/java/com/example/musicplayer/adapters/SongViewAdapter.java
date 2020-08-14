package com.example.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.models.MP3Metadata;
import com.example.musicplayer.R;

import java.util.List;

public class SongViewAdapter extends RecyclerView.Adapter<SongViewAdapter.SongViewHolder>{
	private MP3Metadata mCurrentMetadata;
	private OnSongListener mOnSongListener;
	private List<MP3Metadata> mMetadataSongs;

	// Provide a suitable constructor (depends on the kind of dataset)
	public SongViewAdapter(List<MP3Metadata> metadataSongs, OnSongListener onSongListener) {
		mMetadataSongs = metadataSongs;
		mOnSongListener = onSongListener;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public SongViewAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent,
															 int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.song_file_view_item, parent, false);
		return new SongViewHolder(view, mOnSongListener);

	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(final SongViewHolder holder, int position) {
		mCurrentMetadata = mMetadataSongs.get(position);
		if(mCurrentMetadata.image != null)
			holder.getCover().setImageBitmap(mCurrentMetadata.image);
		else
			holder.getCover().setImageResource(R.drawable.logo1);
		holder.getTxtName().setText(mCurrentMetadata.title);
		holder.getTxtArtist().setText(String.format("%s | %s", mCurrentMetadata.artistName, mCurrentMetadata.albumName));
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mMetadataSongs.size();
	}

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		private ImageView cover;
		private TextView txtName;
		private TextView txtArtist;
		private ImageView imgMoreOptions;
		private OnSongListener onSongListener;

		public SongViewHolder(@NonNull View view, final OnSongListener onSongListener) {
			super(view);
			view.setOnClickListener(this);
			view.setOnLongClickListener(this);

			cover = view.findViewById(R.id.imgCover);
			txtName = view.findViewById(R.id.txtName);
			txtArtist = view.findViewById(R.id.txtArtist);
			imgMoreOptions = view.findViewById(R.id.imgMoreOptions);
			imgMoreOptions.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onSongListener.onClickSongOptions(getAdapterPosition());
				}
			});
			this.onSongListener = onSongListener;
		}

		public ImageView getCover() {
			return cover;
		}

		public TextView getTxtArtist() {
			return txtArtist;
		}

		public TextView getTxtName() {
			return txtName;
		}

		@Override
		public void onClick(View v) {
			onSongListener.onClickSong(getAdapterPosition());
		}

		@Override
		public boolean onLongClick(View v) {
			onSongListener.onLongClickSong(getAdapterPosition());
			return true;
		}
	}

	public interface OnSongListener {
		void onClickSong(int position);
		void onLongClickSong(int position);
		void onClickSongOptions(int position);
	}
}
