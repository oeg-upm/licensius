<%@page import="oeg.rdflicense.TestRDFLicense"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>RDFLicense, Dataset of licenses as RDF</title>
<meta content="text/html; charset=iso-8859-1" http-equiv="Content-Type/">
<meta property="dc:creator" content="Victor Rodriguez-Doncel">
<meta name="description" content="This is a collection of licenses written in RDF">
<meta name="author" content="Victor Rodriguez Doncel ">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

<meta name="dc.title" content="Licenses in RDF">
<link rel="stylesheet" type="text/css" href="style.css">
<link rel="icon" href="http://rdflicense.linkeddata.es/dataset/img/favicon.png" type="image/png">


<script async="" src="./analytics.js"></script><script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
  ga('create', 'UA-49063507-1', 'linkeddata.es');
  ga('send', 'pageview');
</script><style type="text/css"></style>
</head>


  <body>
<body> 
<div class="container"> 
<div class="sixteen columns">
<div class="sixteen columns alpha">
<h1>RDF License dataset</h1>
<p>This site provides an RDF representation of different licenses for data, software or general works. Licenses served under http://purl.oclc.org/NET/rdflicense/ are understood by humans and machines alike.</p>


<center><img src="./fig2.png" width="300px"></center>

<!--<span style="color:red">UNDER MAINTENANCE (18.11.2014)</span> -->


<p>Complete dataset for download: <big><a href="http://purl.org/NET/rdflicense/">rdflicense.ttl</a></big> </p>
<p>This is the VoID description <a href="http://rdflicense.linkeddata.es/dataset/void.ttl">void.ttl</a> and this is a sample license <a href="http://rdflicense.linkeddata.es/dataset/example.ttl">example.ttl</a>  </p>	





<div class="CSSTableGenerator">
                <table>
				<colgroup>
                                <col width="40%">
				<col width="40%">
				<col width="10%">
				<col width="10%">
                    </colgroup><tbody><tr>
                        <td>
                            License
                        </td>
                        <td>
                            URI
                        </td>
                        <td>
                            Version
                        </td>
                        <td>
                            Links
                        </td>
                    </tr>
<tr><td>Open Government Licence</td><td>http://purl.org/NET/rdflicense/ukogl1.0</td><td>1.0</td><td><a href="http://purl.org/NET/rdflicense/ukogl1.0"><img src="./rdflicense32.png"></a><a href="http://purl.org/NET/rdflicense/ukogl1.0.ttl"><img src="./rdf32.png"></a></td></tr>

            <%= TestRDFLicense.getTable() %>
            
                </tbody></table>
            </div>
<p></p>			
<center>
<a href="https://github.com/oeg-upm/rdflicense"><img src="./github.png"></a>
<a href="http://datahub.io/dataset/rdflicense"><img src="./ckan.png"></a>
</center>


<!-- <p>The list of referenceable licenses is also provided <a href="licenses.html">here</a></p> -->
<p class="vbottom"></p>

<p>The RDF version of the licenses in no manner substitutes the traditional English texts, but complements them by favouring the search, the automatic processing and the interoperation of licensed contents.</p>

<p><a rel="license" href="http://creativecommons.org/licenses/by/4.0/"><img alt="Licencia de Creative Commons" style="border-width:0" src="./80x15.png"></a>This dataset is published under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Attribution 4.0 International license</a>.
You may also want to cite our research work:</p><p>
</p><blockquote><a href="http://delicias.dia.fi.upm.es/~vrodriguez/pdf/2014.11.jurix.dataset.pdf">A Dataset of RDF Licenses</a>, V. Rodriguez-Doncel, S. Villata, A. Gomez-Perez, in Proc. of the 27th Int. Conf. on Legal Knowledge and Information System (JURIX), R. Hoekstra (Ed.), ISBN 978-1-61499-467-1, pp. 187-189, IOS Press, 2014 </blockquote>




<!-- <p>Other related content you may like:</p> -->
<!-- These RDF versions do not substitute the legal texts, and they have been created to the best knowledge of the editors of this page. -->

<div id="footerxxxx">
<footer>
<hr>
<center>
<p style="font-size:12px">
<strong>About us</strong> These licenses have been edited by the <a href="http://www.oeg-upm.net/">Ontology Engineering Group</a>, at Universidad Politecnica de Madrid and the <a href="http://www-sop.inria.fr/members/Serena.Villata/"> WIMMICS</a> at INRIA<br>
<strong>Disclaimer</strong> Use the information in this page at your own risk. We do not assume any responsibility on the use you make of the licenses. <br>
<strong>Credits</strong> Icons have been taken from <a href="http://www.danilodemarco.com/">Danilo Demarco</a><br>
</p></center>
</footer>
</div>
</div>
</div>
</div>
<script type="text/javascript" id="cookiebanner" src="./cookiebanner.min.js" data-message="We use cookies to enhance your experience. By continuing to visit this site you agree to our use of cookies."></script>

<!-- Start of StatCounter Code for Default Guide -->
<script type="text/javascript">
var sc_project=10323696; 
var sc_invisible=1; 
var sc_security="78f26544"; 
var scJsHost = (("https:" == document.location.protocol) ?
"https://secure." : "http://www.");
document.write("<sc"+"ript type='text/javascript' src='" +
scJsHost+
"statcounter.com/counter/counter.js'></"+"script>");
</script><script type="text/javascript" src="./counter.js"></script>
<noscript>&lt;div class="statcounter"&gt;&lt;a title="web statistics"
href="http://statcounter.com/" target="_blank"&gt;&lt;img
class="statcounter"
src="http://c.statcounter.com/10323696/0/78f26544/1/"
alt="web statistics"&gt;&lt;/a&gt;&lt;/div&gt;</noscript>

  </body>
</html>
