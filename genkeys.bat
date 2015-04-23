REM antes era dsa
keytool -genkey -alias victor -keyalg RSA -dname "CN=Victor, O=OEG, C=ES" -keypass abc1234 -storepass abc12345 -keystore myKeyStore.jks -validity 365
