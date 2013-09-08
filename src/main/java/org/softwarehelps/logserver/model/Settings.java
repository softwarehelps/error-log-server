/* diar/src/com/duckspot/diar/model/Settings
 * 
 * History:
 * 3/21/13 PD rewrite parseTime() method
 * 3/22/13 PD change name to Settings
 * 3/22/13 PD change to always exist, even if user not logged in
 * 3/22/13 PD added getURLs() and getURL() functions to build URLs
 * 4/ 1/13 PD define constants for Entity kind & parameter names
 * 4/12/13 PD clean up GoogleServices access
 */
package org.softwarehelps.logserver.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Peter M Dobson
 */
public class Settings extends SettingsData {

    private UserService userService;
    private DateFormat timeCodeFormat;
    private Map<String,String> myURLs;
    
    public Settings(Entity entity) {
        
        super(entity);
        
        userService = UserServiceFactory.getUserService();        
        
        myURLs = new HashMap<String, String>();
        myURLs.putAll(URLs.getURLs());        
    }    
        
    public void setRequest(HttpServletRequest request) {
        // login now always returns to register, which then may redirect to home
        myURLs.put("login",  userService.createLoginURL(myURLs.get("register")));
        // logout always returns to home
        myURLs.put("logout", userService.createLoginURL(myURLs.get("home")));
    }    
    
    public boolean isUserLoggedIn() {
        return userService.isUserLoggedIn();
    }
    
    public boolean isUserAdmin() {
        return userService.isUserAdmin();
    }
    
    public TimeZone getTimeZone()
    {        
        return TimeZone.getTimeZone(getTimeZoneID());
    }
    
    public Locale getLocale()
    {        
        // TODO: 8 create logic to allow user to define Locale
        return Locale.getDefault();
    }
    
    public String getUsername() {
        return userService.getCurrentUser().getNickname();
    }
    
    /* ------------------------------------------------------------------------
     * CONVENIENCE METHODS
     */
    
    public Map<String,String> getURLs() {
        return myURLs;
    }
    
    public String getURL(String name) {
        if (!myURLs.containsKey(name)) {
            StringBuilder keys = new StringBuilder();
            for (String key: myURLs.keySet()) {
                keys.append(key).append(" ");
            }
            throw new IllegalArgumentException("Can't find URL for "+name+" in "+keys);
        }
        return myURLs.get(name);        
    }
    
    String timeCode(Date d) {
        if (timeCodeFormat == null) {
            timeCodeFormat = 
                    new SimpleDateFormat("yyyy.MM.dd.HH.mm", getLocale());
        }
        return timeCodeFormat.format(d);
    }
    
    public String getURL(String name, Object...more) {
        StringBuilder sb = new StringBuilder();
        sb.append(myURLs.get(name));
        for (Object o: more) {
            sb.append("/");
            if (o instanceof Date) {
                sb.append(timeCode((Date)o));
            } else {
                sb.append(o.toString());
            }
        }
        return sb.toString();
    }        
}
