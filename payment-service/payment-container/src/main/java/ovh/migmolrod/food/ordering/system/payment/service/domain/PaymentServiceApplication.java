package ovh.migmolrod.food.ordering.system.payment.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "ovh.migmolrod.food.ordering.system.payment.service.data")
@EntityScan(basePackages = "ovh.migmolrod.food.ordering.system.payment.service.data")
@SpringBootApplication(scanBasePackages = "ovh.migmolrod.food.ordering.system")
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
