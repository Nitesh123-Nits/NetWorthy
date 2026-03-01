"use client";

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { fetchWithAuth, getToken, getUserId } from '@/lib/api';

export default function Home() {
  const router = useRouter();
  const [posts, setPosts] = useState([]);
  const [newPostContent, setNewPostContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    // Check auth on mount
    const token = getToken();
    const currentUserId = getUserId();

    if (!token) {
      router.push('/login');
      return;
    }

    setUserId(currentUserId);
    loadPosts();
  }, [router]);

  const loadPosts = async () => {
    try {
      setLoading(true);
      const data = await fetchWithAuth('/posts/core');
      setPosts(data);
    } catch (err) {
      setError('Failed to load feed. Connection error.');
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePost = async (e) => {
    e.preventDefault();
    if (!newPostContent.trim()) return;

    try {
      await fetchWithAuth('/posts/core', {
        method: 'POST',
        body: JSON.stringify({ content: newPostContent }),
      });
      setNewPostContent('');
      loadPosts(); // Reload feed to show new post
    } catch (err) {
      setError(err.message);
    }
  };

  const handleLike = async (postId, isLiked) => {
    try {
      if (isLiked) {
        // Technically backend doesn't return count on unlike/like immediately in this simplified UI flow
        // For a true reactive UI we'd fetch the specific likes again or update state locally
        await fetchWithAuth(`/posts/likes/${postId}`, { method: 'DELETE' });
      } else {
        await fetchWithAuth(`/posts/likes/${postId}`, { method: 'POST' });
      }

      // Simulating a minor delay before reloading to allow Kafka/DB to process if needed
      // but in this synchronous API call the db is updated immediately
      setTimeout(loadPosts, 200);

    } catch (err) {
      setError("Failed to process like event.");
    }
  }

  if (loading) {
    return (
      <div className="container" style={{ textAlign: 'center', marginTop: '5rem' }}>
        <h2 className="blinking-cursor">LOADING SYSTEM RESOURCES...</h2>
      </div>
    );
  }

  return (
    <div className="container">
      {error && <div className="error-msg">{error}</div>}

      <div className="panel" style={{ backgroundColor: 'var(--accent-teal)', color: 'white' }}>
        <h3 style={{ textTransform: 'uppercase' }}>Broadc@st a Message</h3>
        <form onSubmit={handleCreatePost} style={{ marginTop: '1rem' }}>
          <textarea
            className="input-field textarea-field"
            placeholder="TYPE YOUR TRANSMISSION HERE..."
            value={newPostContent}
            onChange={(e) => setNewPostContent(e.target.value)}
            style={{ marginBottom: '1rem' }}
          ></textarea>
          <button type="submit" className="btn btn-primary" style={{ backgroundColor: 'var(--accent-yellow)', color: 'var(--text-main)' }}>
            [ TRANSMIT ]
          </button>
        </form>
      </div>

      <h1 style={{ marginBottom: '2rem' }}>NETWORK FEED</h1>

      {posts.length === 0 ? (
        <div className="panel" style={{ textAlign: 'center' }}>
          <p style={{ fontFamily: 'var(--font-mono)' }}>NO TRANSMISSIONS DETECTED IN THE AREA.</p>
        </div>
      ) : (
        posts.map((post) => (
          <div key={post.id} className="panel" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div className="flex-row">
              <div className="avatar">U</div>
              <div>
                <div style={{ fontWeight: 'bold' }}>USER IDENTIFIER: {post.userId}</div>
                <div className="meta-text">LOGGED AT: {new Date(post.createdAt).toLocaleString()}</div>
              </div>
            </div>

            <div style={{ padding: '1rem 0', fontFamily: 'var(--font-mono)', fontSize: '1.1rem', whiteSpace: 'pre-wrap' }}>
              {post.content}
            </div>

            <div style={{ borderTop: 'var(--border-thin)', paddingTop: '1rem', display: 'flex', gap: '1rem' }}>
              <button
                className="btn"
                onClick={() => handleLike(post.id, false)}
                style={{ padding: '0.3rem 0.8rem', fontSize: '0.85rem' }}
              >
                [ LIKE ] ({post.likesCount || 0})
              </button>
            </div>
          </div>
        ))
      )}
    </div>
  );
}
