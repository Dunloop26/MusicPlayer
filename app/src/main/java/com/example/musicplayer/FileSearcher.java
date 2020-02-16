package com.example.musicplayer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class FileSearcher {

    private String _rootPath;
    private ArrayList<File> _songList;
    private String _extension;
    private Context _context;
    private ArrayList<String> _ignorePaths;


    //TODO: Leer rutas y valores para ignorar

    /**
     * @param extension referencia a la extension del archivo
     */
    public FileSearcher(String extension, Context context) {
        _songList = new ArrayList<>();
        _rootPath = Environment.getExternalStorageDirectory().toString();
        _extension = addDot(extension);
        _context = context;
        _ignorePaths = new ArrayList<>();
        readFile();
    }

    private int getExtensionLength(String extension) {
        return extension.length();
    }

    public void listSongs(String path) {
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
                boolean ignore = false;
                String nextPath = path + "/" + currentFile.getName();
                for(int j= 0; j < _ignorePaths.size() && !ignore; j++)
                {
                    if(nextPath.startsWith(_ignorePaths.get(j)))
                    {
                        Log.d("IGNOREFILE", nextPath);
                        ignore = true;
                    }
                }
                if (!ignore)
                {
                    listSongs(nextPath);
                }
            } else {

                int extensionLength = getExtensionLength(_extension);
                if (currentFile.getName().length() >= extensionLength + 1) {

                    String currentFileExtension = getExtensionFromFileName(currentFile.getName());
                    if (currentFileExtension == null) return;
                    //Log.d("Test", "Extension " + currentFileExtension);
                    if (currentFileExtension.equals(_extension)) {
                        _songList.add(currentFile);
                    }
                }
            }

        }
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
        _extension = addDot(extension);
    }

    public String getExtension() {
        return _extension;
    }

    public void testPrint() {
        for (int i = 0; i < _songList.size(); i++) {
            File currentSong = _songList.get(i);
            Log.d("Song", "Name: " + currentSong.getName() + ", Path: " + currentSong.getAbsolutePath());
        }
    }

    public String getRootPath() {
        return _rootPath;
    }

    private String addDot(String extension) {
        extension = extension.trim();
        if (extension.isEmpty() || extension == null) return null;
        if (extension.charAt(0) != '.') {
            extension = "." + extension;
        }
        return extension;
    }


    public void readFile()
    {
        InputStream inputStream = _context.getResources().openRawResource(R.raw.ignore);
        try {

            Reader in = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                _ignorePaths.add(line);
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
