package barsoosayque.libgdxoboe

import android.content.res.AssetManager
import com.badlogic.gdx.Audio
import com.badlogic.gdx.Files
import com.badlogic.gdx.audio.AudioDevice
import com.badlogic.gdx.audio.AudioRecorder
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.backends.android.AndroidAudio
import com.badlogic.gdx.backends.android.AndroidMusic
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException

/** [Audio] implementation which utilize [OboeMusic] and [OboeSound].
 * Returns [GdxRuntimeException] on native errors */
class OboeAudio(private val assetManager: AssetManager) : AndroidAudio {
    private var audioEngine: NativeAudioEngine = NativeAudioEngine()
    private val audioDevicesList: MutableList<AudioDevice> = mutableListOf()
    private val musicList: MutableList<Music> = mutableListOf()
    private val soundsList: MutableList<Sound> = mutableListOf()

    init {
        System.loadLibrary("libgdx-oboe")
        init()
    }

    private external fun init()
    private external fun createSoundpoolFromAsset(assetManager: AssetManager, path: String): Long
    private external fun createSoundpoolFromPath(path: String): Long
    private external fun createMusicFromAsset(assetManager: AssetManager, path: String): Long
    private external fun createMusicFromPath(path: String): Long
    private external fun createAudioEngine(samplingRate: Int, isMono: Boolean): Long

    external override fun resume()
    external override fun pause()
    private external fun disposeEngine()

    override fun notifyMusicDisposed(music: AndroidMusic?) {
        // No need to do anything here since runtime disposal of music/soundpool is
        // well handled in mixer.cpp
    }

    override fun dispose() {
        disposeEngine()

        audioDevicesList.onEach { it.dispose() }.clear()
        musicList.onEach { it.dispose() }.clear()
        soundsList.onEach { it.dispose() }.clear()
    }

    override fun newMusic(file: FileHandle): Music? = when (file.type()) {
        Files.FileType.Internal -> createMusicFromAsset(assetManager, file.path())
        else -> createMusicFromPath(file.file().path)
    }.takeIf { it != 0L }
        ?.let(::NativeMusic)
        ?.let(::OboeMusic)
        ?.also(musicList::add)

    override fun newSound(file: FileHandle): Sound? = when (file.type()) {
        Files.FileType.Internal -> createSoundpoolFromAsset(assetManager, file.path())
        else -> createSoundpoolFromPath(file.file().path)
    }.takeIf { it != 0L }
        ?.let(::NativeSoundpool)
        ?.let(::OboeSound)
        ?.also(soundsList::add)

    override fun newAudioDevice(samplingRate: Int, isMono: Boolean): AudioDevice =
        createAudioEngine(samplingRate, isMono).let(::NativeAudioEngine)
            .let(::OboeAudioDevice)
            .also(audioDevicesList::add)

    override fun newAudioRecorder(samplingRate: Int, isMono: Boolean): AudioRecorder =
        createAudioEngine(samplingRate, isMono).let(::NativeAudioEngine)
            .let(::OboeAudioRecorder)
}