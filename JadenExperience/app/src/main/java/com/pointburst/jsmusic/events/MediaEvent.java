package com.pointburst.jsmusic.events;

/**
 * Created by kshitij on 14/7/14.
 */
public class MediaEvent {
	public static final int PLAY_PAUSE = 100;
	public static final int PLAY_NEXT = 101;
	public static final int PLAY_PREV = 102;
	public static final int SHUFFLE = 103;
	public static final int MUTE = 104;
	public static final int PLAY_SONG_AT_INDEX = 105;
	private int type;
	private int index;
	private boolean changeAlbumArt = true;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isChangeAlbumArt() {
		return changeAlbumArt;
	}

	public void setChangeAlbumArt(boolean changeAlbumArt) {
		this.changeAlbumArt = changeAlbumArt;
	}
}
