package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.location.Location;


public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("SELECT l FROM Location l WHERE l.lat = :lat AND l.lon = :lon")
    Location findFirstByLatAndLon(@Param("lat") Float lat, @Param("lon") Float lon);
}
