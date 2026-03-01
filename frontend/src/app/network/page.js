"use client";

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { fetchWithAuth, getToken } from '@/lib/api';

export default function Network() {
    const router = useRouter();
    const [connections, setConnections] = useState([]);
    const [pendingRequests, setPendingRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [demoUserId, setDemoUserId] = useState('');

    useEffect(() => {
        if (!getToken()) {
            router.push('/login');
            return;
        }
        loadNetworkData();
    }, [router]);

    const loadNetworkData = async () => {
        try {
            setLoading(true);
            const [connData, pendingData] = await Promise.all([
                fetchWithAuth('/connections/core'),
                fetchWithAuth('/connections/core/pending')
            ]);
            setConnections(connData);
            setPendingRequests(pendingData);
        } catch (err) {
            setError('Failed to load network data.');
        } finally {
            setLoading(false);
        }
    };

    const handleAction = async (id, action) => {
        try {
            await fetchWithAuth(`/connections/core/${id}/${action}`, { method: 'PUT' });
            loadNetworkData();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleSendRequest = async (e) => {
        e.preventDefault();
        if (!demoUserId) return;
        try {
            await fetchWithAuth(`/connections/core/request/${demoUserId}`, { method: 'POST' });
            setDemoUserId('');
            alert('Request sent!');
        } catch (err) {
            setError(err.message);
        }
    };

    if (loading) return <div className="container"><h2 className="blinking-cursor">LOADING NETWORK...</h2></div>;

    return (
        <div className="container">
            {error && <div className="error-msg">{error}</div>}

            <h1>NETWORK PROTOCOLS</h1>

            <div className="panel" style={{ backgroundColor: 'var(--accent-teal)', color: 'white' }}>
                <h3>[ INITIATE CONNECTION ]</h3>
                <form onSubmit={handleSendRequest} style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                    <input
                        type="number"
                        className="input-field"
                        placeholder="ENTER TARGET USER ID"
                        value={demoUserId}
                        onChange={(e) => setDemoUserId(e.target.value)}
                        style={{ flex: 1 }}
                    />
                    <button type="submit" className="btn btn-primary" style={{ backgroundColor: 'var(--accent-yellow)', color: 'var(--text-main)' }}>
                        TRANSMIT
                    </button>
                </form>
            </div>

            <div style={{ display: 'flex', gap: '2rem' }}>
                <div style={{ flex: 1 }}>
                    <h3 style={{ borderBottom: '2px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                        PENDING REQUESTS ({pendingRequests.length})
                    </h3>
                    {pendingRequests.length === 0 ? (
                        <p style={{ marginTop: '1rem', fontStyle: 'italic' }}>NO INCOMING SIGNALS.</p>
                    ) : (
                        pendingRequests.map(req => (
                            <div key={req.id} className="panel" style={{ padding: '1rem', marginTop: '1rem' }}>
                                <p style={{ marginBottom: '1rem' }}>
                                    <strong>USER ID {req.requesterId}</strong> requested connection.
                                </p>
                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                    <button className="btn btn-primary" onClick={() => handleAction(req.id, 'accept')}>[ ACCEPT ]</button>
                                    <button className="btn" onClick={() => handleAction(req.id, 'reject')}>[ REJECT ]</button>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                <div style={{ flex: 1 }}>
                    <h3 style={{ borderBottom: '2px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                        ESTABLISHED NODES ({connections.length})
                    </h3>
                    {connections.length === 0 ? (
                        <p style={{ marginTop: '1rem', fontStyle: 'italic' }}>NETWORK IS EMPTY.</p>
                    ) : (
                        connections.map(conn => (
                            <div key={conn.id} className="panel" style={{ padding: '1rem', marginTop: '1rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                <div className="avatar" style={{ backgroundColor: 'var(--accent-orange)' }}>N</div>
                                <div>
                                    <strong>NODE ID: {conn.requesterId} <br /> NODE ID: {conn.receiverId}</strong>
                                    <div className="meta-text">Secured: {new Date(conn.updatedAt).toLocaleDateString()}</div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}
