package com.udacity.vehicles.api;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import com.udacity.vehicles.service.ManufacturerService;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping(value = "/cars", produces = "application/json; charset=UTF-8")
class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;
    private final ManufacturerService manufacturerService;
    private final ManufacturerResourceAssembler manufacturerResourceAssembler;

    CarController(CarService carService, CarResourceAssembler assembler, ManufacturerService
            manufacturerService, ManufacturerResourceAssembler manufacturerResourceAssembler) {
        this.carService = carService;
        this.assembler = assembler;
        this.manufacturerService = manufacturerService;
        this.manufacturerResourceAssembler = manufacturerResourceAssembler;
    }

    /**
     * Creates a carList to store any vehicles.
     * @return carList of vehicles
     */
    @GetMapping
    Resources<Resource<Car>> carList() {
        List<Resource<Car>> resources = carService.list().stream().map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(resources);
    }

    @GetMapping("/manufacturers")
    Resources<Resource<Manufacturer>> manufacturerList() {
        List<Resource<Manufacturer>> resources = manufacturerService.list().stream().map
                (manufacturerResourceAssembler::toResource).collect(Collectors.toList());
        return new Resources<>(resources);
    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @GetMapping("/{id}")
    Resource<Car> get(@PathVariable Long id) {
        return assembler.toResource(carService.findById(id));
    }


    /**
     * Posts information to create a new vehicle in the system.
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody Car car) throws URISyntaxException {
        carService.save(car);
        Resource<Car> resource = assembler.toResource(car);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    /**
     * Updates the information of a vehicle in the system.
     * @param id The ID number for which to update vehicle information.
     * @param car The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @PutMapping("/{id}")
    ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Car car) {

        if (id != car.getId()) {
            ResponseEntity.badRequest().body("Path Car Id does not match Car Id in Body");
        }

        carService.save(car);
        Resource<Car> resource = assembler.toResource(new Car());
        return ResponseEntity.ok(resource);
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        carService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}