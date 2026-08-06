// package egovframework.smartbusmng;

// import org.mybatis.spring.annotation.MapperScan;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// @MapperScan(basePackages = {
// 	"egovframework.smartbusmng.mapper"
// })
// public class EgovBootApplication {
	
// 	public static void main(String[] args) {
// 		SpringApplication.run(EgovBootApplication.class, args);
// 	}

// }

package egovframework.smartbusmng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * ✅ EgovBootApplication
 * 
 * - Spring Boot 메인 실행 클래스
 * - MapperScan으로 MyBatis 매퍼 경로 등록
 * - RestTemplate Bean 등록 (외부 API 호출용)
 */
@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {
    "egovframework.smartbusmng.mapper"
})
public class EgovBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgovBootApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
