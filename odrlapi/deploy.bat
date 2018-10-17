REM gcloud projects list
REM gcloud config set project odrlapi
call mvn clean install
call mvn appengine:update
