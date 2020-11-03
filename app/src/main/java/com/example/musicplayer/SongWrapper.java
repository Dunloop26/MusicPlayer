package com.example.musicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.PowerManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SongWrapper {
    private MediaPlayer _player;
    private boolean _prepareForPlay = false;

    private File _currentSongFile;


    // Realizo la lista de listeners
    private ArrayList<OnSongPlayActionListener> _onPlayActionListeners = new ArrayList<>();
    private ArrayList<OnSongStopActionListener> _onStopActionListeners = new ArrayList<>();
    private ArrayList<OnSongChangeActionListener> _onChangeActionListener = new ArrayList<>();

    public interface OnSongPlayActionListener
    {
        void onSongPlayAction(SongWrapper sw, File file);
    }
    public interface OnSongStopActionListener
    {
        void onSongStopAction(SongWrapper sw, File file);
    }

    public interface OnSongChangeActionListener
    {
        void onSongChangeAction(SongWrapper sw, File previousFile);
    }

    public void addOnSongPlayActionListener(OnSongPlayActionListener listener)
    {
        if(listener != null)
            _onPlayActionListeners.add(listener);
    }

    public void addOnSongStopActionListener(OnSongStopActionListener listener)
    {
        if(listener != null)
            _onStopActionListeners.add(listener);
    }

    public void addOnSongChangeActionListener(OnSongChangeActionListener listener)
    {
        if(listener != null)
            _onChangeActionListener.add(listener);
    }

    public void removeOnSongPlayActionListener(OnSongPlayActionListener listener)
    {
        if(listener != null)
            _onStopActionListeners.remove(listener);
    }

    public void removeOnSongStopActionListener(OnSongStopActionListener listener)
    {
        if(listener != null)
            _onPlayActionListeners.remove(listener);
    }

    public void removeOnSongChangeActionListener(OnSongChangeActionListener listener)
    {
        if(listener != null)
            _onChangeActionListener.remove(listener);
    }

    private void fireOnSongPlayActionListener(File file)
    {
        for(OnSongPlayActionListener listener : _onPlayActionListeners)
        {
            if(listener != null)
                listener.onSongPlayAction(this, file);
        }
    }

    private void fireOnSongStopActionListener(File file)
    {
        for(OnSongStopActionListener listener : _onStopActionListeners)
        {
            if(listener != null)
                listener.onSongStopAction(this, file);
        }
    }

    private void fireOnSongChangeListener(File file)
    {
        for(OnSongChangeActionListener listener : _onChangeActionListener)
        {
            if(listener != null)
                listener.onSongChangeAction(this, file);
        }
    }

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
            _player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    fireOnSongChangeListener(getCurrentSongFile());
                }
            });
        } else {

            // Reseteo el player, para definir la nueva canción
            _player.reset();
        }

        return _player;
    }

    private boolean onMediaPlayerErrorListener(MediaPlayer mp, int what, int extra) {
        return true;
    }

    private void onPrepareAsyncMediaPlayer(MediaPlayer player) {

        // Si se prepara para reproducir la canción
        if (_prepareForPlay) {
            // Designo la preparación como completada
            _prepareForPlay = false;
            // Inicio la reproducción
            player.start();

            // Ejecuto el evento de reproducción
            fireOnSongPlayActionListener(getCurrentSongFile());
        }
    }

    public File getCurrentSongFile()
    {
        return _currentSongFile;
    }

    public int getMediaDuration()
    {
        if(_player == null) _player = getMediaPlayer();
        return _player.getDuration();
    }

    public int getMediaCurrentPosition()
    {
        if(_player == null) _player = getMediaPlayer();
        return _player.getCurrentPosition();
    }

    public MediaPlayer getPlayer() {
        return _player;
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

        // Defino el archivo actual para reproducción
        _currentSongFile = songFile;

    }

    public void play(File songFile, int msec)
    {
        // Si la no canción existe
        if (!songFile.exists()) return;
        if(_player == null) return;

        // Si está reproduciendo una canción la detengo
        if (_player.isPlaying()) _player.pause();

        // Preparo para reproducir la canción
        _prepareForPlay = true;
        int duration = _player.getDuration();
        if(msec < 0)
            _player.seekTo(0);
        else if(msec > duration)
            _player.seekTo(duration);
        else
            _player.seekTo(msec);

        _player.start();

        // Defino el archivo actual para reproducción
        _currentSongFile = songFile;
    }

    public boolean isPlaying()
    {
        return _player.isPlaying();
    }

    public void continuePlaying()
    {
        // Si la no canción existe
        if (!_currentSongFile.exists()) return;
        if(_player == null) return;

        if(_player.isPlaying())
            _player.pause();
        else
            _player.start();
    }

    public void setCurrentSongFile(File currentSongFile) {
        _currentSongFile = currentSongFile;
    }

    public void continueReproduction()
    {
        play(_currentSongFile, _player.getCurrentPosition());
    }


    public void initMediaPlayer(Context context) {
        _player = getMediaPlayer();
        _player.setWakeMode(context.getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        _player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }


    public void play(String filepath) {
        // TODO: Encubrir en un try-catch para evitar potenciales errores al no encontrar la ruta del archivo
        // Uso el mismo método, para evitar repetir código
        File songFile = new File(filepath);
        play(songFile);
    }

    public void stop() {
        _player.stop();
        _player.release();

        // Ejecuto el evento con la canción que se detuvo
        fireOnSongStopActionListener(_currentSongFile);

        // Borro la referencia a la canción en reproducción
        _currentSongFile = null;
    }
}
