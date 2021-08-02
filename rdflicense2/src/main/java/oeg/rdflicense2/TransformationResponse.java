package oeg.rdflicense2;

/**
 * Result of a transformation, including:
 * @param valid Whether there was an error or not
 * @param transformation Result of the transformation, or an error message.
 * @author vroddon
 */
public class TransformationResponse {
    public TransformationResponse(boolean v, String r)
    {
        valid = v;
        transformation = r;
    }
    public boolean valid = true;
    public String transformation = "";
    
}
