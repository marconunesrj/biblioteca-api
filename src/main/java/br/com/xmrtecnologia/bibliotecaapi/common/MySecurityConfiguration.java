package br.com.xmrtecnologia.bibliotecaapi.common;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


// Deve ser Retirado quando for para produção ou ser melhor configurado
@Configuration(proxyBeanMethods = false)
public class MySecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.requestMatcher(EndpointRequest.toAnyEndpoint())
		        .authorizeRequests((requests) -> requests.anyRequest().permitAll());
		        //.authorizeRequests((requests) -> requests.anyRequest().hasRole("ENDPOINT_ADMIN"));
        //http.httpBasic();
        return http.build();
    }

}
