package com.example.v_yanligang.nuomidemo.video;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.v_yanligang.nuomidemo.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_yanligang on 2017/2/10.
 */

public class VideoProvider {

    public List<Video> getVideoList(Context context) {
        List<Video> list = new ArrayList<>();
        if(context == null) {
            return list;
        }
        LogUtils.e("video", "titlehahhaa");
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            LogUtils.e("video", "cursor.moveToNext()" + cursor.moveToNext());
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                LogUtils.e("video", "title" + title);
                String album = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                String artist = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                String displayName = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String path = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                long duration = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                long size = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                Video video = new Video(id, title, album, artist, displayName, mimeType, path, size, duration);
                list.add(video);
            }
            cursor.close();
        }
        return list;
    }
}
