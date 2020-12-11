import org.javalite.activejdbc.Model;

public class Organization extends Model {
    public String getName(){
        return getString("name");
    }
}
