all: keystore server-cert keytool-server data-dir mongod

data-dir:
	mkdir -p db

keystore:
	keytool -genkey -alias example -keyalg RSA -keystore KeyStore.jks -keysize 2048

server-cert:
	openssl req -new -x509 -days 365 -out server-cert.crt -keyout server-cert.key && cat server-cert.key server-cert.crt > server.pem

mongod:
	mongod --dbpath ./db --sslMode requireSSL --sslAllowInvalidCertificates --sslPEMKeyFile server.pem

mongo:
	mongo --host localhost --ssl --sslAllowInvalidCertificates

keystore:
	keytool -genkey -alias main -keyalg RSA -keystore KeyStore.jks -keysize 2048

keytool-server:
	keytool -importcert -trustcacerts -file server-cert.crt -keystore KeyStore.jks
