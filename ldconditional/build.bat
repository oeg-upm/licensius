git pull
ant -Dj2ee.server.home=. dist
sudo java -jar jetty-runner.jar --port 9090 --path /ldc dist/ldc.war
# sudo /opt/apache-tomcat-8.0.12/bin/shutdown.sh
# sudo cp dist/ldc.war /opt/apache-tomcat-8.0.12/webapps/ldc.war
# sudo /opt/apache-tomcat-8.0.12/bin/startup.sh


