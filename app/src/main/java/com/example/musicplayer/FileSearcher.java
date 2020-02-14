package com.example.musicplayer;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FileSearcher {

    private String rootPath;
    private ArrayList<File> listSongs;
    private String extension;


    //TODO: Leer rutas y valores para ignorar

    /**
     * @param extension referencia a la extension del archivo
     */
    public FileSearcher(String extension) {
        listSongs = new ArrayList<>();
        rootPath = Environment.getExternalStorageDirectory().toString();
        this.extension = addDot(extension);
    }

    private int getExtensionLength(String extension) {
        return extension.length();
    }

    public void listSongs(String path) {
        //Log.d("Path", path);
        File rootDirectory = new File(path);
        if (!rootDirectory.exists()) {
            Log.d("Error0", "FATAL ERROR!");
            return;
        }

        if (!rootDirectory.isDirectory()) return;

        File[] files = rootDirectory.listFiles();

        if (files == null) {
            Log.d("Error2", "FATAL ERROR!");
            return;
        }

        for (int i = 0; i < files.length; i++) {
            File currentFile = files[i];
            if (!currentFile.exists()) {
                Log.d("Error1", "FATAL ERROR!");
                continue;
            }

            if (currentFile.isDirectory()) {
                listSongs(path + "/" + currentFile.getName());
            } else {

                int extensionLength = getExtensionLength(extension);
                if (currentFile.getName().length() >= extensionLength + 1) {

                    String currentFileExtension = getExtensionFromFileName(currentFile.getName());
                    if (currentFileExtension == null) return;
                    //Log.d("Test", "Extension " + currentFileExtension);
                    if (currentFileExtension.equals(extension)) {
                        listSongs.add(currentFile);
                    }
                }
            }

        }

        testPrint();

    }

    public String getExtensionFromFileName(String filename) {
        String filenameExtension = null;
        int dotIndex = filename.lastIndexOf(".");

        try {
            if (dotIndex > 0)
                filenameExtension = filename.substring(dotIndex);

        } catch (Exception e) {
            Log.e("Error: ", String.format("Filename %s", filename));
            e.printStackTrace();
        }

        return filenameExtension;
    }


    public void setExtension(String extension) {
        this.extension = addDot(extension);
    }

    public String getExtension() {
        return this.extension;
    }

    private void testPrint() {
        for (int i = 0; i < listSongs.size(); i++) {
            File currentSong = listSongs.get(i);
            Log.d("Song", "Name: " + currentSong.getName() + ", Path: " + currentSong.getAbsolutePath());
        }
    }

    public String getRootPath() {
        return rootPath;
    }

    private String addDot(String extension) {
        extension = extension.trim();
        if (extension.isEmpty() || extension == null) return null;
        if (extension.charAt(0) != '.') {
            extension = "." + extension;
        }
        return extension;
    }

}
