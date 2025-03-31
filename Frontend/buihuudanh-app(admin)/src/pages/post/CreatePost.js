import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const CreatePost = () => {
    const [title, setTitle] = useState('');
    const [detail, setDetail] = useState('');
    const [description, setDescription] = useState('');
    const [topicId, setTopicId] = useState('');
    const [type, setType] = useState('post');
    const [image, setImage] = useState(null);
    const [status, setStatus] = useState('1');
    const [topics, setTopics] = useState([]);
    const navigate = useNavigate(); // Updated hook

    useEffect(() => {
        // Fetch topics from the backend
        const fetchTopics = async () => {
            try {
                const response = await axios.get('http://localhost:8000/admin/topic'); // Replace with your actual API endpoint
                setTopics(response.data);
            } catch (error) {
                console.error('Error fetching topics:', error);
            }
        };
        fetchTopics();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('title', title);
        formData.append('detail', detail);
        formData.append('description', description);
        formData.append('topic_id', topicId);
        formData.append('type', type);
        formData.append('image', image);
        formData.append('status', status);

        try {
            await axios.post('http://localhost:8000/admin/post/store', formData); // Replace with your actual API endpoint
            navigate('/admin/post'); // Redirect to the post list page
        } catch (error) {
            console.error('Error creating post:', error);
        }
    };

    return (
        <form onSubmit={handleSubmit} encType="multipart/form-data" className="max-w-4xl mx-auto p-4 bg-white shadow-lg rounded-lg">
            <h2 className="text-xl font-bold mb-4">Thêm bài viết</h2>
            <div className="flex justify-end mb-4">
                <button type="submit" className="bg-green-500 text-white px-4 py-2 rounded mr-2">
                    <i className="fa fa-save"></i> Lưu
                </button>
                <a href="/admin/post" className="bg-blue-500 text-white px-4 py-2 rounded">
                    <i className="fa fa-arrow-left"></i> Về danh sách
                </a>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label htmlFor="title" className="block mb-1">Tiêu đề</label>
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        id="title"
                        className="border border-gray-300 rounded p-2 w-full"
                    />
                </div>
                <div>
                    <label htmlFor="topic_id" className="block mb-1">Chủ đề</label>
                    <select
                        id="topic_id"
                        value={topicId}
                        onChange={(e) => setTopicId(e.target.value)}
                        className="border border-gray-300 rounded p-2 w-full"
                    >
                        <option value="">Chọn chủ đề</option>
                        {topics.map((topic) => (
                            <option key={topic.id} value={topic.id}>
                                {topic.name}
                            </option>
                        ))}
                    </select>
                </div>
                <div>
                    <label htmlFor="type" className="block mb-1">Kiểu</label>
                    <select
                        id="type"
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                        className="border border-gray-300 rounded p-2 w-full"
                    >
                        <option value="post">Bài viết</option>
                        <option value="page">Trang đơn</option>
                    </select>
                </div>
                <div>
                    <label htmlFor="status" className="block mb-1">Trạng thái</label>
                    <select
                        id="status"
                        value={status}
                        onChange={(e) => setStatus(e.target.value)}
                        className="border border-gray-300 rounded p-2 w-full"
                    >
                        <option value="1">Xuất bản</option>
                        <option value="2">Chưa xuất bản</option>
                    </select>
                </div>
                <div>
                    <label htmlFor="image" className="block mb-1">Hình</label>
                    <input
                        type="file"
                        onChange={(e) => setImage(e.target.files[0])}
                        id="image"
                        className="border border-gray-300 rounded p-2 w-full"
                    />
                </div>
                <div className="col-span-2">
                    <label htmlFor="detail" className="block mb-1">Chi tiết</label>
                    <textarea
                        value={detail}
                        onChange={(e) => setDetail(e.target.value)}
                        id="detail"
                        rows="4"
                        className="border border-gray-300 rounded p-2 w-full"
                    ></textarea>
                </div>
                <div className="col-span-2">
                    <label htmlFor="description" className="block mb-1">Mô tả</label>
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        id="description"
                        className="border border-gray-300 rounded p-2 w-full"
                    ></textarea>
                </div>
            </div>
        </form>
    );
};

export default CreatePost;
