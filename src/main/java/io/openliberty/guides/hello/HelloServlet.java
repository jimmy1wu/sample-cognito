package io.openliberty.guides.hello;

import com.ibm.websphere.security.social.UserProfileManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HelloServlet", urlPatterns = "/")
@ServletSecurity(value = @HttpConstraint(rolesAllowed = { "user" }))
public class HelloServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        PrintWriter writer = response.getWriter();

        String username = request.getUserPrincipal().getName();
        writer.println("Hello " + username);

        String accessToken = UserProfileManager.getUserProfile().getAccessToken();
        writer.println("\nAccess Token:");
        writer.println(accessToken);

        String idToken = UserProfileManager.getUserProfile().getIdToken().compact();
        writer.println("\nId Token:");
        writer.println(idToken);

    }
}
