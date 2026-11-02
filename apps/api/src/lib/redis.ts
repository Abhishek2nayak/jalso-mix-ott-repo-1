// Redis client stub — will be replaced with ioredis when needed
export const redis = {
  async ping(): Promise<string> {
    return 'PONG';
  },
};
