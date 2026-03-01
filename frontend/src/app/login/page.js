"use client";

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

export default function Login() {
    const router = useRouter();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Login failed. Check your credentials.');
            }

            // Save token and user info
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('userId', data.user.id);

            // Redirect to home/feed
            router.push('/');
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="container" style={{ maxWidth: '500px', marginTop: '10vh' }}>
            <div className="panel">
                <h1 style={{ textAlign: 'center', display: 'block' }}>NETWORTHY OS</h1>
                <h3 style={{ textAlign: 'center', marginBottom: '2rem' }}>Terminal Login</h3>

                {error && <div className="error-msg">{error}</div>}

                <form onSubmit={handleLogin}>
                    <div className="input-group">
                        <label className="input-label">Em@il Address:</label>
                        <input
                            type="email"
                            className="input-field"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            placeholder="user@example.com"
                        />
                    </div>

                    <div className="input-group">
                        <label className="input-label">P@ssword:</label>
                        <input
                            type="password"
                            className="input-field"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            placeholder="••••••••"
                        />
                    </div>

                    <button type="submit" className="btn btn-primary" style={{ width: '100%', marginBottom: '1rem' }}>
                        [ EXECUTE LOGIN ]
                    </button>
                </form>

                <div style={{ textAlign: 'center', marginTop: '1.5rem', fontFamily: 'var(--font-mono)' }}>
                    <p>NO ACCOUNT? <Link href="/signup">INITIALIZE REGISTRATION</Link></p>
                </div>
            </div>
        </div>
    );
}
