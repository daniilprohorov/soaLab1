import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(urlPatterns={"/organizations/*"})
public class OrganizationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter writer = response.getWriter();

        String text = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is = new InputSource(new StringReader(text));
        Document doc = null;
        try {
            doc = builder.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        }

        String name = doc.getElementsByTagName("name").item(0).getTextContent();
        String fullname = doc.getElementsByTagName("fullname").item(0).getTextContent();
        String employeescount = doc.getElementsByTagName("employeescount").item(0).getTextContent();

        DbConfig config = new DbConfig();
        Base.open(config.driver, config.url, config.name, config.password);
        Organization org1 = new Organization();
        Boolean initialized = org1.init(name, fullname, employeescount);
        if (initialized) {
            writer.println(org1.toXml(true, true));
        } else {
            response.setStatus(422);
        }
        Base.close();
    }

    protected Optional<List<Organization>> filterOrganizationsDB(String idStr, String nameStr, String fullnameStr, String employeescountStr) {
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

        if (filterStr.isEmpty()) {
            return Optional.empty();
        } else {
            List<Organization> organizations = Organization.where(filterStr);
            return Optional.of(organizations);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "*");

        DbConfig config = new DbConfig();
        Base.open(config.driver, config.url, config.name, config.password);
        response.setContentType("text/xml;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        String itemsPerPageStr = request.getParameter("itemsperpage");
        String pageStr = request.getParameter("page");

        String filterByIdStr = request.getParameter("filter-by-id");
        String filterByNameStr = request.getParameter("filter-by-name");
        String filterByFullnameStr = request.getParameter("filter-by-fullname");
        String filterByEmployeescountStr = request.getParameter("filter-by-employeescount");
        String sortBy = request.getParameter("sortBy");



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
                Optional<List<Organization>> filterRes = filterOrganizationsDB(filterByIdStr, filterByNameStr, filterByFullnameStr, filterByEmployeescountStr);
                if (filterRes.isPresent()) {
                    orgs = filterRes.get();
                } else {
                    response.setStatus(404);
                    Base.close();
                    return;
                }
            } else {
                orgs = Organization.findAll();
            }
            LazyList<Organization> organizations = (LazyList<Organization>) orgs;
            if (itemsPerPageStr == null) {
                if (sortBy == null) {
                    writer.println(organizations.toXml(true, true));
                } else {
                    List<String> sortByFields = Arrays.asList("id", "name", "fullname", "employeescount");
                    if (sortByFields.contains(sortBy)){
                        writer.println(organizations.orderBy(sortBy).toXml(true, true));
                    } else {
                        response.setStatus(404);
                        Base.close();
                        return;
                    }
                }
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

                if (sortBy == null) {
                    writer.println(organizations.offset(itemsPerPage * (page - 1)).limit(itemsPerPage).toXml(true, true));
                } else {
                    List<String> sortByFields = Arrays.asList("id", "name", "fullname", "employeescount");
                    if (sortByFields.contains(sortBy)){
                        writer.println(organizations.orderBy(sortBy).offset(itemsPerPage * (page - 1)).limit(itemsPerPage).toXml(true, true));
                    } else {
                        response.setStatus(404);
                        Base.close();
                        return;
                    }
                }
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

        DbConfig config = new DbConfig();
        Base.open(config.driver, config.url, config.name, config.password);
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

        DbConfig config = new DbConfig();
        Base.open(config.driver, config.url, config.name, config.password);

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

                    String text = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = null;
                    try {
                        builder = factory.newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                    InputSource is = new InputSource(new StringReader(text));
                    Document doc = null;
                    try {
                        doc = builder.parse(is);
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }

                    String name = doc.getElementsByTagName("name").item(0).getTextContent();
                    String fullname = doc.getElementsByTagName("fullname").item(0).getTextContent();
                    String employeescount = doc.getElementsByTagName("employeescount").item(0).getTextContent();

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

