package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("SELECT h " +
            "FROM EndpointHit h " +
            "WHERE (:uris IS NULL OR h.uri IN :uris) " +
            "AND h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.id, h.uri, h.ip, h.timestamp, h.app " +
            "HAVING :unique = false OR COUNT(h.ip) = 1")
    List<EndpointHit> findHitsCountByUrisAndTimestampBetween(List<String> uris,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             Boolean unique);


}
