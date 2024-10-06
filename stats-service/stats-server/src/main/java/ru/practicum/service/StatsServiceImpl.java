package ru.practicum.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.hit.HitRequest;
import ru.practicum.hit.HitResponse;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final ObjectMapper mapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void createHit(HitRequest hitRequest) {
        try {
            JsonNode jsonNode = mapper.convertValue(hitRequest, JsonNode.class);
            LocalDateTime timestamp = extractTimestamp(jsonNode);

            if (timestamp != null) {
                ((ObjectNode) jsonNode).put("timestamp", timestamp.toString());
                EndpointHit endpointHit = mapper.treeToValue(jsonNode, EndpointHit.class);
                log.info("Creating endpoint hit for IP: {}", endpointHit.getIp());
                statsRepository.save(endpointHit);
            } else {
                log.error("Invalid timestamp format or missing timestamp field");
            }
        } catch (Exception e) {
            log.error("Error processing hitRequest: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<HitResponse> findHits(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        List<EndpointHit> hits = statsRepository.findHitsCountByUrisAndTimestampBetween(uris, startTime, endTime, unique);
        return aggregateHits(hits, unique);
    }

    private LocalDateTime extractTimestamp(JsonNode jsonNode) {
        JsonNode timestampNode = jsonNode.get("timestamp");
        if (timestampNode != null && timestampNode.isTextual()) {
            return LocalDateTime.parse(timestampNode.textValue(), formatter);
        }
        return null;
    }

    private List<HitResponse> aggregateHits(List<EndpointHit> hits, Boolean unique) {
        Map<String, HitResponse> hitMap = new HashMap<>();

        if (Boolean.TRUE.equals(unique)) {
            aggregateUniqueHits(hits, hitMap);
        } else {
            aggregateAllHits(hits, hitMap);
        }

        return hitMap.values().stream()
                .sorted(Comparator.comparingInt(HitResponse::getHits).reversed())
                .collect(Collectors.toList());
    }



    private void aggregateUniqueHits(List<EndpointHit> hits, Map<String, HitResponse> hitMap) {
        Set<String> uniqueKeys = new HashSet<>();

        for (EndpointHit hit : hits) {
            String uniqueKey = hit.getUri() + "_" + hit.getIp();
            if (uniqueKeys.add(uniqueKey)) {
                hitMap.computeIfAbsent(hit.getUri(), uri -> new HitResponse(hit.getApp(), uri, 0))
                        .setHits(hitMap.get(hit.getUri()).getHits() + 1);
            }
        }
    }

    private void aggregateAllHits(List<EndpointHit> hits, Map<String, HitResponse> hitMap) {
        for (EndpointHit hit : hits) {
            hitMap.computeIfAbsent(hit.getUri(), uri -> new HitResponse(hit.getApp(), uri, 0))
                    .setHits(hitMap.get(hit.getUri()).getHits() + 1);
        }
    }
}
