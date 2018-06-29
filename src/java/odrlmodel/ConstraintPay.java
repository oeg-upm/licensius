package odrlmodel;

import java.net.URI;

/**
 *
 * @author Victor
 */
public class ConstraintPay extends Constraint {

    public Double amount=null;
    public String currency=null;
    public String good = ""; 
    public int amountOfThisGood = 1;
//    public boolean perUnit=false;

    public String toString()
    {
        String str="";
        
        str+=String.format("Pay %.02f %s ", amount, currency);
        String sgood = new String(good);
        try{
            URI uri = new URI(good);
            String path = uri.toString();
            String idStr = path.substring(path.lastIndexOf('#') + 1);            
            sgood = idStr;
        }
        catch(Exception e){}
        
        str+="for " + amountOfThisGood + " "+ sgood;
        return str;
    }
    
    
    public ConstraintPay()
    {
        super();
    }

    public ConstraintPay(Constraint copia)
    {
        super(copia);
    }
    
   
    public ConstraintPay(ConstraintPay copia)
    {
        super(copia);
        amount=copia.amount;
        currency=copia.currency;
        good = copia.good;
        amountOfThisGood = copia.amountOfThisGood;
    }
    
    public void setPayment(Double _amount, String _currency, String _good, int _amountOfThisGood)
    {
        amount=_amount;
        currency=_currency;
        good = _good;
        amountOfThisGood = _amountOfThisGood;
    }

    /**
     * Retrieves a string with the price
     */
    public String getPriceString()
    {
        String slabel=getLabel("en");
        if (slabel==null)
            return "";
        
        if (slabel.equals("pay") || slabel.equals("Pay") || (action!=null && action.equals("http://www.w3.org/ns/odrl/2/pay")))
        {
            String pago = String.format("%.2f %s", amount, currency);
            return pago;
        }
        return "";
    }
    
    /**
     * Devuelve true si el pago es por RDF Triple.
     */
    public boolean isPerTriple()
    {
        if (good.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement"))
            return true;
        return false;
    }
    

    
    public boolean isOpen() {
        return false;
    }
    
}
