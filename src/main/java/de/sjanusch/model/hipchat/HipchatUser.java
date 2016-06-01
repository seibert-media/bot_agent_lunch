package de.sjanusch.model.hipchat;

import com.google.gson.Gson;
import de.sjanusch.utils.WebUtils;

import java.util.HashMap;

public class HipchatUser {

    private int user_id;

    private String name;

    private String mention_name;

    private String email;

    private String title;

    private String photo_url;

    private String status;

    private String status_message;

    private int is_group_admin;

    private int is_deleted;

    private static HashMap<String, HipchatUser> user_cache = new HashMap<String, HipchatUser>();

    public static HipchatUser createInstance(String nick, String APIKey) {
        if (!user_cache.containsKey(nick)) {
            HipchatUser[] users = getHipchatUsers(APIKey);
            for (HipchatUser user : users) {
                if (!user_cache.containsKey(user.name))
                    user_cache.put(user.name, user);
            }
            if (!user_cache.containsKey(nick))
                return null;
            else
                return user_cache.get(nick);
        } else
            user_cache.get(nick);
        return null;
    }

    public static HipchatUser createInstance(int ID, String APIKey) {
        HipchatUser[] users = getHipchatUsers(APIKey);
        for (HipchatUser user : users) {
            if (user.user_id == ID)
                return user;
        }
        return null;
    }

    public static HipchatUser[] getHipchatUsers(String APIKey) {
        return getHipchatUserHolder(APIKey).users;
    }
    
    private static HipchatUserHolder getHipchatUserHolder(String APIKey) {
        try {
            final Gson gson = new Gson();
            final String JSON = WebUtils.getTextAsString("https://api.hipchat.com/v2/user?format=json&auth_token=" + APIKey);
            final HipchatUserHolder data = gson.fromJson(JSON, HipchatUserHolder.class);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            HipchatUserHolder u = new HipchatUserHolder();
            u.users = new HipchatUser[0];
            return u;
        }
    }
    
    private HipchatUser() {
    }
    
    public int getUserID() {
        return user_id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getMentionName() {
        return mention_name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getPhotoUrl() {
        return photo_url;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getStatusMessage() {
        return status_message;
    }
    
    public boolean isGroupAdmin() {
        return is_group_admin == 1;
    }
    
    public boolean isDeletedAccount() {
        return is_deleted == 1;
    }
    
    private static class HipchatUserHolder {

        public HipchatUser[] users;

        public HipchatUserHolder() {
        }
    }
}
