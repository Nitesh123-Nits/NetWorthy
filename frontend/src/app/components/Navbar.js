"use client";

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { clearAuthData, getToken } from '@/lib/api';
import { useEffect, useState } from 'react';

export default function Navbar() {
    const router = useRouter();
    const [isClient, setIsClient] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        setIsClient(true);
        setIsAuthenticated(!!getToken());
    }, []);

    const handleLogout = () => {
        clearAuthData();
        router.push('/login');
        setIsAuthenticated(false);
    };

    if (!isClient || !isAuthenticated) {
        return null; // Don't show navbar on login/signup or while checking auth
    }

    return (
        <nav className="navbar">
            <div className="nav-brand">
                <Link href="/">NETWORTHY</Link>
            </div>
            <div className="nav-links">
                <Link href="/" className="nav-link">[ FEED ]</Link>
                <Link href="/network" className="nav-link">[ NETWORK ]</Link>
                <Link href="/notifications" className="nav-link">[ ALERTS ]</Link>
                <Link href="/profile" className="nav-link">[ PROFILE ]</Link>
                <button
                    onClick={handleLogout}
                    style={{ background: 'none', border: 'none', color: 'var(--error-red)', cursor: 'pointer' }}
                    className="nav-link"
                >
                    [ LOGOUT ]
                </button>
            </div>
        </nav>
    );
}
