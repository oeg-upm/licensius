package ldc.auth;

import java.util.ArrayList;
import java.util.List;
import odrlmodel.Policy;

/**
 * This is the result of an authorization request
 * Botones de: http://tympanus.net/Tutorials/AnimatedButtons/index2.html 
 * y de http://hellohappy.org/css3-buttons/ y de http://www.hongkiat.com/blog/css3-button-tutorials/

 * @author Victor
 */
public class AuthorizationResponse {

    //Whether the request can be satisfied or not
    public boolean ok;
    
    //Policies that give access to this resource, 
    public List<Policy> policies = new ArrayList();
}
