import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

const PostTrash = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPosts();
  }, []);

  const fetchPosts = async () => {
    try {
      const response = await axios.get('http://localhost:8000/admin/post/trash');
      setPosts(response.data);
    } catch (error) {
      console.error('Error fetching posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRestore = async (id) => {
    try {
      await axios.get(`http://localhost:8000/admin/post/restore/${id}`);
      // Fetch posts again after restoring
      fetchPosts();
    } catch (error) {
      console.error('Error restoring post:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to permanently delete this post?')) {
      try {
        await axios.delete(`http://localhost:8000/admin/post/destroy/${id}`);
        // Fetch posts again after deletion
        fetchPosts();
      } catch (error) {
        console.error('Error deleting post:', error);
      }
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container mx-60 p-4">
      <div className="flex justify-end mb-4">
        <Link to="/admin/post" className="btn btn-success">
          <i className="fas fa-arrow-left mr-2"></i>
          Về Danh sách
        </Link>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr>
              <th className="border px-4 py-2">#</th>
              <th className="border px-4 py-2">Hình</th>
              <th className="border px-4 py-2">Tên bài viết</th>
              <th className="border px-4 py-2 text-center">Hoạt động</th>
              <th className="border px-4 py-2">ID</th>
            </tr>
          </thead>
          <tbody>
            {posts.map((post, index) => (
              <tr key={post.id}>
                <td className="border px-4 py-2"><input type="checkbox" /></td>
                <td className="border px-4 py-2">
                  <img
                    src={`http://localhost:8000/imgs/posts/${post.image}`}
                    alt={post.title}
                    className="w-12 h-12 object-cover"
                  />
                </td>
                <td className="border px-4 py-2">{post.title}</td>
                <td className="border px-4 py-2 text-center">
                  <button
                    onClick={() => handleRestore(post.id)}
                    className="px-2 py-1 bg-blue-500 text-white rounded ml-2"
                  >
                    <i className="fas fa-trash-restore-alt"></i> Restore
                  </button>
                  <Link to={`/admin/post/show/${post.id}`} className="px-2 py-1 bg-green-500 text-white rounded ml-2">
                    <i className="fas fa-eye"></i> View
                  </Link>
                  <button
                    onClick={() => handleDelete(post.id)}
                    className="px-2 py-1 bg-red-500 text-white rounded ml-2"
                  >
                    <i className="fas fa-trash"></i> Permanently Delete
                  </button>
                </td>
                <td className="border px-4 py-2">{post.id}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default PostTrash;
