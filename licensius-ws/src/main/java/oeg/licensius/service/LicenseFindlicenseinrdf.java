/*
Copyright (c) 2015 OEG

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.core.LicenseFinder;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusResponse;

/**
 *
 * @author vrodriguez
 */
public class LicenseFindlicenseinrdf extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LicenseFindlicenseinrdf.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getParameter("uri");
        LicenseFinder lf = new LicenseFinder();
        LicensiusResponse le = lf.findLicenseInRDF(uri);
        if (le.getClass().equals(LicensiusError.class)) {
            LicensiusError e = (LicensiusError) le;
            resp.setStatus(500);
            PrintWriter w = resp.getWriter();
            w.println(e.getJSON());
            return;
        }
        resp.setStatus(200);
        //resp.setContentType("application/json");
        resp.setContentType("text/html");
        PrintWriter w = resp.getWriter();
        w.println(le.getJSON());
        return;
    }

    /**
     * Sirve una cadena de prueba.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LicenseFinder lf = new LicenseFinder();
        String body = ServiceCommons.getBody(req);
        LicensiusResponse le = lf.findLicenseFromRDFText2(body);
        if (le.getClass().equals(LicensiusError.class)) {
            LicensiusError e = (LicensiusError) le;
            resp.setStatus(500);
            PrintWriter w = resp.getWriter();
            w.println(e.getJSON());
            return;
        }
        resp.setStatus(200);
        resp.setContentType("application/json");
        PrintWriter w = resp.getWriter();
        w.println(le.getJSON());
        return;
    }

    public static void main(String[] args) {
        String uri = "http://purl.org/NET/p-plan";
        LicenseFinder lf = new LicenseFinder();
        LicensiusResponse le = lf.findLicenseInRDF(uri);
        System.out.println(le.getJSON());

    }

}
