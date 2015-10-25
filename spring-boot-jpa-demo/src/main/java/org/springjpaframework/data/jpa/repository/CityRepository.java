package org.springjpaframework.data.jpa.repository;

import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springjpaframework.data.jpa.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {
	

}
