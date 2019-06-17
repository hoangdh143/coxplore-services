//package co.pailab.lime.security.database;
//
//
//import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
//import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.security.KeyStore;
//import java.security.cert.CertificateFactory;
//import java.security.cert.X509Certificate;
//
//import org.springframework.context.annotation.Configuration;
//
//import java.net.URL;
//
//@Configuration
//public class IAMDatabaseAuthentication {
//    //AWS Credentials of the IAM user with policy enabling IAM Database Authenticated access to the db by the db user.
//    private static final DefaultAWSCredentialsProviderChain creds = new DefaultAWSCredentialsProviderChain();
//    private static final String AWS_ACCESS_KEY = creds.getCredentials().getAWSAccessKeyId();
//    private static final String AWS_SECRET_KEY = creds.getCredentials().getAWSSecretKey();
//
//    //Configuration parameters for the generation of the IAM Database Authentication token
//    private static final String RDS_INSTANCE_HOSTNAME = "pailab.cwgfeb4rzg68.ap-southeast-1.rds.amazonaws.com";
//    private static final int RDS_INSTANCE_PORT = 3306;
//    private static final String REGION_NAME = "ap-southeast-1";
//    private static final String DB_USER = "hanh.chu";
//    private static final String DB_MASTER_USER = "pailab_dev";
//    private static final String DB_MASTER_PASSWORD = "pailab.lime.2018";
//    private static final String SSL_CERTIFICATE = "src/main/resources/rds-combined-ca-bundle.pem";
//
//    private static final String KEY_STORE_TYPE = "JKS";
//    private static final String KEY_STORE_PROVIDER = "SUN";
//    private static final String KEY_STORE_FILE_PREFIX = "sys-connect-via-ssl-test-cacerts";
//    private static final String KEY_STORE_FILE_SUFFIX = ".jks";
//    private static final String DEFAULT_KEY_STORE_PASSWORD = "pailab.lime.2018";
//
//    private static final String DATA_SOURCE_URL = "jdbc:mysql://pailab.cwgfeb4rzg68.ap-southeast-1.rds.amazonaws.com:3306/lime?useUnicode=yes&characterEncoding=UTF-8&useSSL=true&verifyServerCertificate=true";
//
//    /**
//     * This method returns a connection to the db instance authenticated using IAM Database Authentication
//     * @return
//     * @throws Exception
//     */
//    public static void getDBConnectionUsingIam() throws Exception {
//        setSslProperties();
//        System.setProperty("spring.devtools.restart.enabled", "false");
//        System.setProperty("spring.datasource.url", DATA_SOURCE_URL);
//        System.setProperty("spring.datasource.username", DB_USER);
//        System.setProperty("spring.datasource.password",generateAuthToken());
//        
//
//    }
//    
//    public static void getDBConnectionUsingMasterUser() throws Exception {
//        setSslProperties();
//        System.setProperty("spring.devtools.restart.enabled", "false");
//        System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
//        System.setProperty("spring.datasource.url", DATA_SOURCE_URL);
//        System.setProperty("spring.datasource.username",DB_MASTER_USER );
//        System.setProperty("spring.datasource.password",DB_MASTER_PASSWORD);
//
//    }
//    
//    
//
//
//
//    /**
//     * This method generates the IAM Auth Token.
//     * An example IAM Auth Token would look like follows:
//     * btusi123.cmz7kenwo2ye.rds.cn-north-1.amazonaws.com.cn:3306/?Action=connect&DBUser=iamtestuser&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20171003T010726Z&X-Amz-SignedHeaders=host&X-Amz-Expires=899&X-Amz-Credential=AKIAPFXHGVDI5RNFO4AQ%2F20171003%2Fcn-north-1%2Frds-db%2Faws4_request&X-Amz-Signature=f9f45ef96c1f770cdad11a53e33ffa4c3730bc03fdee820cfdf1322eed15483b
//     * @return
//     */
//    private static String generateAuthToken() {
//        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
//
//        RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
//                .credentials(new AWSStaticCredentialsProvider(awsCredentials)).region(REGION_NAME).build();
//        
//        return generator.getAuthToken(GetIamAuthTokenRequest.builder()
//                .hostname(RDS_INSTANCE_HOSTNAME).port(RDS_INSTANCE_PORT).userName(DB_USER).build());
//    }
//
//    /**
//     * This method sets the SSL properties which specify the key store file, its type and password:
//     * @throws Exception
//     */
//    private static void setSslProperties() throws Exception {
//        System.setProperty("javax.net.ssl.trustStore", createKeyStoreFile());
//        System.setProperty("javax.net.ssl.trustStoreType", KEY_STORE_TYPE);
//       
//        System.setProperty("javax.net.ssl.trustStorePassword", DEFAULT_KEY_STORE_PASSWORD);
//    }
//
//    /**
//     * This method returns the path of the Key Store File needed for the SSL verification during the IAM Database Authentication to
//     * the db instance.
//     * @return
//     * @throws Exception
//     */
//    private static String createKeyStoreFile() throws Exception {
//        return createKeyStoreFile(createCertificate()).getPath();
//    }
//
//    /**
//     *  This method generates the SSL certificate
//     * @return
//     * @throws Exception
//     */
//    private static X509Certificate createCertificate() throws Exception {
//        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
//        URL url = new File(SSL_CERTIFICATE).toURI().toURL();
//        if (url == null) {
//            throw new Exception();
//        }
//        try (InputStream certInputStream = url.openStream()) {
//            return (X509Certificate) certFactory.generateCertificate(certInputStream);
//        }
//    }
//
//    /**
//     * This method creates the Key Store File
//     * @param rootX509Certificate - the SSL certificate to be stored in the KeyStore
//     * @return
//     * @throws Exception
//     */
//    private static File createKeyStoreFile(X509Certificate rootX509Certificate) throws Exception {
//        File keyStoreFile = File.createTempFile(KEY_STORE_FILE_PREFIX, KEY_STORE_FILE_SUFFIX);
//        try (FileOutputStream fos = new FileOutputStream(keyStoreFile.getPath())) {
//            KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE, KEY_STORE_PROVIDER);
//            ks.load(null);
//            ks.setCertificateEntry("rootCaCertificate", rootX509Certificate);
//            ks.store(fos, DEFAULT_KEY_STORE_PASSWORD.toCharArray());
//        }
//        return keyStoreFile;
//    }
//    
//    
//}
