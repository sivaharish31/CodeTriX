import { useEffect, useRef, useState, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { LeaderboardResponse } from '../types/leaderboard';

interface UseLeaderboardWebSocketOptions {
  onUpdate?: (leaderboard: LeaderboardResponse) => void;
  autoConnect?: boolean;
}

export const useLeaderboardWebSocket = (options: UseLeaderboardWebSocketOptions = {}) => {
  const { onUpdate, autoConnect = true } = options;
  const [connected, setConnected] = useState(false);
  const [leaderboard, setLeaderboard] = useState<LeaderboardResponse | null>(null);
  const clientRef = useRef<Client | null>(null);

  const connect = useCallback(() => {
    if (clientRef.current?.connected) {
      return;
    }

    const token = localStorage.getItem('token');
    const wsUrl = `${window.location.origin}/ws/leaderboard`;

    const client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      debug: (str) => {
        if (process.env.NODE_ENV === 'development') {
          console.log('STOMP:', str);
        }
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      setConnected(true);
      console.log('Leaderboard WebSocket connected');

      client.subscribe('/topic/leaderboard', (message) => {
        try {
          const data: LeaderboardResponse = JSON.parse(message.body);
          setLeaderboard(data);
          onUpdate?.(data);
        } catch (e) {
          console.error('Failed to parse leaderboard message:', e);
        }
      });
    };

    client.onDisconnect = () => {
      setConnected(false);
      console.log('Leaderboard WebSocket disconnected');
    };

    client.onStompError = (frame) => {
      console.error('STOMP error:', frame.headers['message']);
    };

    client.activate();
    clientRef.current = client;
  }, [onUpdate]);

  const disconnect = useCallback(() => {
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      setConnected(false);
    }
  }, []);

  const requestRefresh = useCallback(() => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination: '/app/leaderboard/refresh',
        body: '',
      });
    }
  }, []);

  useEffect(() => {
    if (autoConnect) {
      connect();
    }

    return () => {
      disconnect();
    };
  }, [autoConnect, connect, disconnect]);

  return {
    connected,
    leaderboard,
    connect,
    disconnect,
    requestRefresh,
  };
};

export default useLeaderboardWebSocket;
