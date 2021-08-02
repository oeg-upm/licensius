set masterfolder=%cd%
cd C:\Users\vroddon\Desktop\xml_records\
for /r %%i in (*) do curl -X POST -H "Content-Type: text/xml; charset=utf-8" -d @%%i https://rdflicense.linkeddata.es/xml2rdf > %%i.ttl

cd /d %masterfolder%
echo %cd%