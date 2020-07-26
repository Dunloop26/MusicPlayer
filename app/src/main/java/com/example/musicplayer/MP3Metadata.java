package com.example.musicplayer;

import android.graphics.Bitmap;
import android.os.Build;

public class MP3Metadata
{
	public MP3Metadata()
	{
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
