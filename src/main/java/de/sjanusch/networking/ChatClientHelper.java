package de.sjanusch.networking;

import com.google.inject.Inject;
import de.sjanusch.hipchat.handler.HipchatRequestHandler;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.Occupant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;

/**
 * Created by Sandro Janusch
 * Date: 21.12.16
 * Time: 14:16
 */

public class ChatClientHelper {

  private static final Logger logger = LoggerFactory.getLogger(ChatClientHelper.class);

  private final HipchatRequestHandler hipchatRequestHandler;

  @Inject
  public ChatClientHelper(final HipchatRequestHandler hipchatRequestHandler) {
    this.hipchatRequestHandler = hipchatRequestHandler;
  }

  public boolean isPacketForBot(final Packet packet, final String username) {
    final String message = this.toMessage(packet);
    final String from = packet.getFrom().split("\\/")[0];
    final String to = packet.getTo().split("\\/")[0];
    if (to.equals(username) && !from.equals(username) && from != null && message != null && !message.isEmpty()) {
      if (this.chatUserExists(this.extractHipchatUserId(from))) {
        return true;
      }
    }
    return false;
  }

  public String extractUserId(final Occupant occupant) {
    final String[] values = occupant.getJid().split("/");
    if (values.length > 0) {
      return values[0];
    }
    return null;
  }

  public String toMessage(final Packet packet) {
    try {
      final Field f = packet.getClass().getDeclaredField("bodies");
      f.setAccessible(true);
      @SuppressWarnings("rawtypes")
      final HashSet h = (HashSet) f.get(packet);
      if (h.size() == 0)
        return "";
      for (final Object obj : h) {
        if (obj instanceof Message.Body)
          return ((Message.Body) obj).getMessage();
      }
      return "";
    } catch (final Exception e) {
      return "";
    }
  }

  public String extractHipchatUserId(final String user) {
    final String[] values = user.split("@");
    if (values.length > 0) {
      final String[] valueId = values[0].split("_");
      if (values.length > 0) {
        return valueId[1];
      }
    }
    return null;
  }

  public byte[] serializeObject(final Object object) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String json = mapper.writeValueAsString(object);
      return json.getBytes();
    } catch (JsonGenerationException e) {
      logger.error("JsonGenerationException " + e.getMessage());
    } catch (JsonMappingException e) {
      logger.error("JsonMappingException " + e.getMessage());
    } catch (IOException e) {
      logger.error("IOException " + e.getMessage());
    }
    return null;
  }

  private boolean chatUserExists(final String username) {
    if (username != null && !username.isEmpty()) {
      hipchatRequestHandler.hipchatUserExist(username);
    }
    return false;
  }

}
