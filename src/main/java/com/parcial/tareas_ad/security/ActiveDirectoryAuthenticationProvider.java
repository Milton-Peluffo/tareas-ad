package com.parcial.tareas_ad.security;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ActiveDirectoryAuthenticationProvider implements AuthenticationProvider {

    private final String ldapUrl;
    private final String activeDirectoryDomain;

    public ActiveDirectoryAuthenticationProvider(
        @Value("${spring.ldap.urls}") String ldapUrl,
        @Value("${app.ad.domain}") String activeDirectoryDomain
    ) {
        this.ldapUrl = ldapUrl;
        this.activeDirectoryDomain = activeDirectoryDomain;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
        throws org.springframework.security.core.AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials() != null
            ? authentication.getCredentials().toString()
            : "";

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BadCredentialsException("Usuario o contrasena vacios");
        }

        List<String> candidatePrincipals = List.of(
            username,
            username + "@" + activeDirectoryDomain
        );

        for (String principal : candidatePrincipals) {
            if (canBind(principal, password)) {
                return UsernamePasswordAuthenticationToken.authenticated(
                    username,
                    password,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }
        }

        throw new BadCredentialsException("Credenciales de Active Directory invalidas");
    }

    private boolean canBind(String principal, String password) {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, ldapUrl);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, principal);
        environment.put(Context.SECURITY_CREDENTIALS, password);

        DirContext context = null;
        try {
            context = new InitialDirContext(environment);
            return true;
        } catch (javax.naming.AuthenticationException ex) {
            return false;
        } catch (NamingException ex) {
            throw new BadCredentialsException("No fue posible conectar con Active Directory", ex);
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException ignored) {
                    // Nothing else to do here.
                }
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
