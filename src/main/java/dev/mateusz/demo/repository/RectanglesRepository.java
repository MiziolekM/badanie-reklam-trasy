package dev.mateusz.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.mateusz.demo.entity.Rectangles;

@Repository
public interface RectanglesRepository extends JpaRepository<Rectangles, Integer> {

}
