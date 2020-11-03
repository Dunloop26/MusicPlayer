package com.example.musicplayer.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.FileSearcher;
import com.example.musicplayer.models.MP3Metadata;
import com.example.musicplayer.util.MetaDataWrapperUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton patterm
 */
public class MetadataSongRepository {
	private static MetadataSongRepository instance;
	private ArrayList<MP3Metadata> dataset = new ArrayList<>();
	private ArrayList<File> files;

	public static MetadataSongRepository getInstance() {
		if(instance == null) {
			instance = new MetadataSongRepository();
		}
		return instance;
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public MutableLiveData<List<MP3Metadata>> getMetadataSongs() {
		setMetadataSongs();

		MutableLiveData<List<MP3Metadata>> data = new MutableLiveData<>();
		data.setValue(dataset);
		return data;
	}

	public void setMetadataSongs() {
		files = executeFileSearcher();
		if(files != null){
			for(int i = 0; i < files.size(); i++) {
				dataset.add(MetaDataWrapperUtil.MP3FromFile(files.get(i)));
			}
		}
	}

	private ArrayList<File> executeFileSearcher() {
		//TODO: Cambiar el sistema de busqueda de archivos para hacerlo compatible con android 11
		FileSearcher fileSearcher = new FileSearcher(".mp3");
		fileSearcher.findFilesOnPath(fileSearcher.getRootPath());
		ArrayList<File> files = fileSearcher.getFiles();
		fileSearcher.printFileUtil(files);
		return files;
	}
}
