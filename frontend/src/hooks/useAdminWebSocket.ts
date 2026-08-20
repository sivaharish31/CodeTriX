import { useEffect, useRef, useState, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { EventStatus, RoundInfo } from '../types/admin';
import type { LeaderboardResponse } from '../types/leaderboard';
import type { ViolationResponse } from '../types/proctoring';

interface AdminWebSocketState {
  connected: boolean;
  eventStatus: EventStatus | null;
  leaderboard: LeaderboardResponse | null;
  recentViolations: ViolationResponse[];
  submissionCount: number;
}

interface UseAdminWebSocketOptions {
  onEventUpdate?: (status: EventStatus) => void;
  onLeaderboardUpdate?: (leaderboard: LeaderboardResponse) => void;
  onViolation?: (violation: ViolationResponse) => void;
  onRoundChange?: (round: RoundInfo) => void;
}

export const useAdminWebSocket = (options: UseAdminWebSocketOptions = {}) => {
  const [state, setState] = useState<AdminWebSocketState>({
    connected: false,
    eventStatus: null,
    leaderboard: null,
    recentViolations: [],
    submissionCount: 0,
  });

  const clientRef = useRef<Client | null>(null);

  const connect = useCallback(() => {
    if (clientRef.current?.connected) return;

    const token = localStorage.getItem('token');
    const wsUrl = `${window.location.origin}/ws`;

    const client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      setState(prev => ({ ...prev, connected: true }));
      console.log('Admin WebSocket connected');

      // Subscribe to event timer updates
      client.subscribe('/topic/event/timer', (message) => {
        try {
          const data: EventStatus = JSON.parse(message.body);
          setState(prev => ({ ...prev, eventStatus: data }));
          options.onEventUpdate?.(data);

          if (data.currentRound) {
            options.onRoundChange?.(data.currentRound);
          }
        } catch (e) {
          console.error('Failed to parse event update:', e);
        }
      });

      // Subscribe to leaderboard updates
      client.subscribe('/topic/leaderboard', (message) => {
        try {
          const data: LeaderboardResponse = JSON.parse(message.body);
          setState(prev => ({ ...prev, leaderboard: data }));
          options.onLeaderboardUpdate?.(data);
        } catch (e) {
          console.error('Failed to parse leaderboard:', e);
        }
      });

      // Subscribe to proctoring violations
      client.subscribe('/topic/admin/violations', (message) => {
        try {
          const data: ViolationResponse = JSON.parse(message.body);
          setState(prev => ({
            ...prev,
            recentViolations: [data, ...prev.recentViolations].slice(0, 50),
          }));
          options.onViolation?.(data);
        } catch (e) {
          console.error('Failed to parse violation:', e);
        }
      });
    };

    client.onDisconnect = () => {
      setState(prev => ({ ...prev, connected: false }));
      console.log('Admin WebSocket disconnected');
    };

    client.activate();
    clientRef.current = client;
  }, [options]);

  const disconnect = useCallback(() => {
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      setState(prev => ({ ...prev, connected: false }));
    }
  }, []);

  useEffect(() => {
    connect();
    return () => disconnect();
  }, [connect, disconnect]);

  return {
    ...state,
    connect,
    disconnect,
  };
};

export default useAdminWebSocket;
