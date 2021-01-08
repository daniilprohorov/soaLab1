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

    protected List<Organization> filterOrganizationsDB(String idStr, String nameStr, String fullnameStr, String employeescountStr) {
        Organization o = new Organization();
        Optional id = o.idIsValid(idStr);
        Optional name = o.nameIsValid(nameStr);
        Optional fullname = o.fullnameIsValid(fullnameStr);
        Optional employeescount = o.employeescountIsValid(employeescountStr);
        String filterStr = "";
        if (id.isPresent()) {
            filterStr = filterStr.concat("id = " + id.get().toString());
        }
        if (name.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("name = '" + name.get() + "'");
            } else {
                filterStr = filterStr.concat(" and name = '" + name.get() + "'");
            }
        }
        if (fullname.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("fullname = '" + fullname.get().toString() + "'");
            } else {
                filterStr = filterStr.concat(" and fullname = '" + fullname.get().toString() + "'");
            }
        }
        if (employeescount.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("employeescount = " + employeescount.get().toString());
            } else {
                filterStr = filterStr.concat(" and employeescount = " + employeescount.get().toString());
            }
        }
        List<Organization> organizations = Organization.where(filterStr);
        return organizations;
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
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

        response.addHeader("Access-Control-Allow-Origin", "*");
        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        response.setContentType("text/xml;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        String itemsPerPageStr = request.getParameter("itemsperpage");
        String pageStr = request.getParameter("page");

        String filterByIdStr = request.getParameter("filter-by-id");
        String filterByNameStr = request.getParameter("filter-by-name");
        String filterByFullnameStr = request.getParameter("filter-by-fullname");
        String filterByEmployeescountStr = request.getParameter("filter-by-employeescount");

        Boolean filter = false;

        if (filterByIdStr != null
                || filterByNameStr != null
                || filterByFullnameStr != null
                || filterByEmployeescountStr != null) {
            filter = true;
        }
        if (pathInfo == null) {
            // return list of product
            PrintWriter writer = response.getWriter();
            List<Organization> orgs;
            if (filter) {
                orgs = filterOrganizationsDB(filterByIdStr, filterByNameStr, filterByFullnameStr, filterByEmployeescountStr);
                if (orgs.isEmpty()) {
                    response.setStatus(404);
                    Base.close();
                    return;
                }
            } else {
                orgs = Organization.findAll();
            }
            LazyList<Organization> organizations = (LazyList<Organization>) orgs;

            if (itemsPerPageStr == null) {
                writer.println(organizations.toXml(true, true));
            } else {
                Integer itemsPerPage;
                Integer page;
                try {
                    itemsPerPage = Integer.valueOf(itemsPerPageStr);
                    page = Integer.valueOf(pageStr);
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                    response.setStatus(422);
                    Base.close();
                    return;
                }
                writer.println(organizations.offset(itemsPerPage * (page - 1)).limit(itemsPerPage).toXml(true, true));
            }

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

        response.addHeader("Access-Control-Allow-Origin", "*");
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

        response.addHeader("Access-Control-Allow-Origin", "*");
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

