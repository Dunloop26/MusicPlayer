package com.example.musicplayer;

import android.app.Application;
import android.app.Notification;

public class MusicApplication extends Application {

    // TODO: Acondicionar la clase para recibir datos globales de la aplicación,
    //  NOTA: Solo se puede hacer uso de datos estrictamente globales.
    //  Datos que probablemente se puedan enviar por el constructor o
    //  con un manejo de estructura que puede resultar en mejor implementación,
    //  no están pensados para su uso con la clase
    private SongWrapper _songWrapper;
    private PlayList _playList;

    // TODO: Hacer una clase que contenga los datos de sesion

    public SongWrapper getSongWrapper()
    {
        if(_songWrapper == null) _songWrapper = new SongWrapper();
        return _songWrapper;
    }

    public PlayList getPlayList()
    {
        if(_playList == null) _playList = new PlayList();
        return _playList;
    }
}
