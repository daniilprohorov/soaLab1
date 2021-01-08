import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

@WebServlet(urlPatterns={"/products/*"})
public class ProductServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter writer = response.getWriter();

        String name = request.getParameter("name");
        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String price = request.getParameter("price");
        String unitofmeasure = request.getParameter("unitofmeasure");
        String manufacturer = request.getParameter("manufacturer");

        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        Product prd1 = new Product();
        Boolean initialized = prd1.init(name, x, y, price, unitofmeasure, manufacturer);
        if (initialized) {
            writer.println(prd1.toXml(true, true));
        } else {
            response.setStatus(422);
        }
        Base.close();
    }

    protected List<Product> filterProductsDB(String idStr, String nameStr, String xStr, String yStr, String dateStr, String priceStr, String unitofmeasureStr, String manufacturerStr) {
        Product p = new Product();
        Optional id = p.idIsValid(idStr);
        Optional name = p.nameIsValid(nameStr);
        Optional x = p.xIsValid(xStr);
        Optional y = p.yIsValid(yStr);
        Optional date = p.dateIsValid(dateStr);
        Optional price = p.priceIsValid(priceStr);
        Optional unitofmeasure = p.unitofmeasureIsValid(unitofmeasureStr);
        Optional manufacturer = p.manufacturerIsValid(manufacturerStr);
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
        if (x.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("x = " + x.get().toString());
            } else {
                filterStr = filterStr.concat(" and x = " + x.get().toString());
            }
        }
        if (y.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("y = " + y.get().toString());
            } else {
                filterStr = filterStr.concat(" and y = " + y.get().toString());
            }
        }
        if (date.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("creationdate = '" + date.get().toString() + "'");
            } else {
                filterStr = filterStr.concat(" and creationdate = '" + date.get().toString() + "'");
            }
        }
        if (price.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("price = " + price.get().toString());
            } else {
                filterStr = filterStr.concat(" and price = " + price.get().toString());
            }
        }
        if (unitofmeasure.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("unitofmeasure = '" + unitofmeasure.get() + "'");
            } else {
                filterStr = filterStr.concat(" and unitofmeasure = '" + unitofmeasure.get() + "'");
            }
        }
        if (manufacturer.isPresent()) {
            if (filterStr.isEmpty()) {
                filterStr = filterStr.concat("manufacturer = " + manufacturer.get().toString());
            } else {
                filterStr = filterStr.concat(" and manufacturer = " + manufacturer.get().toString());
            }
        }
        List<Product> products = Product.where(filterStr);
        return products;
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
        String filterByXStr = request.getParameter("filter-by-x");
        String filterByYStr = request.getParameter("filter-by-y");
        String filterByDateStr = request.getParameter("filter-by-date");
        String filterByPriceStr = request.getParameter("filter-by-price");
        String filterByUnitofmeasureStr = request.getParameter("filter-by-unitofmeasure");
        String filterByManufacturerStr = request.getParameter("filter-by-manufacturer");
        String sortBy = request.getParameter("sortBy");

        Boolean filter = false;

        if (filterByIdStr != null
                || filterByNameStr != null
                || filterByXStr != null
                || filterByYStr != null
                || filterByDateStr != null
                || filterByPriceStr != null
                || filterByUnitofmeasureStr != null
                || filterByManufacturerStr != null ) {
            filter = true;
        }

        if (pathInfo == null) {
            // return list of product
            PrintWriter writer = response.getWriter();

            LazyList<Product> products;
            List<Product> prds;
            if (filter) {
                prds = filterProductsDB(filterByIdStr, filterByNameStr, filterByXStr, filterByYStr, filterByDateStr, filterByPriceStr, filterByUnitofmeasureStr, filterByManufacturerStr);
                if (prds.isEmpty()) {
                    response.setStatus(404);
                    Base.close();
                    return;
                }
            } else {
                prds = Product.findAll();
            }
            products = (LazyList<Product>) prds;
            if (itemsPerPageStr == null) {
                if (sortBy == null) {
                    writer.println(products.toXml(true, true));
                } else {
                    List<String> sortByFields = Arrays.asList("id", "name", "x", "y", "creationdate", "price", "unitofmeasure", "manufacturer");
                    if (sortByFields.contains(sortBy)){
                        writer.println(products.orderBy(sortBy).toXml(true, true));
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
                }
                catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                    response.setStatus(422);
                    Base.close();
                    return;
                }
//                writer.println(products.offset(itemsPerPage * (page - 1)).limit(itemsPerPage).toXml(true, true));

                List<String> sortByFields = Arrays.asList("id", "name", "x", "y", "creationdate", "price", "unitofmeasure", "manufacturer");
                if (sortByFields.contains(sortBy)){
                    writer.println(products.orderBy(sortBy).offset(itemsPerPage * (page - 1)).limit(itemsPerPage).toXml(true, true));
                } else {
                    response.setStatus(404);
                    Base.close();
                    return;
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
                List<Product> prds = Product.where("id = ?", id.get());
                PrintWriter writer = response.getWriter();
                if (!prds.isEmpty()){
                    writer.println(prds.get(0).toXml(true, true));
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

        response.addHeader("Access-Control-Allow-Origin", "*");
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
                List<Product> prds = Product.where("id = ?", id.get());
                if (!prds.isEmpty()){
                    prds.get(0).delete();
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
        response.addHeader("Access-Control-Allow-Origin", "*");
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
                List<Product> prds = Product.where("id = ?", id.get());
                if (!prds.isEmpty()){

                    String name = request.getParameter("name");
                    String x = request.getParameter("x");
                    String y = request.getParameter("y");
                    String price = request.getParameter("price");
                    String unitofmeasure = request.getParameter("unitofmeasure");
                    String manufacturer = request.getParameter("manufacturer");

                    prds.get(0).update(name, x, y, price, unitofmeasure, manufacturer);

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
