import { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  // При первой загрузке проверяем, есть ли сохраненный пользователь в браузере
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('critiquehub_user');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  // Функция входа: сохраняем и в стейт, и в память браузера
  const login = (userData) => {
    localStorage.setItem('critiquehub_user', JSON.stringify(userData));
    setUser(userData);
  };

  // Функция выхода: чистим и стейт, и память браузера
  const logout = () => {
    localStorage.removeItem('critiquehub_user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
