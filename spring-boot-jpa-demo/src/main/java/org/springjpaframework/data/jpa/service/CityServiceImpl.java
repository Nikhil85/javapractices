package org.springjpaframework.data.jpa.service;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springjpaframework.data.jpa.domain.City;
import org.springjpaframework.data.jpa.repository.CityRepository;

@Service
@Validated
public class CityServiceImpl implements CityService {
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private CityRepository cityRepository;

	@Override
	@Transactional
	public City save(City city) {
		return cityRepository.saveAndFlush(city);
		
	}

}
