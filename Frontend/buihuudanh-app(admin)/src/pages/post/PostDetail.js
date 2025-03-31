import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const PostDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const response = await axios.get(`http://localhost:8000/admin/post/show/${id}`);
        setPost(response.data);
      } catch (error) {
        console.error('Error fetching post:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPost();
  }, [id]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!post) {
    return <div>Post not found</div>;
  }

  return (
    <div className="container mx-60 px-6">
      <div className="card">
        <div className="card-header row" style={{ display: 'flex', justifyContent: 'space-between' }}>
          <h2 className="col-6">Tên bài viết: {post.title}</h2>
          <div className="col-6 text-right">
            <button className="btn btn-success" onClick={() => navigate('/admin/post')}>
              <i className="fas fa-arrow-left" style={{ marginRight: '10px' }}></i>
              Về Danh sách
            </button>
          </div>
        </div>

        <div className="row m-4">
          <div className="col-md-3">
            <img
              src={`http://localhost:8000/imgs/posts/${post.image}`}
              alt={post.title}
              className="img-fluid"
              style={{ width: '150px' }}
            />
          </div>
          <div className="col-md-4">
            <p>ID: {post.id}</p>
            <p>Kiểu: {post.type}</p>
            <p>Miêu tả: {post.description}</p>
            <p>Created At: {new Date(post.created_at).toLocaleString()}</p>
            <p>Updated At: {new Date(post.updated_at).toLocaleString()}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostDetail;
