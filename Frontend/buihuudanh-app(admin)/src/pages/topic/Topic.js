import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Topic = () => {
    const [topics, setTopics] = useState([]);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        sort_order: '0',
        status: '2',
    });
    const [errors, setErrors] = useState({});

    useEffect(() => {
        fetchTopics();
    }, []);

    const fetchTopics = async () => {
        try {
            const response = await axios.get('http://localhost:8000/admin/topic'); // adjust API endpoint
            setTopics(response.data);
        } catch (error) {
            console.error('Error fetching topics:', error);
        }
    };

    const handleStatusToggle = async (id) => {
        await axios.get(`http://localhost:8000/admin/topic/status/${id}`);
        fetchTopics();
    };

    const handleDelete = async (id) => {
        await axios.get(`http://localhost:8000/admin/topic/delete/${id}`);
        fetchTopics();
    };

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:8000/admin/topic/store', formData); // adjust API endpoint
            fetchTopics();
        } catch (error) {
            if (error.response && error.response.data) {
                setErrors(error.response.data.errors);
            }
        }
    };

    return (
        <div className="container mx-60 p-4">
            <div className="flex justify-end mb-4">
                <a href="/admin/topic/trash" className="btn bg-green-500 text-white p-2 rounded">
                    <i className="fas fa-trash mr-2"></i>Thùng rác
                </a>
            </div>
            <div className="flex flex-wrap">
                <div className="w-full md:w-1/3 bg-gray-100 p-4">
                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label className="block mb-2" htmlFor="name">Tên chủ đề</label>
                            <input
                                type="text"
                                name="name"
                                id="name"
                                value={formData.name}
                                onChange={handleChange}
                                className="form-input w-full p-2 border rounded"
                            />
                            {errors.name && <span className="text-red-500">{errors.name}</span>}
                        </div>

                        <div className="mb-4">
                            <label className="block mb-2" htmlFor="description">Mô tả</label>
                            <textarea
                                name="description"
                                id="description"
                                value={formData.description}
                                onChange={handleChange}
                                className="form-textarea w-full p-2 border rounded"
                            ></textarea>
                        </div>

                        <div className="mb-4">
                            <label className="block mb-2" htmlFor="sort_order">Sắp xếp</label>
                            <select
                                name="sort_order"
                                id="sort_order"
                                value={formData.sort_order}
                                onChange={handleChange}
                                className="form-select w-full p-2 border rounded"
                            >
                                <option value="0">None</option>
                                {topics.map((topic) => (
                                    <option key={topic.id} value={topic.id + 1}>Sau: {topic.name}</option>
                                ))}
                            </select>
                        </div>

                        <div className="mb-4">
                            <label className="block mb-2" htmlFor="status">Trạng thái</label>
                            <select
                                name="status"
                                id="status"
                                value={formData.status}
                                onChange={handleChange}
                                className="form-select w-full p-2 border rounded"
                            >
                                <option value="2">Chưa xuất bản</option>
                                <option value="1">Xuất bản</option>
                            </select>
                        </div>

                        <div className="mb-4">
                            <button type="submit" className="bg-green-500 text-white p-2 rounded">
                                Thêm danh mục
                            </button>
                        </div>
                    </form>
                </div>

                <div className="w-full md:w-2/3">
                    <table className="table-auto w-full border-collapse">
                        <thead>
                            <tr>
                                <th className="border px-4 py-2">#</th>
                                <th className="border px-4 py-2">Tên chủ đề</th>
                                <th className="border px-4 py-2">Miêu tả</th>
                                <th className="border px-4 py-2">Hoạt động</th>
                                <th className="border px-4 py-2">ID</th>
                            </tr>
                        </thead>
                        <tbody>
                            {topics.map((topic) => (
                                <tr key={topic.id}>
                                    <td className="border px-4 py-2">
                                        <input type="checkbox" />
                                    </td>
                                    <td className="border px-4 py-2">{topic.name}</td>
                                    <td className="border px-4 py-2">{topic.description}</td>
                                    <td className="border px-4 py-2 flex justify-around">
                                        <button onClick={() => handleStatusToggle(topic.id)} className={`btn ${topic.status === 1 ? 'bg-green-500 text-white p-2 rounded ml-2' : 'bg-red-500 text-white p-2 rounded ml-2'}`}>
                                            {topic.status === 1 ? "Activate" : "Unactivate"}
                                        </button>
                                        <Link to={`/admin/topic/show/${topic.id}`} className="btn bg-blue-500 text-white p-2 rounded">
                                            <i className="fas fa-eye">View</i>
                                        </Link>
                                        <Link to={`/admin/topic/edit/${topic.id}`} className="btn bg-yellow-500 text-white p-2 rounded">
                                            <i className="fas fa-edit">Edit</i>
                                        </Link>
                                        <button onClick={() => handleDelete(topic.id)} className="bg-red-500 text-white p-2 rounded">Delete</button>
                                    </td>
                                    <td className="border px-4 py-2">{topic.id}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default Topic;
