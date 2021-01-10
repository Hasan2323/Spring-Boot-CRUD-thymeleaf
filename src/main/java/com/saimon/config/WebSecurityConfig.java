package com.saimon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @Author Muhammad Saimon
 * @since 27/12/2020 21:44
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        UserDetails userOne = User.withUsername("Hasan")
                .password("$2a$10$KW93ot0rNrtJQ6ToaETuy.87Mf0owo2IxDGR.6Y9d4MCEBI1FNc3i")
                .roles("ADMIN")
                .build();

        UserDetails userTwo = User.withUsername("Abid")
                .password("$2a$10$havep73xkJUsEib0dq6J/e9At5kKLHeSqOsalmdshFWPgYVm.sVYi")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userOne, userTwo);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
//                .antMatchers("/", "/home").permitAll()
                .mvcMatchers("/edit/*", "/delete/*").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
//                    .usernameParameter("username") // see login.html page and name="username"
//                    .passwordParameter("password")
                    .permitAll()
//                    .failureUrl("/loginerror")
//                    .defaultSuccessUrl("/new")
                .and()
                .logout().permitAll()
                .logoutSuccessUrl("/logoutsuccess");
    }
}
