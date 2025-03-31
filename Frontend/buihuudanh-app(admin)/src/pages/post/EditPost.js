import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const EditPost = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [post, setPost] = useState({
        title: '',
        detail: '',
        description: '',
        topic_id: '',
        type: 'post',
        status: '1',
        image: null,
    });
    const [topics, setTopics] = useState([]);

    useEffect(() => {
        // Fetch the post details
        axios.get(`http://localhost:8000/admin/post/edit/${id}`)
            .then(response => {
                const data = response.data.post;
                setPost({
                    title: data.title,
                    detail: data.detail,
                    description: data.description,
                    topic_id: data.topic_id,
                    type: data.type,
                    status: data.status,
                    image: data.image,
                });
                setTopics(response.data.topics); // Assuming topics are sent from backend
            })
            .catch(error => console.error(error));
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPost({ ...post, [name]: value });
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        const reader = new FileReader();

        reader.onloadend = () => {
            setPost((prevPost) => ({
                ...prevPost,
                image: reader.result, // Base64 string
            }));
        };
        if (file) {
            reader.readAsDataURL(file); // Convert to Base64
        }
    };


    const handleSubmit = async (e) => {
        e.preventDefault();

        const payload = {
            title: post.title,
            detail: post.detail,
            description: post.description,
            topic_id: post.topic_id,
            type: post.type,
            status: post.status,
            image: post.image, // This is now a Base64 encoded string
        };

        try {
            await axios.post(`http://localhost:8000/admin/post/update/${id}`, payload, {
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            navigate('/admin/post');
        } catch (error) {
            console.error(error.response.data);
            alert('Error updating post. Please check the inputs.');
        }
    };


    return (
        <form onSubmit={handleSubmit} className="container mx-60 p-6 bg-white shadow-md rounded-md">
            <h2 className="text-2xl font-bold mb-4">Chỉnh sửa bài viết</h2>

            <div className="mb-4">
                <label htmlFor="title" className="block text-sm font-medium text-gray-700">Tiêu đề</label>
                <input
                    type="text"
                    name="title"
                    id="title"
                    value={post.title}
                    onChange={handleChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
            </div>

            <div className="mb-4">
                <label htmlFor="detail" className="block text-sm font-medium text-gray-700">Chi tiết</label>
                <textarea
                    name="detail"
                    id="detail"
                    value={post.detail}
                    onChange={handleChange}
                    rows="4"
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                ></textarea>
            </div>

            <div className="mb-4">
                <label htmlFor="description" className="block text-sm font-medium text-gray-700">Mô tả</label>
                <textarea
                    name="description"
                    id="description"
                    value={post.description}
                    onChange={handleChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                ></textarea>
            </div>

            <div className="mb-4">
                <label htmlFor="topic_id" className="block text-sm font-medium text-gray-700">Chủ đề</label>
                <select
                    name="topic_id"
                    id="topic_id"
                    value={post.topic_id}
                    onChange={handleChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                    {topics.map(topic => (
                        <option key={topic.id} value={topic.id}>{topic.name}</option>
                    ))}
                </select>
            </div>

            <div className="mb-4">
                <label htmlFor="type" className="block text-sm font-medium text-gray-700">Kiểu</label>
                <select
                    name="type"
                    id="type"
                    value={post.type}
                    onChange={handleChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                    <option value="post">Bài viết</option>
                    <option value="page">Trang đơn</option>
                </select>
            </div>

            <div className="mb-4">
                <label htmlFor="image" className="block text-sm font-medium text-gray-700">Hình</label>
                <input
                    type="file"
                    name="image"
                    id="image"
                    onChange={handleFileChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
            </div>

            <div className="mb-4">
                <label htmlFor="status" className="block text-sm font-medium text-gray-700">Trạng thái</label>
                <select
                    name="status"
                    id="status"
                    value={post.status}
                    onChange={handleChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                    <option value="1">Xuất bản</option>
                    <option value="2">Chưa xuất bản</option>
                </select>
            </div>

            <div className="flex justify-end">
                <button type="submit" className="px-4 py-2 bg-green-500 text-white rounded-md shadow-sm hover:bg-green-600">
                    <i className="fas fa-save mr-2"></i>Lưu
                </button>
                <button
                    type="button"
                    onClick={() => navigate('/admin/post')}
                    className="ml-4 px-4 py-2 bg-gray-500 text-white rounded-md shadow-sm hover:bg-gray-600"
                >
                    <i className="fas fa-arrow-left mr-2"></i>Về danh sách
                </button>
            </div>
        </form>
    );
};

export default EditPost;
