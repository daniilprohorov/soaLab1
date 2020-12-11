import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;

import java.util.List;
import java.util.Optional;

public class Product extends Model {
    // validation
    public Boolean init(String name, String x, String y, String price, String unitofmeasure, String manufacturer) {
        Optional nameOptional = nameIsValid(name);
        Optional xOptional = xIsValid(x);
        Optional yOptional = yIsValid(y);
        Optional priceOptional = priceIsValid(price);
        Optional unitofmeasureOptional = unitofmeasureIsValid(unitofmeasure);
        Optional manufacturerOptional = manufacturerIsValid(manufacturer);
        if (nameOptional.isPresent() && xOptional.isPresent() && yOptional.isPresent() && priceOptional.isPresent() && unitofmeasureOptional.isPresent() && manufacturerOptional.isPresent() ) {
            this.setString("name", nameOptional.get());
            this.set("x", xOptional.get());
            this.set("y", yOptional.get());
            this.setInteger("price", priceOptional.get());
            this.setString("unitofmeasure", unitofmeasureOptional.get());
            this.set("manufacturer", manufacturerOptional.get());
            this.setDate("creationdate", java.time.LocalDate.now().toString());
            this.saveIt();
            return true;
        } else {
            return false;
        }
    }
    public String getName(){
        return getString("name");
    }
    public Optional<String> nameIsValid(String name) {
        if (!name.isEmpty()) {
            return Optional.of(name);
        } else {
            return Optional.empty();
        }
    }
    public Optional<Long> xIsValid(String x) {
        if (!x.isEmpty()) {
            try {
                Long xl = Long.valueOf(x);
                return Optional.of(xl);
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
    public Optional<Float> yIsValid(String y) {
        if (!y.isEmpty()) {
            try {
                Float yf = Float.valueOf(y);
                if (yf > -400) {
                    return Optional.of(yf);
                } else {
                    return Optional.empty();
                }
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
    public Optional<Integer> priceIsValid(String price) {
        if (!price.isEmpty()) {
            try {
                Integer pricei = Integer.valueOf(price);
                return Optional.of(pricei);
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
    public Optional<String> unitofmeasureIsValid(String unitofmeasure) {
        if (!unitofmeasure.isEmpty()) {
            if (unitofmeasure.equals("kilograms") || unitofmeasure.equals("centimeters") || unitofmeasure.equals("square_meters") || unitofmeasure.equals("pcs")) {
                return Optional.of(unitofmeasure);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }

    }
    public Optional<Integer> manufacturerIsValid(String manufacturer) {
        if (!manufacturer.isEmpty()) {
            try {
                Integer mi = Integer.valueOf(manufacturer);
                List<Organization> resultList = Organization.where("id = ?", mi);
                if (!resultList.isEmpty()) {
                    return Optional.of(mi);

                } else {
                    return Optional.empty();
                }
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}