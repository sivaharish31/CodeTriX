package com.codetrix.event.websocket;

import com.codetrix.event.dto.TimerBroadcast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventWebSocketService {

    private static final String TIMER_TOPIC = "/topic/timer";
    private static final String ROUND_TOPIC = "/topic/round";
    private static final String EVENT_TOPIC = "/topic/event";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastTimerTick(TimerBroadcast tick) {
        messagingTemplate.convertAndSend(TIMER_TOPIC, tick);
    }

    public void broadcastRoundChange(TimerBroadcast roundChange) {
        log.info("Broadcasting round change: Round {} - {}",
                roundChange.getCurrentRoundNumber(),
                roundChange.getCurrentRoundType());
        messagingTemplate.convertAndSend(ROUND_TOPIC, roundChange);
        messagingTemplate.convertAndSend(TIMER_TOPIC, roundChange);
    }

    public void broadcastEventStart(TimerBroadcast eventStart) {
        log.info("Broadcasting event start");
        messagingTemplate.convertAndSend(EVENT_TOPIC, eventStart);
        messagingTemplate.convertAndSend(TIMER_TOPIC, eventStart);
    }

    public void broadcastEventEnd(TimerBroadcast eventEnd) {
        log.info("Broadcasting event end");
        messagingTemplate.convertAndSend(EVENT_TOPIC, eventEnd);
        messagingTemplate.convertAndSend(TIMER_TOPIC, eventEnd);
    }
}
