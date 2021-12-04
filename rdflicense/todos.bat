set masterfolder=%cd%
cd C:\Users\vroddon\Desktop\xml_records\
%for /r %%i in (*.xml) do curl -X POST -H "Content-Type: text/xml; charset=utf-8" -d @%%i https://rdflicense.linkeddata.es/xml2rdf > %%i.ttl
for /r %%i in (*.xml) do curl -X POST -H "Content-Type: text/xml; charset=utf-8" -d @%%i https://rdflicense.linkeddata.es/xml2odrl > %%i.odrl

cd /d %masterfolder%
echo %cd%