package dev.mateusz.demo.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.mateusz.demo.entity.Drivers;
import dev.mateusz.demo.entity.Fixations;

@Repository
public interface FixationsRepository extends JpaRepository<Fixations, Integer> {
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Fixations f where f.driver = :tempDriver")
	void deleteDriverFixations(@Param("tempDriver") Drivers tempDriver);

}
