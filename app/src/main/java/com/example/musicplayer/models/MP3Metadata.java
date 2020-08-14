package com.example.musicplayer.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MP3Metadata implements Parcelable {
	public MP3Metadata()
	{
	}

	protected MP3Metadata(Parcel in) {
		title = in.readString();
		artistName = in.readString();
		genreName = in.readString();
		albumName = in.readString();
		image = in.readParcelable(Bitmap.class.getClassLoader());
	}

	public static final Creator<MP3Metadata> CREATOR = new Creator<MP3Metadata>() {
		@Override
		public MP3Metadata createFromParcel(Parcel in) {
			return new MP3Metadata(in);
		}

		@Override
		public MP3Metadata[] newArray(int size) {
			return new MP3Metadata[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(artistName);
		dest.writeString(genreName);
		dest.writeString(albumName);
		dest.writeParcelable(image, flags);
	}

	public static class Builder
	{
		private String _albumName, _artistName, _genreName, _title;
		private Bitmap _image;

		public Builder()
		{
			_albumName = null;
			_artistName = null;
			_genreName = null;
			_title = null;
			_image = null;
		}

		public Builder withTitle(String title)
		{
			_title = title;
			return this;
		}

		public Builder withArtistName(String name)
		{
			_artistName = name;
			return this;
		}

		public Builder withAlbum(String albumName)
		{
			_albumName = albumName;
			return this;
		}

		public Builder withGenre(String genreName)
		{
			_genreName = genreName;
			return this;
		}

		public Builder withImage(Bitmap image)
		{
			_image = image;
			return this;
		}

		public MP3Metadata build()
		{
			MP3Metadata output = new MP3Metadata();
			output.title = _title;
			output.artistName = _artistName;
			output.genreName = _genreName;
			output.albumName = _albumName;
			output.image = _image;
			return output;
		}
	}

	public String title;
	public String artistName;
	public String genreName;
	public String albumName;
	public Bitmap image;
}
