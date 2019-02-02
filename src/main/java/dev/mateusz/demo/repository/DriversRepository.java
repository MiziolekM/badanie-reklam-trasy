package dev.mateusz.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.mateusz.demo.entity.Drivers;

@Repository
public interface DriversRepository extends JpaRepository<Drivers, Integer> {
	
	@Query(value = "SELECT name FROM drivers", nativeQuery = true)
	List<String> findDriversName();
	
	@Query(value = "SELECT idDriver FROM Drivers where name= :driverName")
	int findIdByDriversName(@Param("driverName") String driverName);
	
	@Query(value = "SELECT d FROM Drivers d where d.name= :driverName")
	Drivers findByDriverName(@Param("driverName") String driverName);
	

}
