import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const CreateProduct = () => {
    const [formData, setFormData] = useState({
        name: '',
        detail: '',
        description: '',
        category_id: '',
        brand_id: '',
        price: '',
        pricesale: '',
        image: null,
        status: '1',
    });

    const [categories, setCategories] = useState([]);
    const [brands, setBrands] = useState([]);
    const navigate = useNavigate(); // Use useNavigate instead of useHistory

    useEffect(() => {
        // Fetch categories and brands
        const fetchCategoriesAndBrands = async () => {
            const categoriesResponse = await axios.get('http://localhost:8000/admin/category');
            const brandsResponse = await axios.get('http://localhost:8000/admin/brand');
            setCategories(categoriesResponse.data);
            setBrands(brandsResponse.data);
        };

        fetchCategoriesAndBrands();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleFileChange = (e) => {
        setFormData({ ...formData, image: e.target.files[0] });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const payload = new FormData();

        Object.keys(formData).forEach((key) => {
            payload.append(key, formData[key]);
        });

        try {
            await axios.post('http://localhost:8000/admin/product/store', payload);
            navigate('/admin/products'); // Redirect to product list
        } catch (error) {
            console.error('Error creating product:', error.response.data);
        }
    };

    return (
        <div className="container mx-60 py-8 px-8">
            <h2 className="text-2xl font-bold mb-6">Thêm sản phẩm</h2>
            <form onSubmit={handleSubmit} encType="multipart/form-data">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label className="block mb-2" htmlFor="name">Tên sản phẩm</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className="border rounded p-2 w-full"
                            required
                        />
                    </div>
                    <div>
                        <label className="block mb-2" htmlFor="detail">Chi tiết</label>
                        <textarea
                            id="detail"
                            name="detail"
                            value={formData.detail}
                            onChange={handleChange}
                            className="border rounded p-2 w-full"
                            rows="5"
                            required
                        />
                    </div>
                    <div>
                        <label className="block mb-2" htmlFor="description">Mô tả</label>
                        <textarea
                            id="description"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            className="border rounded p-2 w-full"
                        />
                    </div>
                    <div>
                        <label className="block mb-2" htmlFor="category_id">Danh mục</label>
                        <select
                            id="category_id"
                            name="category_id"
                            value={formData.category_id}
                            onChange={handleChange}
                            className="border rounded p-2 w-full"
                            required
                        >
                            <option value="">Chọn danh mục</option>
                            {categories.map(category => (
                                <option key={category.id} value={category.id}>{category.name}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block mb-2" htmlFor="brand_id">Thương hiệu</label>
                        <select
                            id="brand_id"
                            name="brand_id"
                            value={formData.brand_id}
                            onChange={handleChange}
                            className="border rounded p-2 w-full"
                            required
                        >
                            <option value="">Chọn thương hiệu</option>
                            {brands.map(brand => (
                                <option key={brand.id} value={brand.id}>{brand.name}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block mb-2" htmlFor="price">Giá</label>
                        <input
                            type="number"
                            id="price"
                            name="price"
                            value={formData.price}
                            onChange={handleChange}
                            className="border rounded p-2 w-full"
                            required
                        />
                    </div>
                    <div>
                        <label className="block mb-2" htmlFor="pricesale">Giá khuyến mãi</label>
                        <input
                            type="number"
                            id="pricesale"
                            name="pricesale"
                            value={formData.pricesale}
                            onChange={handleChange}
                            className="border rounded p-2 w-full"
                        />
                    </div>
                    <div>
                        <label className="block mb-2" htmlFor="image">Hình</label>
                        <input
                            type="file"
                            id="image"
                            name="image"
                            onChange={handleFileChange}
                            className="border rounded p-2 w-full"
                        />
                    </div>
                    <div>
                        <label className="block mb-2" htmlFor="status">Trạng thái</label>
                        <select
                            id="status"
                            name="status"
                            value={formData.status}
                            onChange={handleChange}
                            className="border rounded p-2 w-full"
                        >
                            <option value="1">Xuất bản</option>
                            <option value="2">Chưa xuất bản</option>
                        </select>
                    </div>
                </div>
                <div className="mt-4">
                    <button type="submit" className="bg-green-500 text-white py-2 px-4 rounded">
                        Lưu
                    </button>
                    <button type="button" onClick={() => navigate('/admin/product')} className="bg-blue-500 text-white py-2 px-4 rounded ml-2">
                        Về danh sách
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateProduct;
