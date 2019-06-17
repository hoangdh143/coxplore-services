//package co.pailab.lime.security.database;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;
//
//@Configuration
//@EnableMongoRepositories(basePackages = "co.pailab.lime.mongodb.repository")
//public class MongoDbConfig extends AbstractMongoConfiguration {
//
//	@Value("${spring.data.mongodb.database}")
//	private String dbName;
//
//	@Value("${spring.data.mongodb.uri}")
//	private String mongoURI;
//
//	@Override
//	protected String getDatabaseName() {
//		return dbName;
//	}
//
//	@Bean
//	public MongoDbFactory mongoDbFactory() {
//		try {
//			return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Bean
//	public MongoTemplate mongoTemplate() throws Exception {
//
//		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
//
//		return mongoTemplate;
//
//	}
//
//	@Override
//	@Bean
//	public MongoClient mongoClient() {
//		MongoClientURI uri = new MongoClientURI(mongoURI);
//
//		return new MongoClient(uri);
//	}
//
//
//}

//package co.pailab.lime.security.database;
//
//import java.util.Collections;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//import com.mongodb.MongoClient;
//import com.mongodb.MongoCredential;
//import com.mongodb.ServerAddress;
//
//@Configuration
//@EnableMongoRepositories(basePackages = "co.pailab.lime.mongodb.repository")
//public class MongoDbConfig {
//	@Autowired
//	private Environment env;
//
//	@Bean
//	public MongoDbFactory customMongoDbFactory() throws Exception {
//
//		return new SimpleMongoDbFactory(new MongoClient(env.getProperty("spring.data.mongodb.uri")), env.getProperty("spring.data.mongodb.database"));
//		
//	}
//	
//	@Bean(name="mongoTemplate")
//  public MongoTemplate mongoTemplate() throws Exception {
//			
//		return new MongoTemplate(
//	            new SimpleMongoDbFactory(
//	                    new MongoClient(
//	                            new ServerAddress("ec2-13-229-136-59.ap-southeast-1.compute.amazonaws.com", 27017),
//	                            Collections.singletonList(
//	                                    MongoCredential.createCredential(
//	                                            "pailabdev",
//	                                            "admin",
//	                                            "pailab_lime_2018".toCharArray()
//	                                    )
//	                            )
//	                    ),
//	                    "vitae"
//	            )
//	    );
//  }
//}
