# RDFLicense

Web application offering different services related to licensing

## Funcionality

* Lists licenses that have been mapped into one or more RDF or other structured formats. 
* Serves URIs in the http://purl.org/NET/rdflicense/ domain, redirecting them to the W3C ODRL CG [repo](https://github.com/w3c/odrl/tree/master/bp/license)
Thus, [http://purl.org/NET/rdflicense/APACHE2.0](http://purl.org/NET/rdflicense/APACHE2.0.ttl) is served from [the raw github](https://raw.githubusercontent.com/w3c/odrl/master/bp/license/rdflicense/APACHE2.0.ttl) (.ttl suffix added if needed). In that repo, an [index file[(https://github.com/w3c/odrl/blob/master/bp/license/index.json)] is maintained. Feel free to contribute!
* The list of licenses if refreshed every 24h

## Internal technical details

This application lives in the pretallod VM, folder /licensius/rdflicense.  
It is governed by the service pddm.
It runs by default in the port 8178.
```
cd /licensius/rdflicense
sudo git pull
sudo mvn clean install
sudo service pddm restart
```

