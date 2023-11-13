package spring.security.boot2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.properties.RedisProperties;
import spring.security.boot2.properties.RefreshTokenProperties;
import spring.security.boot2.properties.SecurityCorsProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		AccessTokenProperties.class,
		RefreshTokenProperties.class,
		SecurityCorsProperties.class,
		RedisProperties.class
})
public class Boot2Application {

	public static void main(String[] args) {
		SpringApplication.run(Boot2Application.class, args);
	}

}
