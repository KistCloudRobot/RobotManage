package net.ion;

import net.ion.mdk.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@SpringBootApplication(exclude = {ContextInstanceDataAutoConfiguration.class})
@EnableJpaAuditing
// OAuth 사용을 위해 필요.
@EnableConfigurationProperties(AppProperties.class)
public class CloudRobotApplication {

	public static final TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");

	@PostConstruct
	public void started() {
		TimeZone.setDefault(timeZone);
		System.out.println("서비스 시작: v 2021-12-22 at" + ZonedDateTime.now());
	}

	public static void main(String[] args) {
		SpringApplication.run(CloudRobotApplication.class, args);
	}

}
