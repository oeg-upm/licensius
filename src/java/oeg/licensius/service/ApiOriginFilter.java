
package oeg.licensius.service;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

/**
 
   <filter>
    <filter-name>ApiOriginFilter</filter-name>
    <filter-class>io.swagger.api.ApiOriginFilter</filter-class>
  </filter>
  <filter-mapping>
    <filter-name>ApiOriginFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>

 
 */
public class ApiOriginFilter implements javax.servlet.Filter {
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		res.addHeader("Access-Control-Allow-Headers", "Content-Type");
		chain.doFilter(request, response);
	}

	public void destroy() {}

	public void init(FilterConfig filterConfig) throws ServletException {}
}
/**
 */