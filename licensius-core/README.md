# licensius-core
Collection of license-related services

This project is made up of a Java library (licensius-core) (documented at http://licensius.com/apidoc/licensius-core.pdf) which can be invoked from the command line, and a HTTP Rest Web service whose documentation is available at http://licensius.com/apidoc/index.html.

<code><pre>
Name
licensius-core is a jar package wich implements different functionalities to handle licenses in RDF
Synopsis
usage: oeg.licensius.core.Licensius
 -findlicenseinrdf <arg>   finds possible licenses in a RDF document.
                           Input is URI pointing to an online RDF
                           document. Output is json.
 -findlicenseintxt <arg>   finds possible licenses in a TEXT document.
                           Input is URI pointing to an online TXT
                           document. Output is json.
 -getinfolicense <arg>     shows basic information of a license in json.
                           Input is RDFLicense URI. Output is json.
 -help                     shows help (Help)
 -isopen <arg>             determines if a license is 'open' according to
                           the http://www.opendefinition.com. Output is
                           'true' or 'false'
 -legalcode <arg>          shows the legal text of a license in English if
                           available.  Input is RDFLicense URI. Output is
                           json.
 -listlicenses             shows the list of RDFLicenses available at
                           http://rdflicense.linkeddata.es/. Takes no
                           input. Output is json
 -nologs                   disables the logging funcionality
 -version                  shows the version info

Options
-help
	Shows this help
-version
	Shows the version of licensius-core. Example

</pre></code>
See full documentation online.

Parent:
https://github.com/oeg-upm/licensius


