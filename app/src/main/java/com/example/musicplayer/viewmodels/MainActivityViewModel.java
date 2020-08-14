package com.example.musicplayer.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicplayer.models.MP3Metadata;
import com.example.musicplayer.repositories.MetadataSongRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {
	private MutableLiveData<List<MP3Metadata>> mMetadataSongs;
	private MetadataSongRepository mRepo;

	public void init() {
		if(mMetadataSongs != null) {
			return;
		}
		mRepo = MetadataSongRepository.getInstance();
		mMetadataSongs = mRepo.getMetadataSongs();
	}

	public ArrayList<File> getFiles() {
		return mRepo.getFiles();
	}

	public LiveData<List<MP3Metadata>> getMetadataSongs() {
		return mMetadataSongs;
	}
}
