package com.emaintec.lib.util

import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.util.Log

import com.emaintec.lib.base.Emaintec

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap

class Sound
// ===========================================================
// Constructors
// ===========================================================

private constructor() {

	// ===========================================================
	// Fields
	// ===========================================================

	private var mSoundPool: SoundPool? = null
	private var mLeftVolume: Float = 0.toFloat()
	private var mRightVolume: Float = 0.toFloat()
	private var mIsAudioFocus = true

	// sound path and stream ids map
	// a file may be played many times at the same time
	// so there is an array map to a file path
	private val mPathStreamIDsMap = HashMap<String, ArrayList<Int>>()
	// A lock for mPathStreamIDsMap operation to keep mPathStreamIDsMap being visited in only one thread at a time.
	private val mLockPathStreamIDsMap = Any()

	private val mPathSoundIDMap = HashMap<String, Int>()

	// Key: SoundID, Value: SoundInfoForLoadedCompleted
	private val mPlayWhenLoadedEffects = ConcurrentHashMap<Int, SoundInfoForLoadedCompleted>()

	// volume should be in [0, 1.0]
	var effectsVolume: Float
		get() = (this.mLeftVolume + this.mRightVolume) / 2
		set(volume) {
			var volume = volume
			if (volume < 0) {
				volume = 0f
			}
			if (volume > 1) {
				volume = 1f
			}

			this.mRightVolume = volume
			this.mLeftVolume = this.mRightVolume

			if (!mIsAudioFocus)
				return

			setEffectsVolumeInternal(mLeftVolume, mRightVolume)
		}

	fun init() {
		this.initData()
	}

	private fun initData() {
		if (Build.MODEL.contains("GT-I9100")) {
			this.mSoundPool =
				SoundPool(Sound.MAX_SIMULTANEOUS_STREAMS_I9100, AudioManager.STREAM_MUSIC, Sound.SOUND_QUALITY)
		} else {
			this.mSoundPool =
				SoundPool(Sound.MAX_SIMULTANEOUS_STREAMS_DEFAULT, AudioManager.STREAM_MUSIC, Sound.SOUND_QUALITY)
		}

		this.mSoundPool!!.setOnLoadCompleteListener(OnLoadCompletedListener())

		this.mLeftVolume = 0.5f
		this.mRightVolume = 0.5f
	}

	fun preloadEffect(path: String): Int {
		var soundID = this.mPathSoundIDMap[path]

		if (soundID == null) {
			soundID = this.createSoundIDFromAsset(path)
			// save value just in case if file is really loaded
			if (soundID != Sound.INVALID_SOUND_ID) {
				this.mPathSoundIDMap[path] = soundID
			}
		}

		return soundID
	}

	fun unloadEffect(path: String) {
		synchronized(mLockPathStreamIDsMap) {
			// stop effects
			val streamIDs = this.mPathStreamIDsMap[path]
			if (streamIDs != null) {
				for (steamID in streamIDs) {
					this.mSoundPool!!.stop(steamID)
				}
			}
			this.mPathStreamIDsMap.remove(path)
		}

		// unload effect
		val soundID = this.mPathSoundIDMap[path]
		if (soundID != null) {
			this.mSoundPool!!.unload(soundID)
			this.mPathSoundIDMap.remove(path)
		}
	}

	fun playEffect(path: String, loop: Boolean, pitch: Float, pan: Float, gain: Float): Int {
		var soundID = this.mPathSoundIDMap[path]
		var streamID = Sound.INVALID_STREAM_ID

		if (soundID != null) {
			// parameters; pan = -1 for left channel, 1 for right channel, 0 for both channels

			// play sound
			streamID = this.doPlayEffect(path, soundID, loop, pitch, pan, gain)
		} else {
			// the effect is not prepared
			soundID = this.preloadEffect(path)
			if (soundID == Sound.INVALID_SOUND_ID) {
				// can not preload effect
				return Sound.INVALID_SOUND_ID
			}

			val info = SoundInfoForLoadedCompleted(path, loop, pitch, pan, gain)
			mPlayWhenLoadedEffects.putIfAbsent(soundID, info)

			synchronized(info.lock) {
				try {
					info.lock.wait(LOAD_TIME_OUT.toLong())
				} catch (e: Exception) {
					e.printStackTrace()
				}

			}

			streamID = info.effectID
			mPlayWhenLoadedEffects.remove(soundID)
		}

		return streamID
	}

	fun stopEffect(steamID: Int) {
		this.mSoundPool!!.stop(steamID)

		synchronized(mLockPathStreamIDsMap) {
			// remove record
			for (pPath in this.mPathStreamIDsMap.keys) {
				if (this.mPathStreamIDsMap[pPath]!!.contains(steamID)) {
					this.mPathStreamIDsMap[pPath]!!.removeAt(this.mPathStreamIDsMap[pPath]!!.indexOf(steamID))
					break
				}
			}
		}
	}

	fun pauseEffect(steamID: Int) {
		this.mSoundPool!!.pause(steamID)
	}

	fun resumeEffect(steamID: Int) {
		this.mSoundPool!!.resume(steamID)
	}

	fun pauseAllEffects() {
		synchronized(mLockPathStreamIDsMap) {
			if (!this.mPathStreamIDsMap.isEmpty()) {
				val iter = this.mPathStreamIDsMap.entries.iterator()
				while (iter.hasNext()) {
					val entry = iter.next()
					for (steamID in entry.value) {
						this.mSoundPool!!.pause(steamID)
					}
				}
			}
		}
	}

	fun resumeAllEffects() {
		synchronized(mLockPathStreamIDsMap) {
			// can not only invoke SoundPool.autoResume() here, because
			// it only resumes all effects paused by pauseAllEffects()
			if (!this.mPathStreamIDsMap.isEmpty()) {
				val iter = this.mPathStreamIDsMap.entries.iterator()
				while (iter.hasNext()) {
					val entry = iter.next()
					for (steamID in entry.value) {
						this.mSoundPool!!.resume(steamID)
					}
				}
			}
		}
	}

	fun stopAllEffects() {
		synchronized(mLockPathStreamIDsMap) {
			// stop effects
			if (!this.mPathStreamIDsMap.isEmpty()) {
				val iter = this.mPathStreamIDsMap.entries.iterator()
				while (iter.hasNext()) {
					val entry = iter.next() as Map.Entry<String, ArrayList<Int>>
					for (steamID in entry.value) {
						this.mSoundPool!!.stop(steamID)
					}
				}
			}

			// remove records
			this.mPathStreamIDsMap.clear()
		}
	}

	private fun setEffectsVolumeInternal(left: Float, right: Float) {
		synchronized(mLockPathStreamIDsMap) {
			// change the volume of playing sounds
			if (!this.mPathStreamIDsMap.isEmpty()) {
				val iter = this.mPathStreamIDsMap.entries.iterator()
				while (iter.hasNext()) {
					val entry = iter.next()
					for (steamID in entry.value) {
						this.mSoundPool!!.setVolume(steamID, left, right)
					}
				}
			}
		}
	}

	fun end() {
		this.mSoundPool!!.release()
		synchronized(mLockPathStreamIDsMap) {
			this.mPathStreamIDsMap.clear()
		}
		this.mPathSoundIDMap.clear()
		this.mPlayWhenLoadedEffects.clear()

		this.mLeftVolume = 0.5f
		this.mRightVolume = 0.5f

		this.initData()
	}

	private fun createSoundIDFromAsset(path: String): Int {
		var soundID = Sound.INVALID_SOUND_ID

		try {
			if (path.startsWith(File.separator)) {
				soundID = this.mSoundPool!!.load(path, 0)
			} else {
				/*				if (Cocos2dxHelper.getObbFile() != null) {
					final AssetFileDescriptor assetFileDescriptor = Cocos2dxHelper.getObbFile().getAssetFileDescriptor(path);
					soundID = mSoundPool.load(assetFileDescriptor, 0);
				} else */run { soundID = this.mSoundPool!!.load(Emaintec.application!!.assets.openFd(path), 0) }
			}
		} catch (e: Exception) {
			soundID = Sound.INVALID_SOUND_ID
			Log.e(Sound.TAG, "error: " + e.message, e)
		}

		// mSoundPool.load returns 0 if something goes wrong, for example a file does not exist
		if (soundID == 0) {
			soundID = Sound.INVALID_SOUND_ID
		}

		return soundID
	}

	private fun clamp(value: Float, min: Float, max: Float): Float {
		return Math.max(min, Math.min(value, max))
	}

	// Adds 'synchronized' keyword for doPlayeEffect since it's possible to be invoked in GL thread and main thread.
	// In Cocos2dxSound.playEffect, it's in GL thread.
	// In onLoadComplete callback, it's in main thread.
	@Synchronized
	private fun doPlayEffect(path: String, soundId: Int, loop: Boolean, pitch: Float, pan: Float, gain: Float): Int {
		val leftVolume = this.mLeftVolume * gain * (1.0f - this.clamp(pan, 0.0f, 1.0f))
		val rightVolume = this.mRightVolume * gain * (1.0f - this.clamp(-pan, 0.0f, 1.0f))
		val soundRate = this.clamp(SOUND_RATE * pitch, 0.5f, 2.0f)

		// play sound
		val streamID = this.mSoundPool!!.play(
			soundId,
			this.clamp(leftVolume, 0.0f, 1.0f),
			this.clamp(rightVolume, 0.0f, 1.0f),
			Sound.SOUND_PRIORITY,
			if (loop) -1 else 0,
			soundRate
		)

		synchronized(mLockPathStreamIDsMap) {
			// record stream id
			var streamIDs = this.mPathStreamIDsMap[path]
			if (streamIDs == null) {
				streamIDs = ArrayList()
				this.mPathStreamIDsMap[path] = streamIDs
			}
			streamIDs.add(streamID)
		}

		return streamID
	}

	fun onEnterBackground() {
		this.mSoundPool!!.autoPause()
	}

	fun onEnterForeground() {
		this.mSoundPool!!.autoResume()
	}

	internal fun setAudioFocus(isFocus: Boolean) {
		mIsAudioFocus = isFocus
		val leftVolume = if (mIsAudioFocus) mLeftVolume else 0.0f
		val rightVolume = if (mIsAudioFocus) mRightVolume else 0.0f

		setEffectsVolumeInternal(leftVolume, rightVolume)
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private inner class SoundInfoForLoadedCompleted internal constructor(
		internal var path: String,
		internal var isLoop: Boolean,
		internal var pitch: Float,
		internal var pan: Float,
		internal var gain: Float
	) {
		internal var effectID: Int = 0
		internal var lock = java.lang.Object()

		init {
			effectID = Sound.INVALID_SOUND_ID
		}
	}

	inner class OnLoadCompletedListener : SoundPool.OnLoadCompleteListener {

		override fun onLoadComplete(soundPool: SoundPool, sampleId: Int, status: Int) {
			// This callback is in main thread.
			if (status == 0) {
				val info = mPlayWhenLoadedEffects[sampleId]

				if (info != null) {
					info.effectID = doPlayEffect(info.path, sampleId, info.isLoop, info.pitch, info.pan, info.gain)
					synchronized(info.lock) {
						info.lock.notifyAll()
					}
				}
			}
		}
	}

	fun playEffect(path: String) {
		try {
			val player = MediaPlayer()
			var afd: AssetFileDescriptor? = null
			afd = Emaintec.application!!.assets.openFd(path)
			player.setDataSource(afd!!.fileDescriptor, afd.startOffset, afd.length)
			afd.close()
			player.prepare()
			player.start()
		} catch (e: IOException) {
			e.printStackTrace()
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////
	private object SingletonHolder {
		val INSTANCE = Sound()
	}

	companion object {
		// ===========================================================
		// Constants
		// ===========================================================

		private val TAG = "Sound"

		private val MAX_SIMULTANEOUS_STREAMS_DEFAULT = 5
		private val MAX_SIMULTANEOUS_STREAMS_I9100 = 3
		private val SOUND_RATE = 1.0f
		private val SOUND_PRIORITY = 1
		private val SOUND_QUALITY = 5

		private val INVALID_SOUND_ID = -1
		private val INVALID_STREAM_ID = -1

		private val LOAD_TIME_OUT = 500

		val instance: Sound
			get() = SingletonHolder.INSTANCE
	}
}
