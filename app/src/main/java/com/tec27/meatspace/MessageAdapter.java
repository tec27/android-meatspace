package com.tec27.meatspace;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * List adapter that contains chat messages.
 */
public class MessageAdapter extends BaseAdapter {
  private final Activity activity;
  private final List<Message> messages;

  public MessageAdapter(Activity activity) {
    this.activity = activity;
    messages = Lists.newArrayList();
  }

  public void addItem(Message message) {
    messages.add(message);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return messages.size();
  }

  @Override
  public Message getItem(int i) {
    return messages.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(int i, View view, ViewGroup parent) {
    ViewHolder viewHolder = ViewHolder.fromView(view, activity, parent);

    viewHolder.bind(messages.get(i));
    return viewHolder.view;
  }

  private static class ViewHolder {
    private static final String TAG = ViewHolder.class.getSimpleName();

    View view;

    private TextView messageText;
    private SurfaceView videoSurface;

    private Message boundMessage;
    private SurfaceHolder videoHolder;
    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnPreparedListener onPreparedListener;
    private boolean prepared = false;

    private ViewHolder(Context context, ViewGroup parent) {
      view = LayoutInflater.from(context).inflate(R.layout.message, parent, false);
      view.setTag(R.id.ViewHolder, this);
      messageText = (TextView) view.findViewById(R.id.MessageText);
      videoSurface = (SurfaceView) view.findViewById(R.id.Video);
      onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
          prepared = true;
          playVideo();
        }
      };

      view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View view) {
          mediaPlayer = new MediaPlayer();
          mediaPlayer.setOnPreparedListener(onPreparedListener);
          openVideo();
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
          resetVideo();
          mediaPlayer.release();
          mediaPlayer = null;
        }
      });
      videoSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
          videoHolder = surfaceHolder;
          openVideo();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {
          playVideo();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
          videoHolder = null;
          resetVideo();
        }
      });
    }

    public static ViewHolder fromView(View view, Context context, ViewGroup parent) {
      if (view == null) {
        return new ViewHolder(context, parent);
      } else {
        return (ViewHolder) view.getTag(R.id.ViewHolder);
      }
    }

    public void bind(Message message) {
      boundMessage = message;

      messageText.setText(Html.fromHtml(message.getText()));
      openVideo();
      playVideo();
    }

    private void openVideo() {
      if (mediaPlayer == null || videoHolder == null || boundMessage == null) {
        return;
      }

      resetVideo();
      try {
        mediaPlayer.setDisplay(videoHolder);
        mediaPlayer.setDataSource(view.getContext(), boundMessage.getMediaUri());
        mediaPlayer.setLooping(true);
        mediaPlayer.prepareAsync();
      } catch (IOException e) {
        Log.d(TAG, "Error playing video", e);
      }
    }

    private void playVideo() {
      if (!prepared || mediaPlayer == null || videoHolder == null || boundMessage == null) {
        return;
      }
      mediaPlayer.start();
    }

    private void resetVideo() {
      prepared = false;
      if (mediaPlayer != null) {
        mediaPlayer.reset();
      }
    }
  }
}
