import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const data = await login(form.email, form.password);
      navigate(data.role === "ADMIN" ? "/admin" : "/customer");
    } catch {
      setError("Invalid email or password");
    }
  };

  return (
    <div className="page">
      <form className="card stack" onSubmit={submit}>
        <h1>Login</h1>
        <input
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          required
        />
        {error ? <p className="error">{error}</p> : null}
        <button type="submit">Login</button>
        <p>
          Customer signup: <Link to="/register">Register</Link>
        </p>
        <p className="hint">Owner login: owner@hotel.com / Owner@123</p>
      </form>
    </div>
  );
}