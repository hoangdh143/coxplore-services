package co.pailab.lime.starter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

//import co.pailab.lime.security.database.IAMDatabaseAuthentication;

@SpringBootApplication(scanBasePackages = "co.pailab.lime")
@EntityScan(basePackages = "co.pailab.lime")
//@EnableJpaRepositories("co.pailab.lime")
public class LimeStarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(LimeStarterApplication.class, args);
    }

//    public static void main(String[] args) {
//    	try {
//			IAMDatabaseAuthentication.getDBConnectionUsingMasterUser();
//			SpringApplication.run(LimeStarterApplication.class, args);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            System.out.println(beanNames.length);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
        };
    }
}
