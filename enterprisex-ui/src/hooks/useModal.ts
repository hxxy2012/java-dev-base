import { useState } from 'react';

export interface UseModalResult<T = any> {
  visible: boolean;
  data: T | null;
  open: (data?: T) => void;
  close: () => void;
  toggle: () => void;
  setData: (data: T | null) => void;
}

/**
 * 弹窗管理Hook
 */
export const useModal = <T = any>(): UseModalResult<T> => {
  const [visible, setVisible] = useState(false);
  const [data, setData] = useState<T | null>(null);

  const open = (initialData?: T) => {
    setVisible(true);
    if (initialData !== undefined) {
      setData(initialData);
    }
  };

  const close = () => {
    setVisible(false);
    setData(null);
  };

  const toggle = () => {
    setVisible((prev) => !prev);
  };

  return {
    visible,
    data,
    open,
    close,
    toggle,
    setData,
  };
};
