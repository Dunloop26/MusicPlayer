package com.example.musicplayer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class FileSearcher {

    private ArrayList<File> _fileList;
    private ArrayList<String> _ignorePaths;
    private String _rootPath;
    private String _extension;
    private Context _context;

    /**
     * @param extension Referencia a la extension del archivo
     */
    public FileSearcher(String extension, Context context) {
        _extension = addDot(extension);
        _context = context;
        _rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        _rootPath = _context.getExternalFilesDir(null).getAbsolutePath();
        _fileList = new ArrayList<>();
        _ignorePaths = new ArrayList<>();
        readIgnoreFile();
    }

    private void readIgnoreFile()
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

    private String addDot(String extension) {
        extension = extension.trim();
        if (extension.isEmpty() || extension == null) return null;
        if (extension.charAt(0) != '.') {
            extension = "." + extension;
        }
        return extension;
    }

    private void processCurrentFile(File file, String path) {

        if (!file.exists()) {
            Log.d("Error", "FATAL ERROR!");
            return;
        }
        if (file.isDirectory()) {
            boolean ignore = false;
            String nextPath = path + "/" + file.getName();
            for(int pathListIndex= 0; pathListIndex < _ignorePaths.size() && !ignore; pathListIndex++)
            {
                if(nextPath.startsWith(_ignorePaths.get(pathListIndex)))
                {
                    Log.d("IGNOREFILE", nextPath);
                    ignore = true;
                }
            }
            if (!ignore)
            {
                findFilesOnPath(nextPath);
            }
        } else {

            int extensionLength = _extension.length();
            if (file.getName().length() >= extensionLength + 1) {

                String currentFileExtension = getExtensionFromFileName(file.getName());
                if (currentFileExtension == null) return;
                if (currentFileExtension.equals(_extension)) {
                    _fileList.add(file);
                }
            }
        }
    }

    public void findFilesOnPath(String path) {
        File rootDirectory = new File(path);


        if (!rootDirectory.exists()) {
            return;
        }
        if (!rootDirectory.isDirectory()) return;



        File[] files = rootDirectory.listFiles();
        rootDirectory.list();

//        Log.d("files_debug", "Archivos: " + files.length);
        if (files == null) {
            Log.d("Error", "FATAL ERROR!");
            return;
        }

        for (int fileListIndex = 0; fileListIndex < files.length; fileListIndex++) {
            File currentFile = files[fileListIndex];
            // Analizo si el archivo actual coincide con los parámetros y lo agrego a la lista
//            Log.d("files_debug", "Analizando: " + currentFile);
            processCurrentFile(currentFile, path);
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

    public static void printFileUtil(ArrayList<File> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
            File currentSong = fileList.get(i);
//            Log.d("Song", "Name: " + currentSong.getName() + ", Path: " + currentSong.getAbsolutePath());
        }
    }

    public String getRootPath() {
        return _rootPath;
    }

    public ArrayList<File> getFiles()
    {
        return _fileList;
    }
}
