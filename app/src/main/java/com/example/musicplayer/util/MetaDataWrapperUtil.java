package com.example.musicplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;

import com.example.musicplayer.MP3Metadata;

import java.io.File;
import java.io.FileDescriptor;

public final class MetaDataWrapperUtil {
    private MetaDataWrapperUtil() { }

    public static final String UNKNOWN_ALBUM = "Unknown Album";
    public static final String UNKNOWN_ARTIST = "Unknown Artist";
    public static final String UNKNOWN_GENRE = "Unknown Genre";
    public static final String UNKNOWN_TITLE = "Unknown Title";

    public static MP3Metadata MP3FromFile(File file)
    {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource(file.getAbsolutePath());
        }
        catch (Exception e){
            Log.e("metadataRetrieverError", "File: " + file.getAbsolutePath());
            Log.e("metadataRetrieverError", e.getMessage());
        }

        String title, artistName, albumName, genreName;
        Bitmap image = null;

        byte[] byteImage = metadataRetriever.getEmbeddedPicture();
        if(byteImage != null)
            image = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

        albumName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        artistName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        genreName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        if(albumName == null)
            albumName = UNKNOWN_ALBUM;
        if(artistName == null)
            artistName = UNKNOWN_ARTIST;
        if(genreName == null)
            genreName = UNKNOWN_GENRE;
        if(title == null)
            title = file.getName();

        metadataRetriever.release();

        return new MP3Metadata.Builder()
                .withTitle(title)
                .withArtistName(artistName)
                .withGenre(genreName)
                .withAlbum(albumName)
                .withImage(image)
                .build();
    }
}
