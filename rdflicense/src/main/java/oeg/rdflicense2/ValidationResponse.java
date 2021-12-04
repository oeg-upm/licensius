package oeg.rdflicense2;

/**
 *
 * @author vroddon
 */
public class ValidationResponse {
    public ValidationResponse(boolean v, String r)
    {
        valid = v;
        reason = r;
    }
    public boolean valid = true;
    public String reason = "";
}
