git pull
ant -Dj2ee.server.home=. dist
sudo cp dist/ldc.war /opt/apache-tomcat-8.0.12/webapps/ldc.war