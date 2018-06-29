/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package odrlmodel;

/**
 *
 * @author Victor
 */
public class ConstraintIndustry extends Constraint{
    public String industry;
    
    public String toString()
    {
        return "Only for the industry: " + industry;
    }
    public ConstraintIndustry()
    {
        super();
    }

    public ConstraintIndustry(Constraint copia)
    {
        super(copia);
    }
    
   
    public ConstraintIndustry(ConstraintIndustry copia)
    {
        super(copia);
        industry = copia.industry;
    }    
}
