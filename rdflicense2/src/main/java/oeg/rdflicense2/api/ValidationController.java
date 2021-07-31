package oeg.rdflicense2.api;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;

/**
 *
 * @author vroddon
 */
@Controller
public class ValidationController {

    @RequestMapping("/validate")
    public String page(Model model) {
        model.addAttribute("attribute", "value");
        return "view.name";
    }

    public static boolean validate(String xml) {
        try {
            System.out.println("Vamos a validar!");
            File fXmlFile = new File("/path/to/my.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

}
