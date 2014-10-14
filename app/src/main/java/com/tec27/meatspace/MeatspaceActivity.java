package com.tec27.meatspace;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class MeatspaceActivity extends Activity {

  private Socket socket;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.meatspace_activity);


    try {
      socket = IO.socket("https://chat.meatspac.es");
    } catch (URISyntaxException ex) {
      throw Throwables.propagate(ex);
    }

    socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        Log.d("tec27", "connected!");
        socket.emit("join", "webm");
      }
    }).on("message", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        JSONObject message = (JSONObject) args[0];
        try {
          Log.d("tec27", message.getString("fingerprint") + ": " + message.getString("message"));
        } catch (JSONException e) {
          Log.e("tec27", "JSON error!", e);
        }
      }
    });

    socket.connect();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.meatspace, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
