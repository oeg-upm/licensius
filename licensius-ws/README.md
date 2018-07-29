# licensius-ws
Licensius. 
This is a web application showing:
* An entry webpage with links to external resources for licensing in RDF
* The REST API services of licensius. It is a collection of license-related HTTP Rest API services

# Technical details for developers

* The code of this project is only hosted at: https://github.com/oeg-upm/licensius
* The web where the system is accessible  is: http://licensius.com
* The licensius website is a website deployed in: https://licensius.appspot.com/
* The services can be tested here: https://licensius.appspot.com/apidoc/index.html

## Deployment
1. Download and use the gcloud Google client, a java SDK, Maven and git.
2. Download and compile this project. 
   ```
   git pull https://github.com/oeg-upm/licensius`
   cd licensius
   cd licensius-ws
   mvn clean install
   ``` 
3. Choose this project, if you have several. 
```
gcloud config set project licensius
```

4. Upload your project
```
mvn appengine:update  
```

5. Check logs, errors, performance... here
https://console.cloud.google.com

