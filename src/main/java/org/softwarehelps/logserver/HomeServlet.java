package org.softwarehelps.logserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.softwarehelps.logserver.model.Settings;

/**
 * HomeServlet is responsible for starting users on a path through 
 * registration, and/or login.
 * 
 * If the user is already logged in, there is a server-side redirect to the
 * "ready" URL (see Settings.getURL()), otherwise a oneColumn layout with
 * welcome content is displayed.  Welcome content 
 * 
 */
public class HomeServlet extends AbstractServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        checkSecurity(request);
        
        Settings settings = (Settings)request.getAttribute("settings");
        
        // find out if we're logged in and registered
        if (settings.isUserRegistered()) {
            
            // we get here if user is logged in, and registered            
            
            // forward to READY URL
            getServletContext().getRequestDispatcher(settings.getURL("ready"))
                               .forward(request, response);
        } else {
        
            // prepare and output page
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("settings", settings);
            model.put("pageTitle", "error log server");
            model.put("userBlock", template("layout/userBlock.html", model));
            model.put("content", template("welcome.html", model));
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.print(template("layout/oneColumn.html", model));
        }
    }
    
    /**
     * Post does this so it also forwards to READY.
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException, ServletException
    {
        doGet(request, response);
    }
}
