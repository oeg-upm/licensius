# ODRL licenses generator

Deployed on http://odrleditor.appspot.com/

## Usage

```
java -jar ./target/odrl_generator-1.0-SNAPSHOT.jar
```

or	create project with Maven and run upm.Application.main()

Server deployed on http://localhost:8080/ by default.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

Steps for installation:
	Install gcloud and then
	```
	gcloud config set project odrleditor
	mvn clean install
	mvn appengine:deploy
```


## Authorship
This application has been created in Jan 2018 in the context of an end-of-grade project for the ETSIInf (Escuela Técnica Superior de Ingenieros Informáticos) by Guillermo Gutierrez Lorenzo. You can check out the original code on bitbucket. (https://bitbucket.org/Guillermo_Gutierrez/odrl_generator_web).

Further changes made by Victor Rodriguez Doncel.

