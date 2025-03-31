import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";

const EditBanner = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [banner, setBanner] = useState({
        name: '',
        link: '',
        description: '',
        position: '',
        image: null,
        imageBase64: '', // For storing base64 image
        status: '1'
    });
    const [positionOptions, setPositionOptions] = useState('');

    useEffect(() => {
        const fetchBannerDetails = async () => {
            try {
                const response = await axios.get(`http://localhost:8000/admin/banner/edit/${id}`);
                const bannerData = response.data.banner;
                setBanner({
                    name: bannerData.name,
                    link: bannerData.link,
                    description: bannerData.description || '',
                    position: bannerData.position,
                    status: bannerData.status.toString(),
                    imageBase64: bannerData.imageBase64 || '' // This will hold base64 data if it's loaded from backend
                });
                setPositionOptions(response.data.position); // Backend provides options as HTML strings
            } catch (error) {
                console.error("Error fetching banner details", error);
            }
        };

        fetchBannerDetails();
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setBanner({
            ...banner,
            [name]: value
        });
    };

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setBanner({
                    ...banner,
                    imageBase64: reader.result
                });
            };
            reader.readAsDataURL(file); // Converts the file to base64
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.put(`http://localhost:8000/admin/banner/update/${id}`, {
                name: banner.name,
                link: banner.link,
                description: banner.description,
                position: banner.position,
                status: banner.status,
                imageBase64: banner.imageBase64 // Sending base64 encoded image
            }, {
                headers: {
                    "Content-Type": "application/json"
                }
            });
            navigate("/admin/banners");
        } catch (error) {
            console.error("Error updating banner", error);
        }
    };

    return (
        <div className="container mx-60 p-6">
            <div className="bg-white shadow-md rounded-lg p-6">
                <div className="flex justify-between items-center border-b pb-4 mb-4">
                    <h2 className="text-2xl font-semibold">Chỉnh sửa Banner</h2>
                    <Link
                        to="/admin/banners"
                        className="bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600 flex items-center"
                    >
                        <i className="fas fa-arrow-left mr-2"></i>Về Danh sách
                    </Link>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label htmlFor="name" className="block text-lg font-medium">Tên Banner</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={banner.name}
                            onChange={handleChange}
                            className="w-full px-4 py-2 border rounded-lg"
                        />
                    </div>

                    <div>
                        <label htmlFor="link" className="block text-lg font-medium">Liên kết</label>
                        <input
                            type="text"
                            id="link"
                            name="link"
                            value={banner.link}
                            onChange={handleChange}
                            className="w-full px-4 py-2 border rounded-lg"
                        />
                    </div>

                    <div>
                        <label htmlFor="description" className="block text-lg font-medium">Mô tả</label>
                        <textarea
                            id="description"
                            name="description"
                            value={banner.description}
                            onChange={handleChange}
                            className="w-full px-4 py-2 border rounded-lg"
                        />
                    </div>

                    <div>
                        <label htmlFor="position" className="block text-lg font-medium">Vị trí</label>
                        <select
                            id="position"
                            name="position"
                            value={banner.position}
                            onChange={handleChange}
                            className="w-full px-4 py-2 border rounded-lg"
                        >
                            <option value="0">None</option>
                            {/* Inject the position options from backend */}
                            <div dangerouslySetInnerHTML={{ __html: positionOptions }} />
                        </select>
                    </div>

                    <div>
                        <label htmlFor="image" className="block text-lg font-medium">Hình</label>
                        <input
                            type="file"
                            id="image"
                            name="image"
                            onChange={handleFileChange}
                            className="w-full px-4 py-2 border rounded-lg"
                        />
                    </div>

                    <div>
                        <label htmlFor="status" className="block text-lg font-medium">Trạng thái</label>
                        <select
                            id="status"
                            name="status"
                            value={banner.status}
                            onChange={handleChange}
                            className="w-full px-4 py-2 border rounded-lg"
                        >
                            <option value="2">Chưa xuất bản</option>
                            <option value="1">Xuất bản</option>
                        </select>
                    </div>

                    <div>
                        <button type="submit" className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600">
                            Sửa Banner
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditBanner;
