"use client";

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { fetchWithAuth, getToken, getUserId } from '@/lib/api';

export default function Profile() {
    const router = useRouter();
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Editable fields
    const [name, setName] = useState('');
    const [headline, setHeadline] = useState('');
    const [about, setAbout] = useState('');
    const [skills, setSkills] = useState('');

    useEffect(() => {
        const token = getToken();
        const userId = getUserId();

        if (!token || !userId) {
            router.push('/login');
            return;
        }

        loadProfile(userId);
    }, [router]);

    const loadProfile = async (userId) => {
        try {
            setLoading(true);
            const data = await fetchWithAuth(`/users/profile/${userId}`);
            setProfile(data);
            setName(data.name || '');
            setHeadline(data.headline || '');
            setAbout(data.about || '');
            setSkills(data.skills ? data.skills.join(', ') : '');
        } catch (err) {
            setError('Failed to load profile parameters.');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setSaving(true);

        try {
            const skillsArray = skills.split(',').map(s => s.trim()).filter(s => s);

            const payload = {
                name,
                headline,
                about,
                skills: skillsArray
            };

            const data = await fetchWithAuth('/users/profile', {
                method: 'PUT',
                body: JSON.stringify(payload)
            });

            setProfile(data);
            setSuccess('PROFILE PARAMETERS UPDATED SUCCESSFULLY.');
        } catch (err) {
            setError(err.message);
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <div className="container"><h2 className="blinking-cursor">ACCESSING USER DIRECTORY...</h2></div>;

    return (
        <div className="container" style={{ maxWidth: '700px' }}>
            <h1>USER CONFIGURATION</h1>

            {error && <div className="error-msg">{error}</div>}
            {success && <div style={{ color: 'var(--accent-teal)', fontWeight: 'bold', marginBottom: '1rem', padding: '0.5rem', border: '2px solid var(--accent-teal)' }}>{success}</div>}

            <div className="panel">
                <div style={{ display: 'flex', alignItems: 'center', gap: '2rem', marginBottom: '2rem' }}>
                    <div className="avatar" style={{ width: '100px', height: '100px', fontSize: '3rem' }}>
                        {profile?.name?.charAt(0) || 'U'}
                    </div>
                    <div>
                        <h2 style={{ marginBottom: '0.2rem', fontFamily: 'var(--font-mono)' }}>{profile?.name}</h2>
                        <div className="meta-text" style={{ fontSize: '1rem', marginBottom: '0.5rem' }}>ID: {profile?.id} | EMAIL: {profile?.email}</div>
                    </div>
                </div>

                <form onSubmit={handleUpdate}>
                    <div className="input-group">
                        <label className="input-label">DISPLAY NAME:</label>
                        <input
                            type="text"
                            className="input-field"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>

                    <div className="input-group">
                        <label className="input-label">PROFESSIONAL HEADLINE:</label>
                        <input
                            type="text"
                            className="input-field"
                            value={headline}
                            onChange={(e) => setHeadline(e.target.value)}
                            placeholder="e.g. Senior Software Engineer"
                        />
                    </div>

                    <div className="input-group">
                        <label className="input-label">ABOUT SEQUENCE:</label>
                        <textarea
                            className="input-field textarea-field"
                            value={about}
                            onChange={(e) => setAbout(e.target.value)}
                            placeholder="Enter your biography data here..."
                        />
                    </div>

                    <div className="input-group">
                        <label className="input-label">SKILL REGISTRY (Comma Separated):</label>
                        <input
                            type="text"
                            className="input-field"
                            value={skills}
                            onChange={(e) => setSkills(e.target.value)}
                            placeholder="Java, Next.js, Docker, Kafka"
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={saving}
                    >
                        {saving ? '[ SAVING... ]' : '[ OVERWRITE CONFIGURATION ]'}
                    </button>
                </form>
            </div>
        </div>
    );
}
