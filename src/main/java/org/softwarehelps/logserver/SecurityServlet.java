package org.softwarehelps.logserver;

import org.softwarehelps.logserver.model.Settings;
import com.google.appengine.api.users.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Display message, suggesting login again, or telling user "access denied".
 */
public class SecurityServlet extends AbstractServlet {
    
    UserService userService;
    
    @Override
    public void init() {
        super.init();
        userService = UserServiceFactory.getUserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        checkSecurity(request);
        
        Settings settings = (Settings)request.getAttribute("settings");
        
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("settings", settings);
        model.put("userBlock", template("layout/userBlock.html", model));
        
        // Security filter forwards to /security?continue=failed-url
        String nextURL = request.getParameter("continue");
        if (nextURL == null || nextURL.isEmpty()) {
            nextURL = settings.getURL("home");;
        }
        model.put("securityLoginURL", userService.createLoginURL(nextURL));
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print(template("security.html",model));
    }    
}
