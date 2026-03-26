import { createContext, useContext, useMemo, useState } from "react";
import api from "../api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("token"));
  const [role, setRole] = useState(() => localStorage.getItem("role"));
  const [name, setName] = useState(() => localStorage.getItem("name"));
  const [email, setEmail] = useState(() => localStorage.getItem("email"));

  const setSession = (data) => {
    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);
    localStorage.setItem("name", data.name || "");
    localStorage.setItem("email", data.email || "");
    setToken(data.token);
    setRole(data.role);
    setName(data.name || "");
    setEmail(data.email || "");
  };

  const login = async (userEmail, password) => {
    const { data } = await api.post("/auth/login", { email: userEmail, password });
    setSession(data);
    return data;
  };

  const register = async (userName, userEmail, password) => {
    const { data } = await api.post("/auth/register", { name: userName, email: userEmail, password });
    setSession(data);
    return data;
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("name");
    localStorage.removeItem("email");
    setToken(null);
    setRole(null);
    setName(null);
    setEmail(null);
  };

  const value = useMemo(
    () => ({ token, role, name, email, isAuthenticated: !!token, login, register, logout }),
    [token, role, name, email]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return context;
}