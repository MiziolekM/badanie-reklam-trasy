package dev.mateusz.demo.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.mateusz.demo.entity.Data;
import dev.mateusz.demo.entity.Drivers;

@Repository
public interface DataRepository extends JpaRepository<Data, Integer> {
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Data d where d.driver = :tempDriver")
	void deleteDriverData(@Param("tempDriver") Drivers tempDriver);

}
