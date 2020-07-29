package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class PlayList {
	private ArrayList<File> _songs;
	private String _name;
	private Date _creationDate;
	private int _totalSongs;
	private int _indexCurrentSong;


	public PlayList()
	{
		_indexCurrentSong = 0;
		_songs = new ArrayList<>();
	}


	public void addSong(File song)
	{
		_songs.add(song);
	}

	public void setIndexCurrentSong(int indexCurrentSong) {
		_indexCurrentSong = indexCurrentSong;
	}

	public void increaseSongIndex()
	{
		_indexCurrentSong++;
		if(_indexCurrentSong >= _songs.size())
		{
			_indexCurrentSong = 0;
		}
	}

	public void decreaseSongIndex()
	{
		_indexCurrentSong--;
		if(_indexCurrentSong < 0)
		{
			_indexCurrentSong = _songs.size() - 1;
		}
	}

	public File getCurrentSong()
	{
		return _songs.get(_indexCurrentSong);
	}

	public int indexOf(File songFile)
	{
		return _songs.indexOf(songFile);
	}

	public void setIndexFromSong(File songFile)
	{
		_indexCurrentSong = indexOf(songFile);

	}
}
