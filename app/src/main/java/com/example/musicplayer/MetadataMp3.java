package com.example.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;

public class MetadataMp3
{
	private MediaMetadataRetriever _mediaMetadataRetreive;
	private Bitmap _image;
	private String _albumName;
	private String _artistName;
	private String _genreName;
	private String _title;
	private File _mp3File;

	public MetadataMp3(File mp3File)
	{
		_mp3File = mp3File;
		_mediaMetadataRetreive = new MediaMetadataRetriever();
	}

	public void extractMetadata()
	{
		_mediaMetadataRetreive.setDataSource(_mp3File.getAbsolutePath());
		byte[] byteImage = _mediaMetadataRetreive.getEmbeddedPicture();
		if(byteImage != null)
			_image = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
		_albumName = _mediaMetadataRetreive.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		_artistName = _mediaMetadataRetreive.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		_genreName = _mediaMetadataRetreive.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
		_title = _mediaMetadataRetreive.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

		if(_albumName == null)
			_albumName = "Unknown Album";
		if(_artistName == null)
			_artistName = "Unknown Artist";
		if(_genreName == null)
			_genreName = "Unknown Genre";
		if(_title == null)
			_title = "Unknown Title";
	}

	public Bitmap getImage() {
		return _image;
	}

	public String getAlbumName() {
		return _albumName;
	}

	public String getArtistName() {
		return _artistName;
	}

	public String getTitle() {
		return _title;
	}

	public File getMp3File() {
		return _mp3File;
	}

	public String getGenreName() {
		return _genreName;
	}
}
