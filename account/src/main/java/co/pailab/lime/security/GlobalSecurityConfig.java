package co.pailab.lime.security;

import co.pailab.lime.helper.storage.S3Handler;
import co.pailab.lime.repository.*;
import co.pailab.lime.service.StorageService;
import co.pailab.lime.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static co.pailab.lime.security.SecurityConstants.*;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GlobalSecurityConfig extends GlobalMethodSecurityConfiguration {
    private UserRepository userRepository;
    private GroupModulePermissionRepository groupModulePermissionRepository;
    private ModuleRepository moduleRepository;

    @Autowired
    public GlobalSecurityConfig(UserRepository userRepository,
                                GroupModulePermissionRepository groupModulePermissionRepository, ModuleRepository moduleRepository) {
        this.userRepository = userRepository;
        this.groupModulePermissionRepository = groupModulePermissionRepository;
        this.moduleRepository = moduleRepository;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler(
                userRepository, groupModulePermissionRepository, moduleRepository);
//        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return expressionHandler;
    }


    @Configuration
    @EnableWebSecurity
    public class WebSecurity extends WebSecurityConfigurerAdapter {
        private UserDetailsServiceImpl userDetailsService;
        private BCryptPasswordEncoder bCryptPasswordEncoder;
        private UserRepository userRepository;
        private AuthenticationTokenRepository authenticationTokenRepository;
        private Environment env;

        @Autowired
        private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

        public WebSecurity(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder,
                           UserRepository userRepository,
                           AuthenticationTokenRepository authenticationTokenRepository, Environment env) {
            this.userDetailsService = userDetailsService;
            this.bCryptPasswordEncoder = bCryptPasswordEncoder;
            this.userRepository = userRepository;
            this.authenticationTokenRepository = authenticationTokenRepository;
            this.env = env;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.cors().and().csrf().disable().exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())

                    .authenticationEntryPoint(restAuthenticationEntryPoint).and().authorizeRequests()
                    .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll().antMatchers(HttpMethod.POST, ACTIVATION_URL)
                    .permitAll().antMatchers(HttpMethod.POST, ACTIVATE_REQUEST_URL).permitAll()
                    .antMatchers(HttpMethod.POST, "/accounts/forget_password").permitAll()
                    .antMatchers(HttpMethod.PUT, VERIFY_FORGET_PASSWORD_TOKEN).permitAll()
                    .antMatchers(HttpMethod.PUT, FORGET_PASSWORD).permitAll()
                    .antMatchers(HttpMethod.POST, LOGIN_WITH_FACEBOOK).permitAll()
                    .antMatchers(HttpMethod.POST, LOGIN_WITH_GOOGLE).permitAll()
                    .antMatchers(HttpMethod.GET, "/facebook**").permitAll()
                    .antMatchers(HttpMethod.GET, "/getFacebookInfo**").permitAll()
                    .antMatchers(HttpMethod.GET, "/createFacebookAuthorization").permitAll()
                    .antMatchers(HttpMethod.GET, "/google**").permitAll()
                    .antMatchers(HttpMethod.GET, "/getGoogleInfo**").permitAll()
                    .antMatchers(HttpMethod.GET, "/createGoogleAuthorization").permitAll()
                    .antMatchers(HttpMethod.GET, "/ws**").permitAll()
                    .antMatchers(HttpMethod.GET, "/ws/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/topic/public").permitAll()
                    .antMatchers(HttpMethod.GET, "/app/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new JWTAuthenticationFilter(authenticationManager(), userRepository, authenticationTokenRepository, env))
                    .addFilter(new JWTAuthorizationFilter(authenticationManager(), authenticationTokenRepository))

                    // this disables session creation on Spring Security
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().requiresChannel().anyRequest().requiresSecure();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        }

//	    @Override
//	    protected MethodSecurityExpressionHandler createExpressionHandler() {
//	        CustomMethodSecurityExpressionHandler expressionHandler = 
//	          new CustomMethodSecurityExpressionHandler();
//	        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
//	        return expressionHandler;
//	    }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
            corsConfiguration.addAllowedMethod("*");
            source.registerCorsConfiguration("/**", corsConfiguration);
            return source;
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityConstants SecurityConstants() {
            return new SecurityConstants();
        }

        @Bean
        public CommandLineRunner init(StorageService storageService) {
            return (args) -> {
                storageService.deleteAll();
                storageService.init();
            };
        }

        @Bean
        public S3Handler s3Handler() {
            return new S3Handler();
        }

        @Bean
        public Facebook facebook() {
            return new FacebookTemplate(null);
        }

        @Bean
        public ConnectionRepository connectionRepository() {
            return new MyConnectionRepository();
        }


    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurerAdapter() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")
//                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
//            }
//        };
//    }
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        final CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOrigin("*");
////            configuration.addAllowedHeader("*");
//        configuration.addAllowedMethod("*");
//        // setAllowCredentials(true) is important, otherwise:
//        // The value of the 'Access-Control-Allow-Origin' header in the response must
//        // not be the wildcard '*' when the request's credentials mode is 'include'.
//        configuration.setAllowCredentials(false);
//        // setAllowedHeaders is important! Without it, OPTIONS preflight request
//        // will fail with 403 Invalid CORS request
//        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type", "deviceToken", "deviceType", "userId"));
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    class MyConnectionRepository implements ConnectionRepository {
        @Override
        public MultiValueMap<String, Connection<?>> findAllConnections() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Connection<?>> findConnections(String providerId) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <A> List<Connection<A>> findConnections(Class<A> apiType) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MultiValueMap<String, Connection<?>> findConnectionsToUsers(
                MultiValueMap<String, String> providerUserIds) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Connection<?> getConnection(ConnectionKey connectionKey) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void addConnection(Connection<?> connection) {
            // TODO Auto-generated method stub

        }

        @Override
        public void updateConnection(Connection<?> connection) {
            // TODO Auto-generated method stub

        }

        @Override
        public void removeConnections(String providerId) {
            // TODO Auto-generated method stub

        }

        @Override
        public void removeConnection(ConnectionKey connectionKey) {
            // TODO Auto-generated method stub

        }
    }
}
