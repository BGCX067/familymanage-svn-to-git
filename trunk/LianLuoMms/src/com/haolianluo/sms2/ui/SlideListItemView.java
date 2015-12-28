/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haolianluo.sms2.ui;

import com.haolianluo.sms2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Map;

/**
 * A simplified view of slide in the slides list.
 */
public class SlideListItemView extends LinearLayout implements SlideViewInterface {
    private static final String TAG = "SlideListItemView";

    private Context mContext;
    private TextView mTextPreview;
    private ImageView mImagePreview;
    private TextView mAttachmentName;
    private ImageView mAttachmentIcon;

    public SlideListItemView(Context context) {
        super(context);
        this.mContext = context;
    }

    public SlideListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        mTextPreview = (TextView) findViewById(R.id.text_preview);
        mTextPreview.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        mImagePreview = (ImageView) findViewById(R.id.image_preview);
        mAttachmentName = (TextView) findViewById(R.id.attachment_name);
        mAttachmentIcon = (ImageView) findViewById(R.id.attachment_icon);
    }

    public void startAudio() {
        // Playing audio is not needed in this view.
    }

    public void startVideo() {
        // Playing audio is not needed in this view.
    }

    public void setAudio(Uri audio, String name, Map<String, ?> extras) {
        if (name != null) {
            mAttachmentName.setText(name);
            mAttachmentIcon.setImageResource(R.drawable.ic_mms_music);
        } else {
            mAttachmentName.setText("");
            mAttachmentIcon.setImageDrawable(null);
        }
    }

    public void setImage(String name, Bitmap bitmap) {
        if (null == bitmap) {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_missing_thumbnail_picture);
        }
        mImagePreview.setImageBitmap(bitmap);
    }

    public void setImageRegionFit(String fit) {
        // TODO Auto-generated method stub
    }

    public void setImageVisibility(boolean visible) {
        // TODO Auto-generated method stub
    }

    public void setText(String name, String text) {
        mTextPreview.setText(text);
        mTextPreview.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    public void setTextVisibility(boolean visible) {
        // TODO Auto-generated method stub
    }

    public void setVideo(String name, Uri video) {
        if (name != null) {
            mAttachmentName.setText(name);
            mAttachmentIcon.setImageResource(R.drawable.movie);
        } else {
            mAttachmentName.setText("");
            mAttachmentIcon.setImageDrawable(null);
        }

        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(mContext, video);
//            mImagePreview.setImageBitmap(mp.getFrameAt(1000));
            mImagePreview.setImageBitmap(BitmapFactory.decodeResource(
            		getResources(), android.R.drawable.ic_media_play));
        } catch (IOException e) {
        } finally {
            mp.release();
        }
    }

    public void setVideoVisibility(boolean visible) {
        // TODO Auto-generated method stub
    }

    public void stopAudio() {
        // Stopping audio is not needed in this view.
    }

    public void stopVideo() {
        // Stopping video is not needed in this view.
    }

    public void reset() {
        // TODO Auto-generated method stub
    }

    public void setVisibility(boolean visible) {
        // TODO Auto-generated method stub
    }

    public void pauseAudio() {
        // TODO Auto-generated method stub

    }

    public void pauseVideo() {
        // TODO Auto-generated method stub

    }

    public void seekAudio(int seekTo) {
        // TODO Auto-generated method stub

    }

    public void seekVideo(int seekTo) {
        // TODO Auto-generated method stub

    }
}
