package com.timothysinard.Checkers.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Sound class used to play a sound loaded from the given file name
 */
public class Sound {
	
	//Stores file name, kept for identification purposes
	private String file;
	//Stores the loaded audio clip
	private Clip clip;
	
	public Sound (String file){
		setClip(file);
	}
	
	/**
	 * Method to play sound
	 *
	 *<b>Preconditions:</b> Clip cannot be null
	 *<b>Postconditions:</b> Plays clip once if precondition is met
	 *<b>Throws:</b> None
	 */
	public void playSound(){
		if(clip != null){
			clip.start();
		}
	}
	
	/**
	 * Method to loop sound
	 *
	 *<b>Preconditions:</b> Clip cannot be null
	 *<b>Postconditions:</b> Loops clip if precondition is met
	 *<b>Throws:</b> None
	 */
	public void loopSound(){
		if(clip != null){
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	/**
	 *  Method to stop the sound 
	 *
	 *<b>Preconditions:</b> Clip cannot be null
	 *<b>Postconditions:</b> Clip is stopped if precondition is met.
	 *<b>Throws:</b> None
	 */
	public void stopSound(){
		if(clip != null){
			clip.stop();
		}
	}
	
	
	/**
	 * Sets audio clip to be control by this class
	 *
	 *<b>Preconditions:</b> Given file name must be valid with extensions. Only accepts .wav, .mid, and .au sounds.
	 *<b>Postconditions:</b> Loads the file from the sounds/ path of the project. If invalid, clip is set to null.
	 *<b>Throws:</b> None
	 */
	public boolean setClip(String file){
		this.file = file;
		try {
			URL url = new File("sounds" + file).toURI().toURL();
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			return true;
		} catch (UnsupportedAudioFileException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (LineUnavailableException e) {
			return false;
		}
	}
	
	/**
	 * Get and return current audio clip file name
	 *
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> None
	 *<b>Throws:</b>
	 */
	public String getClipFile(){
		return file;
	}
	
	/**
	 * Get and return current audio clip 
	 *
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> None
	 *<b>Throws:</b>
	 */
	public Clip getClip(){
		return clip;
	}
}
