package ldconditional.ldserver;

import ldconditional.Main;
import org.json.simple.JSONObject;


/**
 *
 * @author Victor
 */
public class Oferta {
    
    private String license_title = "";
    private String license_price ="";
    private String license_link ="";

    
    
    public static void main(String[] args) throws Exception {
        Main.initLogger();
        
        Oferta offer = new Oferta();
        offer.setLicense_title("Open Data Commons ODC-BY");
        offer.setLicense_link("http://www.opendatacommons.org/licenses/by/");
        offer.setLicense_price("Open Data");
        System.out.println(offer.toJSON().toJSONString());
    }
 
    public JSONObject toJSON()
    {
        JSONObject pol = new JSONObject();
        pol.put("title", license_title);
        pol.put("link", license_link);
        pol.put("price", license_price);
        return pol;
    }

    /**
     * @return the license_title
     */
    public String getLicense_title() {
        return license_title;
    }

    /**
     * @param license_title the license_title to set
     */
    public void setLicense_title(String license_title) {
        this.license_title = license_title;
    }


    /**
     * @return the license_price
     */
    public String getLicense_price() {
        return license_price;
    }

    /**
     * @param license_price the license_price to set
     */
    public void setLicense_price(String license_price) {
        this.license_price = license_price;
    }

    /**
     * @return the license_link
     */
    public String getLicense_link() {
        return license_link;
    }

    /**
     * @param license_link the license_link to set
     */
    public void setLicense_link(String license_link) {
        this.license_link = license_link;
    }
    
    
}
