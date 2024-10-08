package ru.practicum.service;

import ru.practicum.hit.HitRequest;
import ru.practicum.hit.HitResponse;

import java.util.List;

public interface StatsService {
   void createHit(HitRequest hitRequest);


   List<HitResponse> findHits(String start, String end, List<String> uris, Boolean unique);
}
