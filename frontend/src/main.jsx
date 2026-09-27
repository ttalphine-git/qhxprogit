import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  Activity,
  BarChart3,
  CheckCircle2,
  KeyRound,
  LogOut,
  Shield,
  UserRound,
} from 'lucide-react';
import './styles.css';

const API_URL = import.meta.env.VITE_API_URL ?? '/api';
const TOKEN_KEY = 'adminPortalToken';

function App() {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [profile, setProfile] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(Boolean(token));
  const [error, setError] = useState('');

  const authHeaders = useMemo(
    () => (token ? { Authorization: `Bearer ${token}` } : {}),
    [token],
  );

  useEffect(() => {
    if (!token) {
      setProfile(null);
      setDashboard(null);
      setLoading(false);
      return;
    }

    let ignore = false;
    async function loadDashboard() {
      setLoading(true);
      setError('');
      try {
        const [profileResponse, dashboardResponse] = await Promise.all([
          fetch(`${API_URL}/me`, { headers: authHeaders }),
          fetch(`${API_URL}/dashboard`, { headers: authHeaders }),
        ]);

        if (!profileResponse.ok || !dashboardResponse.ok) {
          throw new Error('Session expired. Please sign in again.');
        }

        const [profileData, dashboardData] = await Promise.all([
          profileResponse.json(),
          dashboardResponse.json(),
        ]);

        if (!ignore) {
          setProfile(profileData);
          setDashboard(dashboardData);
        }
      } catch (err) {
        if (!ignore) {
          localStorage.removeItem(TOKEN_KEY);
          setToken(null);
          setError(err.message);
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    }

    loadDashboard();
    return () => {
      ignore = true;
    };
  }, [authHeaders, token]);

  async function handleLogin(credentials) {
    setLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        throw new Error('Invalid username or password.');
      }

      const data = await response.json();
      localStorage.setItem(TOKEN_KEY, data.token);
      setToken(data.token);
      setProfile({ username: data.username, role: data.role });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  function handleLogout() {
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
    setProfile(null);
    setDashboard(null);
    setError('');
  }

  return (
    <main className="shell">
      {!token ? (
        <LoginPage onLogin={handleLogin} loading={loading} error={error} />
      ) : (
        <DashboardPage
          profile={profile}
          dashboard={dashboard}
          loading={loading}
          error={error}
          onLogout={handleLogout}
        />
      )}
    </main>
  );
}

function LoginPage({ onLogin, loading, error }) {
  const [username, setUsername] = useState('superadmin');
  const [password, setPassword] = useState('');

  function submit(event) {
    event.preventDefault();
    onLogin({ username, password });
  }

  return (
    <section className="login-layout">
      <div className="brand-panel">
        <div className="brand-mark">
          <Shield size={34} aria-hidden="true" />
        </div>
        <h1>qhx Superadmin Portal</h1>
        <p>Secure control center for user access, operations, approvals, and platform health.</p>
        <div className="status-strip">
          <span><CheckCircle2 size={17} aria-hidden="true" /> Java 21 API</span>
          <span><CheckCircle2 size={17} aria-hidden="true" /> React 19.3 UI</span>
        </div>
      </div>

      <form className="login-card" onSubmit={submit}>
        <div>
          <p className="eyebrow">Authorized access</p>
          <h2>Sign in</h2>
        </div>

        <label>
          Username
          <span className="input-wrap">
            <UserRound size={18} aria-hidden="true" />
            <input value={username} onChange={(event) => setUsername(event.target.value)} />
          </span>
        </label>

        <label>
          Password
          <span className="input-wrap">
            <KeyRound size={18} aria-hidden="true" />
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </span>
        </label>

        {error && <p className="error">{error}</p>}

        <button className="primary-button" type="submit" disabled={loading}>
          {loading ? 'Signing in...' : 'Sign in'}
        </button>
      </form>
    </section>
  );
}

function DashboardPage({ profile, dashboard, loading, error, onLogout }) {
  if (loading && !dashboard) {
    return <div className="loading">Loading dashboard...</div>;
  }

  return (
    <section className="dashboard-layout">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <Shield size={28} aria-hidden="true" />
          <span>Admin Portal</span>
        </div>
        <nav>
          <a className="active" href="#overview"><BarChart3 size={18} aria-hidden="true" /> Overview</a>
          <a href="#activity"><Activity size={18} aria-hidden="true" /> Activity</a>
        </nav>
      </aside>

      <div className="dashboard-main">
        <header className="topbar">
          <div>
            <p className="eyebrow">Superadmin dashboard</p>
            <h1>{dashboard?.greeting ?? `Welcome back, ${profile?.username ?? 'superadmin'}`}</h1>
          </div>
          <div className="account-pill">
            <div>
              <strong>{profile?.username}</strong>
              <span>{profile?.role}</span>
            </div>
            <button className="icon-button" onClick={onLogout} aria-label="Log out" title="Log out">
              <LogOut size={18} aria-hidden="true" />
            </button>
          </div>
        </header>

        {error && <p className="error">{error}</p>}

        <section id="overview" className="metric-grid">
          {dashboard?.metrics?.map((metric) => (
            <article className="metric-card" key={metric.label}>
              <span>{metric.label}</span>
              <strong>{metric.value}</strong>
              <small>{metric.delta}</small>
            </article>
          ))}
        </section>

        <section id="activity" className="activity-section">
          <div className="section-heading">
            <h2>Recent activity</h2>
            <span>{dashboard?.generatedAt ? new Date(dashboard.generatedAt).toLocaleString() : ''}</span>
          </div>
          <div className="activity-list">
            {dashboard?.activities?.map((item) => (
              <article className="activity-row" key={item.title}>
                <CheckCircle2 size={20} aria-hidden="true" />
                <div>
                  <strong>{item.title}</strong>
                  <span>{item.time}</span>
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </section>
  );
}

createRoot(document.getElementById('root')).render(<App />);
