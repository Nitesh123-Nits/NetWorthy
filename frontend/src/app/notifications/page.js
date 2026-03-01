"use client";

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { fetchWithAuth, getToken } from '@/lib/api';

export default function Notifications() {
    const router = useRouter();
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!getToken()) {
            router.push('/login');
            return;
        }
        loadNotifications();
    }, [router]);

    const loadNotifications = async () => {
        try {
            setLoading(true);
            const data = await fetchWithAuth('/notifications/core');
            setNotifications(data);
        } catch (err) {
            setError('Failed to load alerts.');
        } finally {
            setLoading(false);
        }
    };

    const handleMarkRead = async (id) => {
        try {
            await fetchWithAuth(`/notifications/core/${id}/read`, { method: 'PUT' });
            // Update local state to feel responsive
            setNotifications(notifications.map(n =>
                n.id === id ? { ...n, read: true } : n
            ));
        } catch (err) {
            setError(err.message);
        }
    };

    const handleMarkAllRead = async () => {
        try {
            await fetchWithAuth(`/notifications/core/read-all`, { method: 'PUT' });
            setNotifications(notifications.map(n => ({ ...n, read: true })));
        } catch (err) {
            setError(err.message);
        }
    };

    if (loading) return <div className="container"><h2 style={{ fontFamily: 'var(--font-mono)' }}>SCANNING FOR ALERTS...</h2></div>;

    return (
        <div className="container">
            {error && <div className="error-msg">{error}</div>}

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <h1 style={{ margin: 0 }}>SYSTEM ALERTS</h1>
                <button className="btn btn-secondary" onClick={handleMarkAllRead}>
                    [ ACKNOWLEDGE ALL ]
                </button>
            </div>

            {notifications.length === 0 ? (
                <div className="panel" style={{ textAlign: 'center', fontFamily: 'var(--font-mono)' }}>
                    <p>NO NEW ALERTS DETECTED.</p>
                </div>
            ) : (
                notifications.map((notif) => (
                    <div key={notif.id} className="panel" style={{
                        opacity: notif.read ? 0.6 : 1,
                        borderLeft: notif.read ? 'none' : '5px solid var(--accent-orange)'
                    }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                            <div>
                                <span className="meta-text" style={{ display: 'block', marginBottom: '0.5rem' }}>
                                    [{notif.type}] - {new Date(notif.createdAt).toLocaleString()}
                                </span>
                                <p style={{ fontFamily: 'var(--font-mono)', fontSize: '1.1rem', fontWeight: notif.read ? 'normal' : 'bold' }}>
                                    {notif.message}
                                </p>
                            </div>

                            {!notif.read && (
                                <button
                                    className="btn"
                                    onClick={() => handleMarkRead(notif.id)}
                                    style={{ fontSize: '0.8rem', padding: '0.3rem 0.6rem' }}
                                >
                                    [ ACKNOWLEDGE ]
                                </button>
                            )}
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}
