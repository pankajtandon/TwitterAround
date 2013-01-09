package com.nayidisha.utils;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;



public class NDUtils {
	public static Logger log = Logger.getLogger(NDUtils.class);
	public static String getCurrentUserId(){
		String s = "";
		if (SecurityContextHolder.getContext().getAuthentication() != null){
			Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (o instanceof UserDetails){
				s = ((UserDetails)o).getUsername();
			} else if (o instanceof String){
				s = (String)o;
			}
			//log.debug("User from security context is " + s);
		}
		return s;
	}
	
	public static void setUpSecurityContext(String u, String p, String role){
		GrantedAuthority authoritiesArray[] = new GrantedAuthority[]{new GrantedAuthorityImpl(role)};
		Authentication authentication = new UsernamePasswordAuthenticationToken(u, p, authoritiesArray);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	public static boolean isZeroLengthOrNullCollection(Collection c) {
		boolean boo = false;
		if (c == null ){
			boo = true;
		} else if (c.size() == 0){
			boo = true;
		}
		return boo;
	}
}
