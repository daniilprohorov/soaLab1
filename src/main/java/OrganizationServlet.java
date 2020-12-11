import org.javalite.activejdbc.Base;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns={"/organizations/*"})
public class OrganizationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();

        String name = request.getParameter("name");
        String fullname = request.getParameter("fullname");
        String employeescount = request.getParameter("employeescount");

        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        Organization org1 = new Organization();
        Boolean initialized = org1.init(name, fullname, employeescount);
        if (initialized) {
            writer.println(org1.toXml(true, true));
        } else {
            response.setStatus(422);
        }
        Base.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
