package de.sjanusch.model.hipchat;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"links", "created", "email", "group", "is_group_admin", "is_guest", "last_active", "photo_url", "presence", "roles", "timezone", "title", "_deleted"})
public class HipchatUser {

  private int id;

  private String mention_name;

  private String name;

  private String version;

  private String xmpp_jid;

  private boolean is_deleted;

  private String xmppUserId;

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public String getMention_name() {
    return mention_name;
  }

  public void setMention_name(final String mention_name) {
    this.mention_name = mention_name;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  public boolean is_deleted() {
    return is_deleted;
  }

  public void setIs_deleted(final boolean is_deleted) {
    this.is_deleted = is_deleted;
  }

  public String getXmpp_jid() {
    return xmpp_jid;
  }

  public void setXmpp_jid(final String xmpp_jid) {
    this.xmpp_jid = xmpp_jid;
  }

  public String getXmppUserId() {
    return xmppUserId;
  }

  public void setXmppUserId(final String xmppUserId) {
    this.xmppUserId = xmppUserId;
  }
}
