package ovh.migmolrod.food.ordering.system.customer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {
		"ovh.migmolrod.food.ordering.system.customer.service.data",
		"ovh.migmolrod.food.ordering.system.data"
})
@EntityScan(basePackages = {
		"ovh.migmolrod.food.ordering.system.customer.service.data",
		"ovh.migmolrod.food.ordering.system.data"
})
@SpringBootApplication(scanBasePackages = "ovh.migmolrod.food.ordering.system")
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
