import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export interface User {
  id: number;
  nome: string;
  email: string;
  roles: string[];
}

interface AuthState {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  login: (token: string, refreshToken: string, user: User) => void;
  logout: () => void;
  isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      refreshToken: null,
      login: (token, refreshToken, user) =>
        set({ token, refreshToken, user }),
      logout: () =>
        set({ token: null, refreshToken: null, user: null }),
      isAuthenticated: () => !!get().token,
    }),
    {
      name: 'ppg-hub-auth',
    }
  )
);
