package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.participationRequest.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {


    // Найти запросы на участие по ID запрашивающего
    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findByEventId(Long eventId);

    //SELECT * FROM participation_requests WHERE event_id = ? AND id IN (?);
    List<ParticipationRequest> findByEventIdAndIdIn(Long eventId, List<Long> requestIds);

    // Найти запрос на участие по ID события и ID запрашивающего
    ParticipationRequest findByEventIdAndRequesterId(Long eventId, Long requesterId);

    // Проверка на существование запроса по событию и пользователю
    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    // Подсчет количества запросов на участие по событию
    long countByEventId(Long eventId);
}
