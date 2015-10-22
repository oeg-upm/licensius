REM Primero hay que instalar npm para windows aquí https://www.npmjs.com/package/npm
REM No hay que descargar nada. Pero podrías ver el código aquí here https://github.com/apidoc/apidoc
REM Follow the instructions here:  http://apidocjs.com/
apidoc -i . apidoc/
ftp -i -s:doc.ftp