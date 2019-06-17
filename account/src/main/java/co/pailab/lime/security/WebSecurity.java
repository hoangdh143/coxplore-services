//package co.pailab.lime.security;
//
//import co.pailab.lime.helper.storage.S3Handler;
//import co.pailab.lime.repository.AuthenticationTokenRepository;
//import co.pailab.lime.repository.UserRepository;
//import co.pailab.lime.service.StorageService;
//import co.pailab.lime.service.UserDetailsServiceImpl;
//import com.google.common.collect.ImmutableList;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.social.connect.Connection;
//import org.springframework.social.connect.ConnectionKey;
//import org.springframework.social.connect.ConnectionRepository;
//import org.springframework.social.facebook.api.Facebook;
//import org.springframework.social.facebook.api.impl.FacebookTemplate;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//import static co.pailab.lime.security.SecurityConstants.*;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurity extends WebSecurityConfigurerAdapter {
//    private UserDetailsServiceImpl userDetailsService;
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//    private UserRepository userRepository;
//    private AuthenticationTokenRepository authenticationTokenRepository;
//    @Autowired
//    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
//
//    public WebSecurity(
//            UserDetailsServiceImpl userDetailsService,
//            BCryptPasswordEncoder bCryptPasswordEncoder,
//            UserRepository userRepository,
//            AuthenticationTokenRepository authenticationTokenRepository) {
//        this.userDetailsService = userDetailsService;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//        this.userRepository = userRepository;
//        this.authenticationTokenRepository = authenticationTokenRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http.cors().and().csrf().disable()
//                .exceptionHandling()
//                .authenticationEntryPoint(restAuthenticationEntryPoint)
//                .and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
//                .antMatchers(HttpMethod.POST, ACTIVATION_URL).permitAll()
//                .antMatchers(HttpMethod.POST, ACTIVATE_REQUEST_URL).permitAll()
//                .antMatchers(HttpMethod.POST, FORGET_PASSWORD).permitAll()
//                .antMatchers(HttpMethod.POST, VERIFY_FORGET_PASSWORD_TOKEN).permitAll()
//                .antMatchers(HttpMethod.PUT, NEW_PASSWORD_WHEN_FORGET).permitAll()
//                .antMatchers(HttpMethod.POST, LOGIN_WITH_FACEBOOK).permitAll()
//                .antMatchers(HttpMethod.POST, LOGIN_WITH_GOOGLE).permitAll()
//                .antMatchers(HttpMethod.GET, "/facebook**").permitAll()
//                .antMatchers(HttpMethod.GET, "/getFacebookInfo**").permitAll()
//                .antMatchers(HttpMethod.GET, "/createFacebookAuthorization").permitAll()
//                .antMatchers(HttpMethod.GET, "/google**").permitAll()
//                .antMatchers(HttpMethod.GET, "/getGoogleInfo**").permitAll()
//                .antMatchers(HttpMethod.GET, "/createGoogleAuthorization").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .addFilter(new JWTAuthenticationFilter(authenticationManager(), userRepository, authenticationTokenRepository))
//                .addFilter(new JWTAuthorizationFilter(authenticationManager(), authenticationTokenRepository))
//
//                // this disables session creation on Spring Security
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//    }
//
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(bCryptPasswordEncoder);
//    }
//    
////    @Override
////    protected MethodSecurityExpressionHandler createExpressionHandler() {
////        CustomMethodSecurityExpressionHandler expressionHandler = 
////          new CustomMethodSecurityExpressionHandler();
////        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
////        return expressionHandler;
////    }
//    
//    
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        final CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(ImmutableList.of("*"));
//        configuration.setAllowedMethods(ImmutableList.of("HEAD",
//                "GET", "POST", "PUT", "DELETE", "PATCH"));
//        // setAllowCredentials(true) is important, otherwise:
//        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
//        configuration.setAllowCredentials(true);
//        // setAllowedHeaders is important! Without it, OPTIONS preflight request
//        // will fail with 403 Invalid CORS request
//        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    //  @Bean
////  CorsConfigurationSource corsConfigurationSource() {
////    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
////    return source;
////  }
//
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public CommandLineRunner init(StorageService storageService) {
//        return (args) -> {
//            storageService.deleteAll();
//            storageService.init();
//        };
//    }
//
//    @Bean
//    public S3Handler s3Handler() {
//        return new S3Handler();
//    }
//
//    @Bean
//    public Facebook facebook() {
//        return new FacebookTemplate(null);
//    }
//
//    @Bean
//    public ConnectionRepository connectionRepository() {
//        return new MyConnectionRepository();
//    }
//}
//
//class MyConnectionRepository implements ConnectionRepository {
//    @Override
//    public MultiValueMap<String, Connection<?>> findAllConnections() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public List<Connection<?>> findConnections(String providerId) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Connection<?> getConnection(ConnectionKey connectionKey) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public void addConnection(Connection<?> connection) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void updateConnection(Connection<?> connection) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void removeConnections(String providerId) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void removeConnection(ConnectionKey connectionKey) {
//        // TODO Auto-generated method stub
//
//    }
//}
