import SockJS from 'sockjs-client';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';

export interface WebSocketMessage {
  type: string;
  title: string;
  content: string;
  sendTime?: string;
  sender?: string;
  receiver?: string;
}

class WebSocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, StompSubscription> = new Map();
  private reconnectDelay = 5000;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;

  /**
   * 连接WebSocket
   */
  connect(onConnected?: () => void, onError?: (error: any) => void): void {
    // 如果已经连接，不重复连接
    if (this.client && this.client.connected) {
      console.log('WebSocket already connected');
      return;
    }

    const socket = new SockJS(`${import.meta.env.VITE_API_URL}/ws`);

    this.client = new Client({
      webSocketFactory: () => socket as any,
      reconnectDelay: this.reconnectDelay,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      onConnect: () => {
        console.log('WebSocket Connected');
        this.reconnectAttempts = 0;
        if (onConnected) {
          onConnected();
        }
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame);
        if (onError) {
          onError(frame);
        }
      },
      onWebSocketError: (error) => {
        console.error('WebSocket Error:', error);
        if (onError) {
          onError(error);
        }
      },
      onDisconnect: () => {
        console.log('WebSocket Disconnected');
        this.handleReconnect();
      },
    });

    this.client.activate();
  }

  /**
   * 处理重连
   */
  private handleReconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
      setTimeout(() => {
        this.connect();
      }, this.reconnectDelay);
    } else {
      console.error('Max reconnect attempts reached. Please refresh the page.');
    }
  }

  /**
   * 订阅公共消息
   */
  subscribePublic(callback: (message: WebSocketMessage) => void): string {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return '';
    }

    const subscription = this.client.subscribe('/topic/public', (message: IMessage) => {
      const data = JSON.parse(message.body);
      callback(data);
    });

    const subId = 'public-' + Date.now();
    this.subscriptions.set(subId, subscription);
    return subId;
  }

  /**
   * 订阅系统通知
   */
  subscribeNotification(callback: (message: WebSocketMessage) => void): string {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return '';
    }

    const subscription = this.client.subscribe('/topic/notification', (message: IMessage) => {
      const data = JSON.parse(message.body);
      callback(data);
    });

    const subId = 'notification-' + Date.now();
    this.subscriptions.set(subId, subscription);
    return subId;
  }

  /**
   * 订阅用户私有消息
   */
  subscribeUser(username: string, callback: (message: WebSocketMessage) => void): string {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return '';
    }

    const subscription = this.client.subscribe(`/user/${username}/queue/message`, (message: IMessage) => {
      const data = JSON.parse(message.body);
      callback(data);
    });

    const subId = 'user-' + Date.now();
    this.subscriptions.set(subId, subscription);
    return subId;
  }

  /**
   * 取消订阅
   */
  unsubscribe(subscriptionId: string): void {
    const subscription = this.subscriptions.get(subscriptionId);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(subscriptionId);
    }
  }

  /**
   * 发送消息
   */
  sendMessage(destination: string, message: WebSocketMessage): void {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(message),
    });
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    if (this.client) {
      // 取消所有订阅
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe();
      });
      this.subscriptions.clear();

      // 断开连接
      this.client.deactivate();
      this.client = null;
    }
  }

  /**
   * 检查连接状态
   */
  isConnected(): boolean {
    return this.client !== null && this.client.connected;
  }
}

// 导出单例
export const websocketService = new WebSocketService();
