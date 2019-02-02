package dev.mateusz.demo.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.mateusz.demo.entity.Drivers;
import dev.mateusz.demo.entity.Results;

@Repository
public interface ResultsRepository extends JpaRepository<Results, Integer> {
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Results r where r.driver = :tempDriver")
	void deleteDriverResults(@Param("tempDriver") Drivers tempDriver);

}
