package com.tec27.meatspace;

import android.net.Uri;
import android.util.Base64;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * A Meatspace chat message.
 */
public class Message {
  private final String TAG = Message.class.getSimpleName();

  private final String fingerprint;
  private final Date created;
  private final String message;
  private final Uri mediaUri;

  private Message(String fingerprint, Date created, String message, Uri mediaUri) {
    this.fingerprint = fingerprint;
    this.created = created;
    this.message = message;
    this.mediaUri = mediaUri;
  }

  public static Message fromJson(JSONObject json) {
    try {
      return new Message(
          json.getString("fingerprint"),
          new Date(json.getInt("created")),
          json.getString("message"),
          Uri.parse(json.getString("media"))
      );
    } catch (JSONException e) {
      throw Throwables.propagate(e);
    }
  }

  public String getFingerprint() {
    return fingerprint;
  }

  public Date getCreated() {
    return created;
  }

  public String getText() {
    return message;
  }

  public Uri getMediaUri() {
    return mediaUri;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Message message1 = (Message) o;

    if (created != null ? !created.equals(message1.created) : message1.created != null)
      return false;
    if (fingerprint != null ? !fingerprint.equals(message1.fingerprint) : message1.fingerprint != null)
      return false;
    if (message != null ? !message.equals(message1.message) : message1.message != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = fingerprint != null ? fingerprint.hashCode() : 0;
    result = 31 * result + (created != null ? created.hashCode() : 0);
    result = 31 * result + (message != null ? message.hashCode() : 0);
    return result;
  }
}
