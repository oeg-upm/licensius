<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <title>Licensius Services</title>
        <link type="text/css" rel="stylesheet" href="/stylesheets/vroddon.css" /> <!-- /stylesheets/main.css -->
    </head>

    <body>
        <!-- <img style="float: left; margin: 5px 5px 5px 5px;" src="rdf-condicional.png" width="100" /> -->
        <img src="rdf-condicional.png"/>
        <h1>Licensius services</h1>
        
        <h2>Services description</h2>
        <p>This page allows testing some of the Licensius Services, a set of services for handling (RDF) licenses.</p>
        
        <p>You can check the API documentation <a href="http://cosasbuenas.es/static/licensius/"> here </a>.</p>
        <p>An example on how to invoke a Service if you like Java programming can be found <a href="http://www.cosasbuenas.es/blog/getLicense">here</a>.</p>
        
        
        
        <h2>Test the services here!</h2>
        
        <h3>1. Verify the license of a given ontology (or dataset)</h3> 


        <p>Find a license in a RDF or OWL file. Please introduce the URL here</p>
        <p>Examples:</p>
        <ul>
            <li>http://www.geonames.org/ontology/ontology_v3.1.rdf</li>
            <li>http://purl.org/goodrelations/v1.owl</li>
            <li>http://datos.bne.es/resource/XX947766</li>
        </ul>
        <form action="/getLicense" method="post">
            <div><textarea name="content" rows="1" cols="60"></textarea></div>
            <div><input type="submit" value="Find license in RDF" /></div>
        </form>
        <p>${message1}</p>        
        
        <br></br>
        <h3>2. Look for links to known licenses in HTML pages</h3> 
        <p>Examples:</p>
        <ul>
            <li>http://www.flickr.com/photos/greenwichphotography/5015528201/</li>
            <li>http://www.lacocinadivertida.com/</li>
            <li>http://ocw.upm.es/algebra/</li>
        </ul>


        <form action="/licensewebfinder" method="post">
            <div><textarea name="contentweb" rows="1" cols="60"></textarea></div>
            <div><input type="submit" value="Find license in HTML" /></div>
        </form>

        <p>${message}</p>
        <p></p>

        <h3>3. Compute the license of a resource composed of differently licensed resources</h3> 
        <p>Examples:</p>
        <ul>
            <li>CreativeCommons BY + CreativeCommons BY-SA = CreativeCommons BY-SA</li>
        </ul>
        
        
        <form action="/licensecomposer" method="post">
            <select name="lic1">
                <option value="CC0">Creative Commons Zero</option>
                <option value="CC-BY">Creative Commons BY</option>
                <option value="CC-BY-SA">Creative Commons BY-SA</option>
                <option value="CC-BY-NC">Creative Commons BY-NC</option>
                <option value="CC-BY-ND">Creative Commons BY-ND</option>
                <option value="CC-BY-NC-ND">Creative Commons BY-NC-ND</option>
                <option value="CC-BY-NC-SA">Creative Commons BY-NC-SA</option>
            </select>        
            <select name="lic2">
                <option value="CC0">Creative Commons Zero</option>
                <option value="CC-BY">Creative Commons BY</option>
                <option value="CC-BY-SA">Creative Commons BY-SA</option>
                <option value="CC-BY-NC">Creative Commons BY-NC</option>
                <option value="CC-BY-ND">Creative Commons BY-ND</option>
                <option value="CC-BY-NC-ND">Creative Commons BY-NC-ND</option>
                <option value="CC-BY-NC-SA">Creative Commons BY-NC-SA</option>
            </select>        
            <div><input type="submit" value="Find license of composed resource" /></div>
        </form>

        <p>${message3}</p>
        <br/><br/>
        <h3>4. Find the URI for a well-known license from an incomplete text</h3> 
        <p>Examples:</p>
        <ul>
            <li>"Creative Commons BY"</li>
            <li>LGPL</li>
            <li>ODC-BY</li>
        </ul>


        <form action="/licenseguess" method="post">
            <div><textarea name="txt" rows="1" cols="60"></textarea></div>
            <div><input type="submit" value="Guess license URI" /></div>
        </form>

        <p>${message4}</p>
        <p></p>
        
        

        <hr/>
        <div style="text-align: center">
            <a href="http://www.oeg-upm.net"><img src="oeg100.png" width="100" /></a>
        </div>        
        <p><small><strong>Disclaimer</strong>__: This is an experimental service. The results given are merely informative and cannot be trusted to correspond to the actual license of the queried ontologies or datasets.</small></p>
    </body>
</html>
