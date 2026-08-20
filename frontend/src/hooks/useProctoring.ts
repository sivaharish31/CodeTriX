import { useEffect, useRef, useCallback, useState } from 'react';
import { proctoringApi } from '../services/proctoringApi';
import type { ViolationType, ViolationRequest } from '../types/proctoring';

interface UseProctoringOptions {
  enabled: boolean;
  roundId?: number;
  onViolation?: (type: ViolationType, details?: string) => void;
  onWarning?: (message: string) => void;
  requireFullscreen?: boolean;
  blockCopyPaste?: boolean;
  blockKeyboardShortcuts?: boolean;
}

interface ProctoringState {
  isActive: boolean;
  isFullscreen: boolean;
  violationCount: number;
  lastViolation?: { type: ViolationType; time: Date };
}

const BLOCKED_SHORTCUTS = [
  { key: 'c', ctrl: true },
  { key: 'v', ctrl: true },
  { key: 'x', ctrl: true },
  { key: 'a', ctrl: true },
  { key: 'f', ctrl: true },
  { key: 'p', ctrl: true },
  { key: 's', ctrl: true },
  { key: 'Tab', alt: true },
  { key: 'F12', ctrl: false },
];

export const useProctoring = (options: UseProctoringOptions) => {
  const {
    enabled,
    roundId,
    onViolation,
    onWarning,
    requireFullscreen = true,
    blockCopyPaste = true,
    blockKeyboardShortcuts = true,
  } = options;

  const [state, setState] = useState<ProctoringState>({
    isActive: false,
    isFullscreen: false,
    violationCount: 0,
  });

  const violationQueueRef = useRef<ViolationRequest[]>([]);
  const flushTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  const queueViolation = useCallback((type: ViolationType, details?: string) => {
    const violation: ViolationRequest = {
      violationType: type,
      roundId,
      clientTimestamp: Date.now(),
      details,
    };

    violationQueueRef.current.push(violation);
    setState(prev => ({
      ...prev,
      violationCount: prev.violationCount + 1,
      lastViolation: { type, time: new Date() },
    }));

    onViolation?.(type, details);

    if (flushTimeoutRef.current) {
      clearTimeout(flushTimeoutRef.current);
    }
    flushTimeoutRef.current = setTimeout(flushViolations, 1000);
  }, [roundId, onViolation]);

  const flushViolations = useCallback(async () => {
    if (violationQueueRef.current.length === 0) return;

    const toSend = [...violationQueueRef.current];
    violationQueueRef.current = [];

    if (toSend.length === 1) {
      await proctoringApi.reportViolation(toSend[0]);
    } else {
      await proctoringApi.reportViolationsBatch(toSend);
    }
  }, []);

  const handleVisibilityChange = useCallback(() => {
    if (document.hidden) {
      queueViolation('VISIBILITY_HIDDEN');
      onWarning?.('Warning: Window focus lost. This has been recorded.');
    }
  }, [queueViolation, onWarning]);

  const handleBlur = useCallback(() => {
    queueViolation('TAB_SWITCH');
    onWarning?.('Warning: Tab switch detected. Please stay on this page.');
  }, [queueViolation, onWarning]);

  const handleFullscreenChange = useCallback(() => {
    const isFullscreen = !!document.fullscreenElement;
    setState(prev => ({ ...prev, isFullscreen }));

    if (!isFullscreen && requireFullscreen) {
      queueViolation('FULLSCREEN_EXIT');
      onWarning?.('Warning: Fullscreen exited. Please return to fullscreen mode.');
    }
  }, [queueViolation, onWarning, requireFullscreen]);

  const handleCopy = useCallback((e: ClipboardEvent) => {
    if (blockCopyPaste) {
      e.preventDefault();
      queueViolation('COPY');
      onWarning?.('Warning: Copy is not allowed during the competition.');
    }
  }, [blockCopyPaste, queueViolation, onWarning]);

  const handlePaste = useCallback((e: ClipboardEvent) => {
    if (blockCopyPaste) {
      e.preventDefault();
      queueViolation('PASTE');
      onWarning?.('Warning: Paste is not allowed during the competition.');
    }
  }, [blockCopyPaste, queueViolation, onWarning]);

  const handleCut = useCallback((e: ClipboardEvent) => {
    if (blockCopyPaste) {
      e.preventDefault();
      queueViolation('CUT');
      onWarning?.('Warning: Cut is not allowed during the competition.');
    }
  }, [blockCopyPaste, queueViolation, onWarning]);

  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    if (!blockKeyboardShortcuts) return;

    const isBlocked = BLOCKED_SHORTCUTS.some(shortcut => {
      const ctrlMatch = shortcut.ctrl ? (e.ctrlKey || e.metaKey) : true;
      const altMatch = shortcut.key === 'Tab' && shortcut.alt ? e.altKey : true;
      return e.key.toLowerCase() === shortcut.key.toLowerCase() && ctrlMatch && altMatch;
    });

    if (isBlocked) {
      e.preventDefault();
      queueViolation('KEYBOARD_SHORTCUT', `Key: ${e.key}, Ctrl: ${e.ctrlKey}, Alt: ${e.altKey}`);
      onWarning?.('Warning: This keyboard shortcut is not allowed.');
    }
  }, [blockKeyboardShortcuts, queueViolation, onWarning]);

  const handleContextMenu = useCallback((e: MouseEvent) => {
    if (blockCopyPaste) {
      e.preventDefault();
    }
  }, [blockCopyPaste]);

  const enterFullscreen = useCallback(async () => {
    try {
      await document.documentElement.requestFullscreen();
      setState(prev => ({ ...prev, isFullscreen: true }));
    } catch (error) {
      console.error('Failed to enter fullscreen:', error);
    }
  }, []);

  const exitFullscreen = useCallback(async () => {
    try {
      if (document.fullscreenElement) {
        await document.exitFullscreen();
      }
    } catch (error) {
      console.error('Failed to exit fullscreen:', error);
    }
  }, []);

  useEffect(() => {
    if (!enabled) {
      setState(prev => ({ ...prev, isActive: false }));
      return;
    }

    setState(prev => ({ ...prev, isActive: true }));

    document.addEventListener('visibilitychange', handleVisibilityChange);
    window.addEventListener('blur', handleBlur);
    document.addEventListener('fullscreenchange', handleFullscreenChange);
    document.addEventListener('copy', handleCopy);
    document.addEventListener('paste', handlePaste);
    document.addEventListener('cut', handleCut);
    document.addEventListener('keydown', handleKeyDown);
    document.addEventListener('contextmenu', handleContextMenu);

    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      window.removeEventListener('blur', handleBlur);
      document.removeEventListener('fullscreenchange', handleFullscreenChange);
      document.removeEventListener('copy', handleCopy);
      document.removeEventListener('paste', handlePaste);
      document.removeEventListener('cut', handleCut);
      document.removeEventListener('keydown', handleKeyDown);
      document.removeEventListener('contextmenu', handleContextMenu);

      if (flushTimeoutRef.current) {
        clearTimeout(flushTimeoutRef.current);
      }
      flushViolations();
    };
  }, [
    enabled,
    handleVisibilityChange,
    handleBlur,
    handleFullscreenChange,
    handleCopy,
    handlePaste,
    handleCut,
    handleKeyDown,
    handleContextMenu,
    flushViolations,
  ]);

  return {
    ...state,
    enterFullscreen,
    exitFullscreen,
  };
};

export default useProctoring;
