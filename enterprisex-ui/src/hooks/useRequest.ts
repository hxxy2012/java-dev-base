import { useState, useCallback } from 'react';

export interface UseRequestOptions<T, P extends any[]> {
  onSuccess?: (data: T) => void;
  onError?: (error: Error) => void;
  manual?: boolean;
  defaultParams?: P;
}

export interface UseRequestResult<T, P extends any[]> {
  data: T | null;
  loading: boolean;
  error: Error | null;
  run: (...params: P) => Promise<T | null>;
  runAsync: (...params: P) => Promise<T>;
  refresh: () => Promise<T | null>;
  mutate: (data: T | null) => void;
}

/**
 * 请求管理Hook
 */
export const useRequest = <T = any, P extends any[] = any[]>(
  service: (...args: P) => Promise<T>,
  options?: UseRequestOptions<T, P>
): UseRequestResult<T, P> => {
  const { onSuccess, onError, manual = false, defaultParams } = options || {};

  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(!manual);
  const [error, setError] = useState<Error | null>(null);

  const run = useCallback(
    async (...params: P): Promise<T | null> => {
      setLoading(true);
      setError(null);

      try {
        const result = await service(...params);
        setData(result);
        onSuccess?.(result);
        return result;
      } catch (err) {
        const error = err as Error;
        setError(error);
        onError?.(error);
        return null;
      } finally {
        setLoading(false);
      }
    },
    [service, onSuccess, onError]
  );

  const runAsync = useCallback(
    async (...params: P): Promise<T> => {
      setLoading(true);
      setError(null);

      try {
        const result = await service(...params);
        setData(result);
        onSuccess?.(result);
        return result;
      } catch (err) {
        const error = err as Error;
        setError(error);
        onError?.(error);
        throw error;
      } finally {
        setLoading(false);
      }
    },
    [service, onSuccess, onError]
  );

  const refresh = useCallback(() => {
    if (defaultParams) {
      return run(...defaultParams);
    }
    return Promise.resolve(null);
  }, [run, defaultParams]);

  const mutate = useCallback((newData: T | null) => {
    setData(newData);
  }, []);

  return {
    data,
    loading,
    error,
    run,
    runAsync,
    refresh,
    mutate,
  };
};
