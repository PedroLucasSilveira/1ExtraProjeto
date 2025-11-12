package com.example.monitoring.web;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

@RestController
@RequestMapping("/monitor")
public class MonitoringController {
    private final Counter errorCounter;
    private final Counter kpiCounter;
    private final Counter slaCounter;
    private final Counter purgeCounter;
    private final Counter microserviceCounter;
    private final Counter throughputCounter;
    private final Counter securityCounter;

    private final Deque<Map<String, Object>> events = new ArrayDeque<>(200);

    public MonitoringController(MeterRegistry registry) {
        this.errorCounter = registry.counter("monitor_error_total");
        this.kpiCounter = registry.counter("monitor_kpi_total");
        this.slaCounter = registry.counter("monitor_sla_total");
        this.purgeCounter = registry.counter("monitor_purge_total");
        this.microserviceCounter = registry.counter("monitor_microservice_total");
        this.throughputCounter = registry.counter("monitor_throughput_total");
        this.securityCounter = registry.counter("monitor_security_total");
    }

    @PostMapping("/error")
    public ResponseEntity<Map<String,Object>> error(@RequestBody(required = false) Map<String,Object> body){
        errorCounter.increment();
        return ResponseEntity.ok(record("error", body));
    }
    @PostMapping("/kpi")
    public ResponseEntity<Map<String,Object>> kpi(@RequestBody(required = false) Map<String,Object> body){
        kpiCounter.increment();
        return ResponseEntity.ok(record("kpi", body));
    }
    @PostMapping("/sla")
    public ResponseEntity<Map<String,Object>> sla(@RequestBody(required = false) Map<String,Object> body){
        slaCounter.increment();
        return ResponseEntity.ok(record("sla", body));
    }
    @PostMapping("/purge")
    public ResponseEntity<Map<String,Object>> purge(@RequestBody(required = false) Map<String,Object> body){
        purgeCounter.increment();
        return ResponseEntity.ok(record("purge", body));
    }
    @PostMapping("/microservice")
    public ResponseEntity<Map<String,Object>> microservice(@RequestBody(required = false) Map<String,Object> body){
        microserviceCounter.increment();
        return ResponseEntity.ok(record("microservice", body));
    }
    @PostMapping("/throughput")
    public ResponseEntity<Map<String,Object>> throughput(@RequestBody(required = false) Map<String,Object> body){
        throughputCounter.increment();
        return ResponseEntity.ok(record("throughput", body));
    }
    @PostMapping("/security")
    public ResponseEntity<Map<String,Object>> security(@RequestBody(required = false) Map<String,Object> body){
        securityCounter.increment();
        return ResponseEntity.ok(record("security", body));
    }

    @GetMapping("/events")
    public Deque<Map<String,Object>> list() { return events; }

    private Map<String,Object> record(String type, Map<String,Object> body){
        Map<String,Object> evt = Map.of(
                "type", type,
                "ts", Instant.now().toString(),
                "payload", body == null ? Map.of() : body
        );
        if (events.size() >= 200) events.removeFirst();
        events.addLast(evt);
        return evt;
    }
}
