package org.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTest {
	@Test
	public void testGen(){
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", 1);
		claims.put("username", "admin");
		String jwt = JWT.create()
				.withClaim("user", claims)
				.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))
				.sign(Algorithm.HMAC256("secret"));
		System.out.println(jwt);
	}
	@Test
	public void testParse(){
		String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoxLCJ1c2VybmFtZSI6ImFkbWluIn0sImV4cCI6MTc2NTkzNTc5Mn0.ywqXxkvHgglpCDvhljzemEDjwLjgNC-Iifk3feBo9Rk";
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256("secret")).build();
		DecodedJWT decodedJWT = verifier.verify(jwt);
		Map<String, Claim> user = decodedJWT.getClaims();
		System.out.println(user.get("user").asMap());
	}

}
