package com.dylan.library.media;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.IOException;

/**
 * Author: Dylan
 * Date: 2021/8/14
 * Desc:
 */

public class MediaSource {

    public Context context;
    public String inputPath;
    public Uri inputUri;

    public MediaSource(String inputPath) {
        this.inputPath = inputPath;
    }

    public MediaSource(Context context, Uri inputUri) {
        this.context = context;
        this.inputUri = inputUri;
    }

    public void setDataSource(MediaMetadataRetriever retriever){
        if(inputPath!=null){
            retriever.setDataSource(inputPath);
        }else{
            retriever.setDataSource(context,inputUri);
        }
    }

    public void setDataSource(MediaExtractor extractor) throws IOException {
        if(inputPath!=null){
            extractor.setDataSource(inputPath);
        }else{
            extractor.setDataSource(context,inputUri,null);
        }
    }
}
