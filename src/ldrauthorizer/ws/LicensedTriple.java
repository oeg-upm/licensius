package ldrauthorizer.ws;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import handlers.HandlerResource;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import languages.LanguageManager;
import languages.Multilingual;

import ldconditional.LDRConfig;
import model.ConditionalDataset;
import model.Grafo;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.RDFUtils;

/**
 * This class represents a RDF statement that has been licensed.
 * A RDF stati
 * @author Victor
 */
public class LicensedTriple {

    Statement stmt;
    public String g;

    public LicensedTriple() {
    }

    public LicensedTriple(LicensedTriple lt) {
        stmt = lt.stmt;
        g = lt.g;
    }

    public LicensedTriple(String s, String p, String o, String g) {
        Model m = ModelFactory.createDefaultModel();
        RDFNode n = null;
        if (RDFUtils.isURI(o)) {
            n = m.createResource(o);
        } else {
            n = m.createLiteral(o);
        }
        stmt = m.createStatement(m.createResource(s), m.createProperty(p), n);
        this.g = g;
    }

    /**
     *  Ofrece una versión como cadena de este triple
     */
    public String toString() {
        String str = "";

        String s = stmt.getSubject().getURI();
        String p = stmt.getPredicate().asResource().getURI();
        String o = stmt.getObject().isResource() ? stmt.getObject().asResource().getURI() : stmt.getObject().asLiteral().getString();

        str += s + "\t" + p + "\t" + o + "\t" + g;
        return str;
    }

    /**
     * Determines whether the object is a policy
     */
    public boolean hasPolicyAsObject() {
        boolean bPolicy = false;
        if (stmt.getObject().isResource()) {
            RDFNode r = stmt.getObject();
            String sobjetooriginal = stmt.getObject().asResource().getURI();
            if (sobjetooriginal.contains("policy/") || sobjetooriginal.contains("license/")) {
                return true;
            }
        }
        if (stmt.getObject().isAnon()) {
            //    System.out.println("XXXXXXXXXXXXXXX");
        }
        return bPolicy;
    }

    public boolean isForbidden() {
        if (stmt.getObject().isResource()) {
            String sobjetooriginal = stmt.getObject().asResource().getURI();
            if (sobjetooriginal.equals("http://purl.oclc.org/NET/ldr/ns#ResourceNotAuthorized")) {
                return true;
            }
        }
        return false;
    }

    public boolean isOpen() {
        if (!isForbidden() && !hasPolicyAsObject()) {
            return true;
        }
        return false;
    }

    public String toChurroURI() {
        try {
            String s1 = URLEncoder.encode(stmt.getSubject().getURI(), "UTF-8");
            String s2 = URLEncoder.encode(stmt.getPredicate().getURI(), "UTF-8");
            return s1 + "xxxxx" + s2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String toChurro() {
        try {
            String s1 = stmt.getSubject().getURI();
            String s2 = stmt.getPredicate().getURI();
            return s1 + "xxxxx" + s2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static LicensedTriple fromChurro(String churro) {
        LicensedTriple lt = new LicensedTriple();

        try {
            String dos[] = churro.split("xxxxx");
            if (dos.length != 2) {
                return null;
            }
            String s1 = URLDecoder.decode(dos[0], "UTF-8");
            String s2 = URLDecoder.decode(dos[1], "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return lt;

    }

    /**
     * Writes a formatted LDR triple
     * @param lan The language
     */
    public String toHTMLRow(String lan) {
        String str = "";

        String p = stmt.getPredicate().getLocalName();
        String o = "";

        if (stmt.getObject().isResource()) {
            String sobjetooriginal = stmt.getObject().asResource().getURI();
            String sobjeto = stmt.getObject().asResource().getURI();
            try {
                sobjeto = URLDecoder.decode(sobjeto, "UTF-8");
                int index1 = sobjeto.lastIndexOf("/");
                int index2 = sobjeto.lastIndexOf("#");
                int index = Math.max(index1, index2);
                sobjeto = sobjeto.substring(index + 1);
            } catch (Exception e) {
            }
            String texto = "Limited access";//limited access

            //If the object is a policy, consider the following
            if (hasPolicyAsObject()) {
                Policy policy = PolicyManagerOld.getPolicy(sobjetooriginal);
                boolean bperunit = policy.isPerTriple();
                String euri = "";
                try {
                    euri = URLEncoder.encode(policy.getURI(), "UTF-8");
                } catch (Exception e) {
                }

                String link = sobjetooriginal;
                String starget = policy.getFirstTarget();
                link = "/service/managePolicy?action=View&licencia=" + euri;
                if (!starget.isEmpty()) {
                    link += "?target=" + starget;
                }

                if (bperunit) {
                    link = sobjetooriginal + "?target=" + toChurroURI();
                }
                o = "<font color=\"red\">" + texto + "</font> (<a target=\"_blank\" href=\"" + link + "\">Offer";
                if (bperunit) {
                    o += " -- Per RDF Statement";
                }
                o += "</a>)";
            } else if (isForbidden()) {
                o = "<font color=\"red\">Información de pago</font>";
            } else {
                if (stmt.getObject().asResource().getURI().startsWith("http")) {
                    o = "<a href=\"" + stmt.getObject().asResource().getURI() + "\">" + sobjeto + "</a>";
                } else {
                    o = sobjeto;
                }

            }
        }
        if (stmt.getObject().isLiteral()) {
            String sobjeto = stmt.getObject().asLiteral().getString();
            String language = stmt.getObject().asLiteral().getLanguage();

            try {
                sobjeto = URLDecoder.decode(sobjeto, "UTF-8");
                int index1 = sobjeto.lastIndexOf("/");
                int index2 = sobjeto.lastIndexOf("#");
                int index = Math.max(index1, index2);
                if (index != -1) {
                    sobjeto = sobjeto.substring(index);
                }

                if (language != null && !language.isEmpty()) {
                    LanguageManager lmgr = new LanguageManager();
                    String lan2 = lmgr.mapa32.get(language);
                    //       if (lan2==null)
                    sobjeto += " (" + language + ")";
                    //     else
                    //     {
                    //         sobjeto+=" <img src=\"./img/"+lan2+".png\"> \n";
                    //     }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            o = sobjeto;
        }


        str += "<tr><td>" + p + "</td><td>" + o + "</td></tr>\n";

        return str;
    }

    /**
     * Writes a formatted LDR triple
     * @param lan The language
     */
    public String toHTMLRowNew(ConditionalDataset cd) {
        String str = "";
        String p = stmt.getPredicate().getLocalName();
        String o = "";

        if (stmt.getObject().isResource()) {
            String sobjetooriginal = stmt.getObject().asResource().getURI();
            String sobjeto = stmt.getObject().asResource().getURI();
            try {
                sobjeto = URLDecoder.decode(sobjeto, "UTF-8");
                int index1 = sobjeto.lastIndexOf("/");
                int index2 = sobjeto.lastIndexOf("#");
                int index = Math.max(index1, index2);
                sobjeto = sobjeto.substring(index + 1);
            } catch (Exception e) {
            }
            String texto = "Limited access";//limited access

            //If the object is a policy, consider the following
            if (hasPolicyAsObject()) {
                Policy policy = PolicyManagerOld.getPolicy(sobjetooriginal);
                boolean bperunit = policy.isPerTriple();
                String euri = "";
                String sg = "";
                try {
                    euri = URLEncoder.encode(policy.getURI(), "UTF-8");
                } catch (Exception e) {
                }
                try {
                    sg = URLEncoder.encode(g, "UTF-8");
                } catch (Exception e) {
                }

                String link = sobjetooriginal;
                String starget = policy.getFirstTarget();
                String ser = LDRConfig.getServer();
                link = ser + cd.name + "/manageren/managePolicy?action=View&licencia=" + euri;
                if (!starget.isEmpty()) {
                    link += "&target=" + starget;
                }
                link += "&selectedGrafo=" + sg;
                if (bperunit) {
                    link = sobjetooriginal + "?target=" + toChurroURI();
                }
                link += "&target=" + sg;

                o = "<font color=\"red\">" + texto + "</font> (<a target=\"_blank\" href=\"" + link + "\">Offer";
                if (bperunit) {
                    o += " -- Per RDF Statement";
                }
                o += "</a>)";
            } else if (isForbidden()) {
                o = "<font color=\"red\">Información de pago</font>";
            } else {
                if (stmt.getObject().asResource().getURI().startsWith("http")) {
                    o = "<a href=\"" + stmt.getObject().asResource().getURI() + "\">" + sobjeto + "</a>";
                } else {
                    o = sobjeto;
                }

            }
        }
        if (stmt.getObject().isLiteral()) {
            String sobjeto = stmt.getObject().asLiteral().getString();
            String language = stmt.getObject().asLiteral().getLanguage();

            try {
                sobjeto = URLDecoder.decode(sobjeto, "UTF-8");
                int index1 = sobjeto.lastIndexOf("/");
                int index2 = sobjeto.lastIndexOf("#");
                int index = Math.max(index1, index2);
                if (index != -1) {
                    sobjeto = sobjeto.substring(index);
                }

                if (language != null && !language.isEmpty()) {
                    LanguageManager lmgr = new LanguageManager();
                    String lan2 = lmgr.mapa32.get(language);
                    //       if (lan2==null)
                    sobjeto += " (" + language + ")";
                    //     else
                    //     {
                    //         sobjeto+=" <img src=\"./img/"+lan2+".png\"> \n";
                    //     }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            o = sobjeto;
        }


        str += "<tr><td>" + p + "</td><td>" + o + "</td></tr>\n";

        return str;
    }

    /**
     * Dado un conjunto de triples licenciados, devuelve aquellos que son válidos.
     * Los que no lo son, se devuelven capados ("el objeto está oculto")
     */
    public static List<LicensedTriple> Authorize(List<LicensedTriple> lst) {
        List<LicensedTriple> validos = new ArrayList();

        for (LicensedTriple lt : lst) {
            String recurso = lt.g;
            AuthorizationResponse ar = ODRLAuthorizer.Authorize2(recurso);
            if (ar.ok == true) {
                validos.add(lt);
            } else {
                if (ar.policies.isEmpty()) {
                } else {
                    List<LicensedTriple> concealed = concealInformation(lt, ar.policies);
                    validos.addAll(concealed);
                    //           System.out.println(concealed);
                }
            }
        }

        return validos;
    }

    /**
     * Añade una política a un triple licenciado como predicado.
     */
    public static List<LicensedTriple> concealInformation(LicensedTriple lt, List<Policy> policies) {
        List<LicensedTriple> triples = new ArrayList();
        Model m = ModelFactory.createDefaultModel();
        Resource r = null;
        if (policies.isEmpty()) {
            r = m.createResource("http://purl.oclc.org/NET/ldr/ns#ResourceNotAuthorized");
            return triples;
        } else {
            for (Policy policy : policies) {
                LicensedTriple oculto = new LicensedTriple(lt);
                r = m.createResource(policy.getURI());
                Statement stmtoculto = m.createStatement(lt.stmt.getSubject(), lt.stmt.getPredicate(), r);
                oculto.stmt = stmtoculto;
                //aqui oculto debería tener la política...
                triples.add(oculto);
                //         System.out.println(oculto);
            }
        }
        return triples;
    }

    public static List<LicensedTriple> Authorize(List<LicensedTriple> lst, Portfolio portfolio) {
        List<LicensedTriple> validos = new ArrayList();


        for (LicensedTriple lt : lst) {
            String recurso = lt.g;
            AuthorizationResponse ar = ODRLAuthorizer.AuthorizeStatement(lt, portfolio.policies);
            if (ar.ok == true) {
                validos.add(lt);
            } else {
                if (ar.policies.isEmpty()) {
                } else {
                    List<LicensedTriple> concealed = concealInformation(lt, ar.policies);
                    validos.addAll(concealed);
                    //           System.out.println(concealed);
                }
            }
        }

        return validos;
    }

    public static List<LicensedTriple> Authorize2(ConditionalDataset cd, List<LicensedTriple> lst, Portfolio portfolio) {
        List<LicensedTriple> validos = new ArrayList();

        List<Grafo> lg = cd.getDatasetVoid().getGrafos();

        for (LicensedTriple lt : lst) {
            Grafo grafo = HandlerResource.getGrafo(lg, lt.g);
            if (grafo==null)
                continue;
            if (grafo.isOpen()) {
                validos.add(lt);
                continue;
            }
            AuthorizationResponse ar = new AuthorizationResponse();
            ar.ok=false;
            ar.policies.clear();
            for (Policy policy : portfolio.policies)
            {
                if (policy.isInOffer() && policy.hasFirstTarget(lt.g))
                {
                    ar.ok=true;
                    ar.policies.add(policy);
                    validos.add(lt);
                    continue;
                }
            }
            if (ar.ok==false)
            {
                List<LicensedTriple> llt = LicensedTriple.concealInformation(lt, grafo.getPolicies());
                validos.addAll(llt);
            }
        }

        return validos;
    }
    public static Comparator<LicensedTriple> PREDICATECOMPARATOR = new Comparator<LicensedTriple>() {

        public int compare(LicensedTriple o1, LicensedTriple o2) {
            String s1 = o1.stmt.getPredicate().toString();
            String s2 = o2.stmt.getPredicate().toString();
            return s1.compareTo(s2);
        }
    };
}
