
package odrlmodel;

/**
 *
 * @author Victor
 */
public class ConstraintLocation  extends Constraint {

    public String location;
    public String toString()
    {
        return "Only for the location:" + location;
    }
    public ConstraintLocation()
    {
        super();
    }

    public ConstraintLocation(Constraint copia)
    {
        super(copia);
    }
    
   
    public ConstraintLocation(ConstraintLocation copia)
    {
        super(copia);
        location = copia.location;
    }
}
