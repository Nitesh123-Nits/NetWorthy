"use client";

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

export default function Signup() {
    const router = useRouter();
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [successMsg, setSuccessMsg] = useState('');
    const [error, setError] = useState('');

    const handleSignup = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await fetch('http://localhost:8080/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ name, email, password }),
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Registration failed.');
            }

            setSuccessMsg('USER PROFILE CREATED. REDIRECTING TO LOGIN...');
            setTimeout(() => {
                router.push('/login');
            }, 2000);

        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="container" style={{ maxWidth: '500px', marginTop: '10vh' }}>
            <div className="panel">
                <h1 style={{ textAlign: 'center', display: 'block' }}>NETWORTHY OS</h1>
                <h3 style={{ textAlign: 'center', marginBottom: '2rem' }}>New User Registration</h3>

                {error && <div className="error-msg">{error}</div>}
                {successMsg && <div style={{ color: 'var(--accent-teal)', fontWeight: 'bold', marginBottom: '1rem', padding: '0.5rem', border: '2px solid var(--accent-teal)' }}>{successMsg}</div>}

                <form onSubmit={handleSignup}>

                    <div className="input-group">
                        <label className="input-label">Full N@me:</label>
                        <input
                            type="text"
                            className="input-field"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                            placeholder="John Doe"
                        />
                    </div>

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
                        [ INITIALIZE PROFILE ]
                    </button>
                </form>

                <div style={{ textAlign: 'center', marginTop: '1.5rem', fontFamily: 'var(--font-mono)' }}>
                    <p>EXISTING USER? <Link href="/login">RETURN TO LOGIN</Link></p>
                </div>
            </div>
        </div>
    );
}
