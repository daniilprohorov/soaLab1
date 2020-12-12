import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

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
        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        response.setContentType("text/xml;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            // return list of product
            // TODO: сделать пагинацию!
            List<Organization> orgs = Organization.findAll();
            PrintWriter writer = response.getWriter();
            writer.println(((LazyList<Organization>) orgs).toXml(true, true));

        } else {
            // return product with id

            String[] pathParts = pathInfo.split("/");
            String idStr = pathParts[1]; // {id}


            Optional<Integer> id;

            try {
                Integer idI = Integer.valueOf(idStr);
                id = Optional.of(idI);
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                id = Optional.empty();
            }
            if (id.isPresent()) {
                List<Organization> orgs = Organization.where("id = ?", id.get());
                PrintWriter writer = response.getWriter();
                if (!orgs.isEmpty()){
                    writer.println(orgs.get(0).toXml(true, true));
                } else {
                    response.setStatus(404);
                }
            } else {
                response.setStatus(422);
            }
        }
        Base.close();
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        response.setContentType("text/xml;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.setStatus(404);
        } else {
            String[] pathParts = pathInfo.split("/");
            String idStr = pathParts[1]; // {id}
            Optional<Integer> id;

            try {
                Integer idI = Integer.valueOf(idStr);
                id = Optional.of(idI);
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                id = Optional.empty();
            }
            if (id.isPresent()) {
                List<Organization> orgs = Organization.where("id = ?", id.get());
                if (!orgs.isEmpty()){
                    orgs.get(0).delete();
                } else {
                    response.setStatus(404);
                }
            } else {
                response.setStatus(422);
            }
        }
        Base.close();
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        response.setContentType("text/xml;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.setStatus(404);
        } else {
            String[] pathParts = pathInfo.split("/");
            String idStr = pathParts[1]; // {id}
            Optional<Integer> id;

            try {
                Integer idI = Integer.valueOf(idStr);
                id = Optional.of(idI);
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                id = Optional.empty();
            }
            if (id.isPresent()) {
                List<Organization> orgs = Organization.where("id = ?", id.get());
                if (!orgs.isEmpty()){

                    String name = request.getParameter("name");
                    String fullname = request.getParameter("fullname");
                    String employeescount = request.getParameter("employeescount");

                    orgs.get(0).update(name, fullname, employeescount);

                } else {
                    response.setStatus(404);
                }
            } else {
                response.setStatus(422);
            }
        }
        Base.close();
    }
}

