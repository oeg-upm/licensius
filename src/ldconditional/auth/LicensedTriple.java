package ldconditional.auth;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.StringWriter;
import ldconditional.handlers.HandlerResource;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import languages.LanguageManager;

import ldconditional.LDRConfig;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.Grafo;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.RDFUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 * This class represents a RDF statement that has been licensed.
 * A RDF stati
 * @author Victor
 */
public class LicensedTriple {


    public Statement stmt;
    public String g;

    public LicensedTriple() {
    }

    public LicensedTriple(LicensedTriple lt) {
        stmt = lt.stmt;
        g = lt.g;
    }

    public LicensedTriple(String s, String p, String o, String l, String g) {
        Model m = ModelFactory.createDefaultModel();
        RDFNode n = null;
//        if (RDFUtils.isURI(o)) {
        if (o.startsWith("http")) {
            n = m.createResource(o);
        } else {
            if (l.isEmpty())
                n = m.createLiteral(o);
            else
                n = m.createLiteral(o, l);
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
     * Writes a HTML line with a formatted LDR triple
     * @param lan The language
     */
    public String toHTMLRowNew(ConditionalDataset cd) {
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
            String texto = "";// "Limited access";//limited access

            //If the object is a policy, consider the following
            if (hasPolicyAsObject()) {
                Policy policy = PolicyManagerOld.getPolicy(sobjetooriginal);
                if (policy == null) {
                    System.out.println("La politica " + sobjetooriginal + " no se ha encontrado");
                    System.out.println("Y mira que teníamos...");
                    for (Policy px : PolicyManagerOld.getPolicies()) {
                        System.out.print(" " + px.getURI());
                    }
                    return texto;
                }
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
                o = "<a href=\"" + link + "\"><span class=\"label label-danger\">Offer</span> </a>";

//                o = "<font color=\"red\">" + texto + "</font> (<a target=\"_blank\" href=\"" + link + "\">Offer";
                if (bperunit) {
                    o += " -- Per RDF Statement";
                }
                o += "</a>";
            } else if (isForbidden()) {
                o = "<font color=\"red\">Información de pago</font>";
            } else {
                if (stmt.getObject().asResource().getURI().startsWith("http")) {
                    String uri = stmt.getObject().asResource().getURI();
                    boolean isExternal = !uri.startsWith(LDRConfig.getServer());
                    o = "<a href=\"" + uri + "\">" + sobjeto + "</a>";
                    if (isExternal)
                        o+="<span class=\"glyphicon glyphicon-share-alt\"></span>";
                } else {
                    o = sobjeto;
                }

            }
        }
        if (stmt.getObject().isLiteral()) {
            String oliteral=stmt.getObject().asLiteral().toString();
            String sobjeto = stmt.getObject().asLiteral().getString();
            String language = stmt.getObject().asLiteral().getLanguage();
                LanguageManager lmgr = new LanguageManager();
                String lan2 = lmgr.mapa32.get(language);
                if (!language.isEmpty())
                    sobjeto += " (" + language + ")"; //languaje
                o = sobjeto;
        }

        String tipo = "info";
        if (hasPolicyAsObject()) {
            tipo = "danger";
        }
        if (pagado==true)
            tipo = "primary";

        String str = "";
        str += "<tr class=\"" + tipo + "\"><td>" + p + "</td><td>" + o + "</td></tr>\n";

        List<String> expandir = cd.getDatasetVoid().getMetadataValues("http://www.w3.org/ns/ldp#hasMemberRelation"); //http://purl.oclc.org/NET/cld/propertyExpand
        for(String expandido: expandir)
        {
            String predicado=stmt.getPredicate().toString();
            if (expandido.equals(predicado))
            {
                str= "<tr class=\"" + tipo + "\"><td>" + p + "</td><td>";
                str+=o;
                String objeto = stmt.getObject().asResource().toString();
                //obtener triples
                
                List<LicensedTriple> subtriples = cd.getLicensedTriples(objeto);
                if (subtriples.size()>0)
                {
                    str+="<table class=\"table table-condensed\">";
                }
                for(LicensedTriple subtriple : subtriples)
                {
                    String spropi = RDFUtils.replaceWithPrefix(subtriple.stmt.getPredicate().toString());
                    String svalor = subtriple.stmt.getObject().toString();
                    if (svalor.startsWith("http://"))
                    {
                        svalor="<a href=\""+svalor+"\">"+RDFUtils.replaceWithPrefix(svalor)+"</a>";
                        if (!svalor.startsWith(LDRConfig.getServer()))
                            svalor+="<span class=\"glyphicon glyphicon-share-alt\"></span>";
                    }
                    else
                    {
                        String language = subtriple.stmt.getObject().asLiteral().getLanguage();
                        if (language!=null && !language.isEmpty())
                            svalor=subtriple.stmt.getObject().asLiteral().getString()+" ("+language+")";
                    }
                    str+="<tr class=\"info\"><td width=\"30%\">"+spropi+"</td><td width=\"70%\">"+svalor+"</td></tr>";
                }
                if (subtriples.size()>0)
                {
                    str+="</table>";
                }
                
                str += "</td></tr>\n";
            }
        }
        
					
					       
        

        return str;
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
            AuthorizationResponse ar = Authorizer.AuthorizeStatement(lt, portfolio.policies);
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
    public boolean pagado = false;
    public static List<LicensedTriple> Authorize2(ConditionalDataset cd, List<LicensedTriple> lst, Portfolio portfolio) {
        List<LicensedTriple> validos = new ArrayList();

        List<Grafo> lg = cd.getDatasetVoid().getGrafos();

        for (LicensedTriple lt : lst) {
            Grafo grafo = HandlerResource.getGrafo(lg, lt.g);
            if (grafo == null) {
                validos.add(lt);    //POLITICA POR DEFECTO: INCLUIR LOS QUE NO ESTAN EN NINGUN GRAFO DENTRO DE LA SOLCUION O NO. BORRAR ESTA LINEA O NO.
                continue;
            }
            if (grafo.isOpen()) {
                validos.add(lt);
                continue;
            }
            AuthorizationResponse ar = new AuthorizationResponse();
            ar.ok = false;
            ar.policies.clear();
            for (Policy policy : portfolio.policies) {
                if (policy.hasPlay() && policy.hasFirstTarget(lt.g)) {
                    ar.ok = true;
                    ar.policies.add(policy);
                    lt.pagado=true;
                    validos.add(lt);
                    continue;
                }
            }
            if (ar.ok == false) {
                List<LicensedTriple> llt = LicensedTriple.concealInformation(lt, grafo.getPolicies());
                validos.addAll(llt);
            }
        }

        return validos;
    }
    
    public static String getNTriples(List<LicensedTriple> llt) {
        Model minimodel = ModelFactory.createDefaultModel();
        for(LicensedTriple lt : llt)
        {
            minimodel.add(lt.stmt);
        }
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, minimodel, Lang.NTRIPLES);;
        return sw.toString();
    }
    public static Model getModel(List<LicensedTriple> llt) {
        Model minimodel = ModelFactory.createDefaultModel();
        for(LicensedTriple lt : llt)
        {
            minimodel.add(lt.stmt);
        }
        return minimodel;
    }    
    
    public static Comparator<LicensedTriple> PREDICATECOMPARATOR = new Comparator<LicensedTriple>() {

        public int compare(LicensedTriple o1, LicensedTriple o2) {
            String s1 = o1.stmt.getPredicate().toString();
            String s2 = o2.stmt.getPredicate().toString();
            return s1.compareTo(s2);
        }
    };
}
