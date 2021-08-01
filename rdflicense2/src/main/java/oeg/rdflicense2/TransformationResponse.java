package oeg.rdflicense2;

/**
 *
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
