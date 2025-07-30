package ovh.migmolrod.food.ordering.system.customer.service.application.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;
import ovh.migmolrod.food.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;

@Slf4j
@RestController
@RequestMapping(value = "/customers", produces = "application/vnd.api.vi+json")
public class CustomerController {

	private final CustomerApplicationService service;

	public CustomerController(CustomerApplicationService service) {this.service = service;}

	@PostMapping
	public ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CreateCustomerCommand command) {
		log.info("Creating customer with username '{}", command.getUsername());
		CreateCustomerResponse response = service.createCustomer(command);

		return ResponseEntity.ok(response);
	}

}
