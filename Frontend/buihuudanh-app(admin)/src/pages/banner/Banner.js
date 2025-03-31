import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Banner = () => {
    const [banners, setBanners] = useState([]);
    const [formData, setFormData] = useState({
        name: '',
        link: '',
        description: '',
        position: '0',
        image: null,
        status: '2',
    });
    const [errors, setErrors] = useState({});

    useEffect(() => {
        fetchBanners();
    }, []);

    const fetchBanners = async () => {
        const response = await axios.get('http://localhost:8000/admin/banner');
        setBanners(response.data);
    };

    const handleChange = (e) => {
        const { name, value, type, files } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'file' ? files[0] : value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formDataToSend = new FormData();
        Object.keys(formData).forEach(key => {
            formDataToSend.append(key, formData[key]);
        });

        try {
            await axios.post('http://localhost:8000/admin/banner/store', formDataToSend);
            fetchBanners();
            setFormData({
                name: '',
                link: '',
                description: '',
                position: '0',
                image: null,
                status: '2',
            });
            setErrors({});
        } catch (error) {
            setErrors(error.response.data.errors || {});
        }
    };

    const handleDelete = async (id) => {
        await axios.get(`http://localhost:8000/admin/banner/delete/${id}`);
        fetchBanners();
    };

    const handleStatusToggle = async (id) => {
        await axios.get(`http://localhost:8000/admin/banner/status/${id}`);
        fetchBanners();
    };

    return (
        <div className="container mx-60 p-4">
            <div className="bg-white shadow-md rounded-lg p-6 mb-6">
                <h2 className="text-xl font-bold mb-4">Quản lí banner</h2>
                <div className="flex justify-end mb-4">
                    <a href="/admin/banners/trash" className="btn btn-success">
                        <i className="fas fa-trash mr-2"></i>Thùng rác
                    </a>
                </div>
                <form onSubmit={handleSubmit} encType="multipart/form-data" className="flex flex-col md:flex-row">
                    <div className="md:w-1/3 bg-gray-100 p-4 rounded-md">
                        <div className="mb-4">
                            <label htmlFor="name" className="block mb-1">Tên banner</label>
                            <input type="text" name="name" id="name" value={formData.name} onChange={handleChange} className={`form-control ${errors.name ? 'border-red-500' : ''}`} />
                            {errors.name && <span className="text-red-500">{errors.name[0]}</span>}
                        </div>
                        <div className="mb-4">
                            <label htmlFor="link" className="block mb-1">Liên kết</label>
                            <input type="text" name="link" id="link" value={formData.link} onChange={handleChange} className={`form-control ${errors.link ? 'border-red-500' : ''}`} />
                            {errors.link && <span className="text-red-500">{errors.link[0]}</span>}
                        </div>
                        <div className="mb-4">
                            <label htmlFor="description" className="block mb-1">Mô tả</label>
                            <textarea name="description" id="description" value={formData.description} onChange={handleChange} className="form-control"></textarea>
                        </div>
                        <div className="mb-4">
                            <label htmlFor="position" className="block mb-1">Vị trí</label>
                            <select name="position" id="position" value={formData.position} onChange={handleChange} className="form-control">
                                <option value="0">None</option>
                                {/* Populate positions dynamically if needed */}
                                <option value="1">Position 1</option>
                                <option value="2">Position 2</option>
                                {/* Add more positions as necessary */}
                            </select>
                        </div>
                        <div className="mb-4">
                            <label htmlFor="image" className="block mb-1">Hình</label>
                            <input type="file" name="image" id="image" onChange={handleChange} className={`form-control ${errors.image ? 'border-red-500' : ''}`} />
                            {errors.image && <span className="text-red-500">{errors.image[0]}</span>}
                        </div>
                        <div className="mb-4">
                            <label htmlFor="status" className="block mb-1">Trạng thái</label>
                            <select name="status" id="status" value={formData.status} onChange={handleChange} className="form-control">
                                <option value="2">Chưa xuất bản</option>
                                <option value="1">Xuất bản</option>
                            </select>
                        </div>
                        <button type="submit" className="btn btn-success">Thêm banner</button>
                    </div>
                    <div className="md:w-2/3 md:ml-4">
                        <table className="min-w-full bg-white border border-gray-300">
                            <thead>
                                <tr>
                                    <th className="border px-4 py-2">#</th>
                                    <th className="border px-4 py-2">Hình</th>
                                    <th className="border px-4 py-2">Tên banner</th>
                                    <th className="border px-4 py-2">Vị trí</th>
                                    <th className="border px-4 py-2">Hoạt động</th>
                                    <th className="border px-4 py-2">ID</th>
                                </tr>
                            </thead>
                            <tbody>
                                {banners.map((banner, index) => (
                                    <tr key={banner.id}>
                                        <td className="border px-4 py-2"><input type="checkbox" /></td>
                                        <td className="border px-4 py-2">
                                            <img src={`http://localhost:8000/imgs/banners/${banner.image}`} alt={banner.name} className="w-12 h-12" />
                                        </td>
                                        <td className="border px-4 py-2">{banner.name}</td>
                                        <td className="border px-4 py-2">{banner.position}</td>
                                        <td className="border px-4 py-2 flex justify-center space-x-2">
                                            <button onClick={() => handleStatusToggle(banner.id)} className={`btn ${banner.status === 1 ? 'bg-green-500 text-white p-2 rounded ml-2' : 'bg-red-500 text-white p-2 rounded ml-2'}`}>
                                                {banner.status === 1 ? "Activate" : "Unactivate"}
                                            </button>
                                            <Link to={`/admin/banners/show/${banner.id}`} className="bg-blue-500 text-white p-2 rounded ml-2">
                                                <i className="fas fa-eye">View</i>
                                            </Link>
                                            <Link to={`/admin/banners/edit/${banner.id}`} className="bg-yellow-500 text-white p-2 rounded ml-2">
                                                <i className="fas fa-edit">Edit</i>
                                            </Link>
                                            <button onClick={() => handleDelete(banner.id)} className="bg-red-500 text-white p-2 rounded ml-2">Delete</button>
                                        </td>
                                        <td className="border px-4 py-2">{banner.id}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Banner;
