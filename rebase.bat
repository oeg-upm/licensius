@echo off
echo Rebase 1.0
echo Este script hace un rebase de los datos y metadatos para servirlos en una máquina u en otra.
echo Sintaxis:
echo rebase servidor oldbase newbase
echo ----------------------
echo Primer parametro: nombre del dataset, como una palabra sin barras ni signos de puntuacion
echo Segundo parametro: Antigua base que se quiere sustituir. Ejemplo: http://localhost.com
echo Tercer parametro: Nueva base. Ejemplo: http://salonica.dia.fi.upm.es
echo
echo Reemplazando la cadena "%2" con la cadena "%3" para el dataset "%1"

fart --c-style datasets\%1\data.nq "%2" "%3"
fart --c-style datasets\%1\void.ttl "%2" "%3"
fart --c-style licenses\onecent.ttl "%2" "%3"
fart --c-style licenses\pago10euros.ttl "%2" "%3"
fart --c-style licenses\pago15euros.ttl "%2" "%3"
fart --c-style ldr.config "%2" "%3"
