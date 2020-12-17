import org.javalite.activejdbc.Model;

import java.util.Optional;

public class Organization extends Model {
    // validation
    public Boolean init(String name, String fullname, String employeescount) {
        Optional nameOptional = nameIsValid(name);
        Optional fullnameOptional = fullnameIsValid(fullname);
        Optional employeescountOptional = employeescountIsValid(employeescount);
        if (nameOptional.isPresent() && fullnameOptional.isPresent() && employeescountOptional.isPresent()) {
            this.setString("name", nameOptional.get());
            this.setString("fullname", fullnameOptional.get());
            this.set("employeescount", employeescountOptional.get());
            this.saveIt();
            return true;
        } else {
            return false;
        }
    }

    public void update(String name, String fullname, String employeescount) {
        Optional nameOptional = nameIsValid(name);
        Optional fullnameOptional = fullnameIsValid(fullname);
        Optional employeescountOptional = employeescountIsValid(employeescount);
        if (nameOptional.isPresent()) {
            this.setString("name", nameOptional.get());
        }
        if (fullnameOptional.isPresent()) {
            this.setString("fullname", fullnameOptional.get());
        }
        if (employeescountOptional.isPresent() ) {
            this.set("employeescount", employeescountOptional.get());
        }
        this.save();
    }

    public Optional<Integer> idIsValid(String id) {
        if (id != null && !id.isEmpty()) {
            try {
                Integer idI = Integer.valueOf(id);
                if (idI > 0) {
                    return Optional.of(idI);
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
    public Optional<String> nameIsValid(String name) {
        if (name != null && !name.isEmpty()) {
            return Optional.of(name);
        } else {
            return Optional.empty();
        }
    }
    public Optional<String> fullnameIsValid(String fullname) {
        if (fullname != null && !fullname.isEmpty() && fullname.length() <= 1858) {
            return Optional.of(fullname);
        } else {
            return Optional.empty();
        }
    }
    public Optional<Long> employeescountIsValid(String employeescount) {
        if (employeescount != null && !employeescount.isEmpty()) {
            try {
                Long employeescountL = Long.valueOf(employeescount);
                if (employeescountL > 0) {
                    return Optional.of(employeescountL);
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
