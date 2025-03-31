import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Post = () => {
    const [posts, setPosts] = useState([]);

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await axios.get('http://localhost:8000/admin/post'); // Adjust this endpoint as necessary
                setPosts(response.data);
            } catch (error) {
                console.error('Error fetching posts:', error);
            }
        };
        fetchPosts();
    }, []);

    const toggleStatus = async (id, currentStatus) => {
        try {
            await axios.get(`http://localhost:8000/admin/post/status/${id}`);
            setPosts(prevPosts => 
                prevPosts.map(post =>
                    post.id === id ? { ...post, status: currentStatus === 1 ? 2 : 1 } : post
                )
            );
        } catch (error) {
            console.error('Error toggling post status:', error);
        }
    };

    const deletePost = async (id) => {
        if (window.confirm('Are you sure you want to delete this post?')) {
            try {
                await axios.get(`http://localhost:8000/admin/post/delete/${id}`);
                setPosts(prevPosts => prevPosts.filter(post => post.id !== id));
            } catch (error) {
                console.error('Error deleting post:', error);
            }
        }
    };

    return (
        <div className="container mx-60 p-4">
            <div className="flex justify-end mb-4">
                <Link to="/admin/post/trash" className="btn btn-success mr-2">
                    <i className="fas fa-trash mr-2"></i> Thùng rác
                </Link>
                <Link to="/admin/post/create" className="btn btn-primary">
                    <i className="fas fa-plus mr-2"></i> Thêm
                </Link>
            </div>
            <div className="bg-white shadow-md rounded-lg overflow-hidden">
                <table className="min-w-full">
                    <thead className="bg-gray-200">
                        <tr>
                            <th className="px-4 py-2">#</th>
                            <th className="px-4 py-2">Hình</th>
                            <th className="px-4 py-2">Tên chủ đề</th>
                            <th className="px-4 py-2">Tiêu đề</th>
                            <th className="px-4 py-2">Kiểu</th>
                            <th className="px-4 py-2">Hoạt động</th>
                            <th className="px-4 py-2">ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        {posts.map((post, index) => (
                            <tr key={post.id} className="border-b hover:bg-gray-100">
                                <td className="px-4 py-2">
                                    <input type="checkbox" />
                                </td>
                                <td className="px-4 py-2">
                                    <img src={`http://localhost:8000/imgs/posts/${post.image}`} alt={post.title} className="w-36" />
                                </td>
                                <td className="px-4 py-2">{post.topicname}</td>
                                <td className="px-4 py-2">{post.title}</td>
                                <td className="px-4 py-2">{post.type}</td>
                                <td className="px-4 py-2 flex py-8">
                                    <button onClick={() => toggleStatus(post.id, post.status)} className={`px-2 py-1 rounded ${post.status === 1 ? 'bg-green-500' : 'bg-red-500'} text-white`}>
                                        {post.status === 1 ? 'Activate' : 'Unactivate'}
                                    </button>
                                    <Link to={`/admin/post/show/${post.id}`} className="px-2 py-1 bg-green-500 text-white rounded ml-2">
                                        <i className="fas fa-eye">View</i>
                                    </Link>
                                    <Link to={`/admin/post/edit/${post.id}`} className="px-2 py-1 bg-blue-500 text-white rounded ml-2">
                                        <i className="fas fa-edit">Edit</i>
                                    </Link>
                                    <button onClick={() => deletePost(post.id)} className="px-2 py-1 bg-red-500 text-white rounded ml-2">
                                        <i className="fas fa-trash">Delete</i>
                                    </button>
                                </td>
                                <td className="px-4 py-2">{post.id}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Post;
