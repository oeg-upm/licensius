package model;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import odrlmodel.Policy;

/**
 * Represents a dataset described in the void.ttl file.
 * @author vroddon
 */
public class Grafo {
    String uri = "";
    DatasetVoid dp = null;

    public Grafo(DatasetVoid _dp, String _uri)
    {
        uri = _uri;
        dp = _dp;
    }

    public DatasetVoid getDatasetVoid()
    {
        return dp;
    }

    public void setDatasetVoid(DatasetVoid _dp)
    {
        dp = _dp;
    }

    public List<Policy> getPolicies()
    {
        return dp.getPolicies(uri);
    }
    public String toString()
    {
        return uri;
    }

    public String getURI()
    {
        return uri;
    }

    public String getName()
    {
        int index = uri.lastIndexOf("/");
        return "";
    }

    public String getComment() {
        NodeIterator rit = dp.model.listObjectsOfProperty(dp.model.createResource(uri),RDFS.comment);
        if (rit.hasNext())
            return rit.next().toString();
        return "";
    }
    public String getLabel() {
        NodeIterator rit = dp.model.listObjectsOfProperty(dp.model.createResource(uri),RDFS.label);
        if (rit.hasNext())
            return rit.next().toString();
        return "";
    }


    public int getNumTriples() {
        NodeIterator rit = dp.model.listObjectsOfProperty(dp.model.createResource(uri),dp.model.createProperty("http://rdfs.org/ns/void#triples"));
        if (rit.hasNext())
        {
            String s = rit.next().toString();
            return Integer.parseInt(s);
        }
        return 0;

    }
    public List<Policy> getOpenPolicies() {
        List<Policy> llp = new ArrayList();
        List<Policy> lp = getPolicies();
        for(Policy p : lp)
        {
            if (p.isOpen())
                llp.add(p);
        }
        return llp;
    }
    public List<Policy> getPoliciesForMoney() {
        List<Policy> llp = new ArrayList();
        List<Policy> lp = getPolicies();
        for(Policy p : lp)
        {
            if (p.isInOffer())
                llp.add(p);
        }
        return llp;
    }
    public boolean isOpen() {
        List<Policy> lp = getPolicies();
        for(Policy p : lp)
        {
            if (p.isOpen())
                return true;
        }
        return false;
    }



}
