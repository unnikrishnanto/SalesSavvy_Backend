package com.SalesSavvy.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.SalesSavvy.entities.Role;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.UserRepository;
import com.SalesSavvy.services.TokenService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/api/*", "/admin/*"})
@Component
@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true") // For CORS
public class AuthenticationFilter implements Filter{
	
	private UserRepository userRepo;
	private TokenService tokenService;
	
	// defining public paths that are allowed 
	private String[] UNAUTHENTICATED_PATHS = {
		"/api/login",
		"/api/user/register"
	};
	
	private String ALLOWED_ORIGIN= "http://localhost:5173";
	
	public AuthenticationFilter(UserRepository userRepo, TokenService tokenService) {
		super();
		System.out.println("Filter Started");
		this.userRepo = userRepo;
		this.tokenService = tokenService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			System.out.println("Request Received");
			doFilterLogic(request, response, chain);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void doFilterLogic(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String uri = httpRequest.getRequestURI();
		
		// Allow requests to Unauthenticated paths
		if(Arrays.asList(UNAUTHENTICATED_PATHS).contains(uri)) {
			chain.doFilter(request, response);
			return;
		}
		
		//Handling Preflight (OPTIONS) requests
		if(httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
			setCORSHeaders(httpResponse);
			return;
		}
		
		// ExtractToken from Cookie
		String token = extractTokenFromCookie(httpRequest);
		// validating token
		if(token ==null || !tokenService.validateToken(token)) {
			setErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: invalid or missing token");
			return;
		}
		
		// validating the user present in the token(subject)
		String userInToken = tokenService.extractUsername(token);
		
		Optional<User> userOpt = userRepo.findByUsername(userInToken);
		if(userOpt.isEmpty()) {
			setErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid User");
			return;
		}
		
		
		// Get the User and Role after authentication
		User user = userOpt.get();
		Role role = user.getRole();
		
		// Checking for ADMIN if request is to admin's endpoint's
		if(uri.startsWith("/admin") && role != Role.ADMIN) {
			setErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Forbidden: ADMIN acess required.");
			return;
		}
		
		// checking for other roles
		if(uri.startsWith("/api") && !(role ==  Role.ADMIN || role ==  Role.CUSTOMER)) {
			setErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Forbidden: CUSTOMER access only");
			return;
		}
		
		// Attaching username to request as an attribute
		httpRequest.setAttribute("username", user.getUsername());
		chain.doFilter(httpRequest, response);
			
	}
	
    // sets the CORS headers
	private void setCORSHeaders(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
		response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setStatus(HttpServletResponse.SC_OK);
		return;
	}

	private void setErrorResponse(HttpServletResponse response, int status, String message ) {
		// The response must contain CORS headers to prevent rejection from browser
				setCORSHeaders(response);
		response.setStatus(status);
		try {
			response.getWriter().write(message);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private String extractTokenFromCookie(HttpServletRequest httpRequest) {
		Cookie cookies[] = httpRequest.getCookies();
		
		if(cookies!=null) {
			return Arrays.stream(cookies)
					.filter((cookie) -> cookie.getName().equals("authtoken"))
					.map(Cookie::getValue)
					.findFirst()
					.orElse(null);
		}
		return null;
	}
	
	

}
