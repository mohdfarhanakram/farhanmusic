package com.pointburst.jsmusic.model;

import java.io.Serializable;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class Media implements Serializable {

    private String showHideControlSection;
    private String lyrics;
    private String title;
    private String textColor;
    private String duration;
    private String mirrorImageOriginalAssetId;
    private String mirrorImageEncodeProfileId;
    private String artImageUrl;
    private String mediaKey;
    private String streamUrl ;

    public String getStreamUrl() {
        //if(StringUtils.isNullOrEmpty(streamUrl))
            //streamUrl = "https://pub2-o.secure.miisolutions.net/mi/pointburst/Stoopid Rich Produced by Tone Jonez_audio_128K.mp3";
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getShowHideControlSection() {
        return showHideControlSection;
    }

    public void setShowHideControlSection(String showHideControlSection) {
        this.showHideControlSection = showHideControlSection;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMirrorImageOriginalAssetId() {
        return mirrorImageOriginalAssetId;
    }

    public void setMirrorImageOriginalAssetId(String mirrorImageOriginalAssetId) {
        this.mirrorImageOriginalAssetId = mirrorImageOriginalAssetId;
    }

    public String getMirrorImageEncodeProfileId() {
        return mirrorImageEncodeProfileId;
    }

    public void setMirrorImageEncodeProfileId(String mirrorImageEncodeProfileId) {
        this.mirrorImageEncodeProfileId = mirrorImageEncodeProfileId;
    }

    public String getArtImageUrl() {
        return artImageUrl;
    }

    public void setArtImageUrl(String artImageUrl) {
        this.artImageUrl = artImageUrl;
    }

    public String getMediaKey() {
        return mediaKey;
    }

    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media)) return false;

        Media media = (Media) o;

        if (mediaKey != null ? !mediaKey.equals(media.mediaKey) : media.mediaKey != null) return false;

        return true;
    }

}
