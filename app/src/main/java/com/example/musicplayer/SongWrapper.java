package com.example.musicplayer;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class SongWrapper {
    private MediaPlayer _player;
    private boolean _prepareForPlay = false;

    private MediaPlayer getMediaPlayer() {

        if (_player == null) {

            // Creo y configuro el reproductor
            _player = new MediaPlayer();
            _player.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return onMediaPlayerErrorListener(mp, what, extra);
                }
            });
            _player.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    onPrepareAsyncMediaPlayer(mp);
                }
            });
        } else {

            // Reseteo el player, para definir la
            _player.reset();
        }

        return _player;
    }

    private boolean onMediaPlayerErrorListener(MediaPlayer mp, int what, int extra) {
        Log.e("SongWrapper: ", String.format("An error has ocurred: MediaPlayer %s %s %s", mp, what, extra));
        return true;
    }

    private void onPrepareAsyncMediaPlayer(MediaPlayer player) {

        // Si se prepara para reproducir la canción
        if (_prepareForPlay) {
            // Designo la preparación como completada
            _prepareForPlay = false;
            // Inicio la reproducción
            player.start();
        }
    }

    public void play(File songFile) {
        // Si la no canción existe

        if (!songFile.exists()) return;
        // Obtengo el reproductor
        _player = getMediaPlayer();

        // Si está reproduciendo una canción la detengo
        if (_player.isPlaying()) _player.stop();

        // Trato de obtener el archivo a reproducir
        try {
            _player.setDataSource(songFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Preparo para reproducir la canción
        _prepareForPlay = true;
        _player.prepareAsync();

    }

    public void play(String filepath) {
        // Uso el mismo método, para evitar repetir código
        File songFile = new File(filepath);
        play(songFile);
    }

    public void stop() {
        _player.stop();
    }
}
