package org.softwarehelps.logserver;

import org.softwarehelps.logserver.model.Settings;
import org.softwarehelps.logserver.model.SettingsDAO;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SecurityFilter uses Google UserService and DatastoreService to find out if 
 * the current user is logged in and has registered as a user of DIAR.
 * 
 * &lt;init-param&gt;requireLogin - default is true, filter forwards to 
 * DiarURLs.SECURITY?continue=requestURI if it's true
 * 
 * Attributes are set:
 * <ul><li>request.settings</li>
 * </ul>
 * 
 * @author Peter M Dobson
 */
public class SecurityFilter implements Filter {
    
    private boolean requireLogin = false;
    
    @Override
    public void init(FilterConfig fc) 
            throws ServletException 
    {                
        String value = fc.getInitParameter("requireLogin");
        if (value != null) {
            requireLogin = Boolean.parseBoolean(value);
        }
    }

    @Override
    public void doFilter(ServletRequest req, 
                         ServletResponse resp, 
                         FilterChain fc) 
            throws IOException, ServletException 
    {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        
        SettingsDAO settingsDAO = new SettingsDAO();
                
        Settings settings = settingsDAO.currentSettings();
        settings.setRequest(request);
        if (!requireLogin || settings.isUserLoggedIn()) {                        
        
            // pass data to DiarServlets
            request.setAttribute("shSecurity", "2.2");
            request.setAttribute("settingsDAO", settingsDAO);
            request.setAttribute("settings", settings);
            
            fc.doFilter(req, resp);
            
        } else {
            
            // user somehow logged out && requireLogin is true
            // forward to security page
            String continueURL = request.getRequestURI();                
            response.sendRedirect(String.format("%s?continue=%s",
                                  settings.getURL("security"),
                                  continueURL));
        }
    }

    public void destroy() {
    }    
}
