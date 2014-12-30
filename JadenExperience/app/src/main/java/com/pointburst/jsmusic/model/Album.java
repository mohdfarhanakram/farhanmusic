package com.pointburst.jsmusic.model;

import java.util.ArrayList;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class Album {

    private String mediaTextColor;
    private String albumTextColor;
    private String showHideControlSection;
    private String bgColor;
    private String controlBgColor;
    private String title;
    private String playListTextColor;
    private String showControlAlways;
    private String controlSongInfoTextColor;
    private String lyricsTextColor;
    private String textColor;
    private String albumBgColor;
    private String playlistTextColor;
    private String controlTextColor;
    private String artImageUrl;
    private String bgImageUrl;
    private String playListBgImageUrl;
    private String lyricsImageUrl;
    private String albumKey;

    private ArrayList<Media> medias;

    public ArrayList<Media> getMedias() {
        if(medias==null)
            medias = new ArrayList<Media>();
        return medias;
    }

    public void setMedias(ArrayList<Media> medias) {
        this.medias = medias;
    }

    public String getMediaTextColor() {
        return mediaTextColor;
    }

    public void setMediaTextColor(String mediaTextColor) {
        this.mediaTextColor = mediaTextColor;
    }

    public String getAlbumTextColor() {
        return albumTextColor;
    }

    public void setAlbumTextColor(String albumTextColor) {
        this.albumTextColor = albumTextColor;
    }

    public String getShowHideControlSection() {
        return showHideControlSection;
    }

    public void setShowHideControlSection(String showHideControlSection) {
        this.showHideControlSection = showHideControlSection;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getControlBgColor() {
        return controlBgColor;
    }

    public void setControlBgColor(String controlBgColor) {
        this.controlBgColor = controlBgColor;
    }

    public String getPlayListTextColor() {
        return playListTextColor;
    }

    public void setPlayListTextColor(String playListTextColor) {
        this.playListTextColor = playListTextColor;
    }

    public String getShowControlAlways() {
        return showControlAlways;
    }

    public void setShowControlAlways(String showControlAlways) {
        this.showControlAlways = showControlAlways;
    }

    public String getControlSongInfoTextColor() {
        return controlSongInfoTextColor;
    }

    public void setControlSongInfoTextColor(String controlSongInfoTextColor) {
        this.controlSongInfoTextColor = controlSongInfoTextColor;
    }

    public String getLyricsTextColor() {
        return lyricsTextColor;
    }

    public void setLyricsTextColor(String lyricsTextColor) {
        this.lyricsTextColor = lyricsTextColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getAlbumBgColor() {
        return albumBgColor;
    }

    public void setAlbumBgColor(String albumBgColor) {
        this.albumBgColor = albumBgColor;
    }

    public String getPlaylistTextColor() {
        return playlistTextColor;
    }

    public void setPlaylistTextColor(String playlistTextColor) {
        this.playlistTextColor = playlistTextColor;
    }

    public String getControlTextColor() {
        return controlTextColor;
    }

    public void setControlTextColor(String controlTextColor) {
        this.controlTextColor = controlTextColor;
    }

    public String getArtImageUrl() {
        return artImageUrl;
    }

    public void setArtImageUrl(String artImageUrl) {
        this.artImageUrl = artImageUrl;
    }

    public String getBgImageUrl() {
        return bgImageUrl;
    }

    public void setBgImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getPlayListBgImageUrl() {
        return playListBgImageUrl;
    }

    public void setPlayListBgImageUrl(String playListBgImageUrl) {
        this.playListBgImageUrl = playListBgImageUrl;
    }

    public String getLyricsImageUrl() {
        return lyricsImageUrl;
    }

    public void setLyricsImageUrl(String lyricsImageUrl) {
        this.lyricsImageUrl = lyricsImageUrl;
    }

    public String getAlbumKey() {
        return albumKey;
    }

    public void setAlbumKey(String albumKey) {
        this.albumKey = albumKey;
    }
}
