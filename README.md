# Connecting to MongoDB with SSL/TLS

For non Java clients, you need to install MongoDB from a distribution with SSL included:-

>Refer to: https://www.mongodb.org/downloads?jmp=docs#production

This shows Windows, Linux, Mac OS X or Solaris. Currently you will see the OS X and Solaris packages are not linked with SSL libraries.
For example, the official documentation to build the OS X distribution is here:-

>Refer to: https://docs.mongodb.org/manual/tutorial/install-mongodb-on-os-x/

### Mongodb 3.2.x Driver connection with JDK 7 or 8

For our Java project, we are showing the most basic Gradle build, the latest 3.2 MongoDB Java dependency used with this example will be downloaded from jcenter:-

``` gradle
plugins {
  id "java"
}

repositories {
  jcenter()
}

dependencies {
  compile("org.mongodb:mongo-java-driver:3.2.+")
}
```

The Java SSL/TLS platform requires all certificates to be trusted and be installed into a Key Store

The keytool can be used to import the Public Key certificate provided by the server deployment

```
keytool -importcert -trustcacerts -file server-cert.crt -keystore KeyStore.jks
```

We should not bypass certificate checking like the "-sslAllowInvalidCertificates" flag would allow on the command line client

>Tip: You can create a Key Store using the JDK’s “keytool” utility. Requires you to enter a password at the end

```
keytool -importcert -trustcacerts -file <cert filename>.crt -keystore <output filename>.jks
```

Your application uses System properties to provide the Java platform SSL library with the location and password of the Key Store file:-

```
-Djavax.net.ssl.trustStore=<path to KeyStore file>
-Djavax.net.ssl.trustStorePassword=<password>
```

These are all the official client connection string settings:-

> Refer to: https://docs.mongodb.org/manual/reference/connection-string/

The connection string used with the Java driver:-

``` java

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

String uri = “mongoldb://<hostname>:27017?ssl=true”;
MongoClientURI connectionString = new MongoClientURI(uri);
MongoClient mongoClient = new MongoClient(connectionString);
MongoDatabase database = mongoClient.getDatabase(“foxtrot”);
```

>Tip: If SSL works when using the hostname but connecting with the IP address fails with a validation error, check the hostname matches the certificate’s CN (Common Name) field

To allow connecting with an IP address, as this would fail hostname validation, use the MongoClientOptions.Builder instead:-

``` java

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import static com.mongodb.MongoClientOptions.builder;

String uri = “mongodb://<ip address>:27017/”;
MongoClientURI connectionString;
MongoClientOptions.Builder builder;
builder = MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true);
connectionString = new MongoClientURI(uri, builder);
MongoClient mongoClient = new MongoClient(connectionString);
```
---

#### Connection with user and password

Firstly, make sure you have the password from the server deployment and a user

We use MongoCredential and have to create a ServerAddress list to make the MongoClient API call

``` java

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import static com.mongodb.MongoClientOptions.builder;

char[] pwd = "mypasswrd!".toCharArray();

MongoCredential credential = MongoCredential.createCredential("myadmin", "admin", pwd); // user "myadmin" on admin database

List<MongoCredential> credentials = Collections.singletonList();

List<ServerAddress> hosts = Arrays.asList(
    new ServerAddress("mongodb01.host.dblayer.com:10054"),
    new ServerAddress("mongodb02.host.1.dblayer.com:10071"));

MongoClient mongoClient = new MongoClient(hosts, credentials, options);
MongoDatabase foxtrot = mongoClient.getDatabase("foxtrot");
MongoIterable<String> collectionNames = foxtrot.listCollectionNames(); // works when authenticated
```

> Tip: If things are not authenticating you will see: "not authorized on foxtrot to execute command"

### X509 Client Authentication

Some servers only supply their own CA Public Key certificates, to support this option, a Certificate Authority must be able to issue the certificates for both the client and the server

> Refer to: https://docs.mongodb.org/manual/tutorial/configure-x509-client-authentication/#prerequisites

All certificates should be added into the Trust Key Store

The X509Credential replaces the username credential as the certificate subject is the username:-

``` java
String subject;
MongoCredential credential = MongoCredential.createMongoX509Credential(subject);
```

The complete Java driver authentication configuration is located here:-

> Refer to: https://mongodb.github.io/mongo-java-driver/3.0/driver/reference/connecting/authenticating/