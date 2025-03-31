import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const EditProduct = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [product, setProduct] = useState({
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
    const [errors, setErrors] = useState({});

    useEffect(() => {
        fetchProduct();
        fetchCategories();
        fetchBrands();
    }, []);

    const fetchProduct = async () => {
        try {
            const response = await axios.get(`http://localhost:8000/admin/product/show/${id}`);
            setProduct(response.data);
        } catch (error) {
            console.error('Error fetching product:', error);
        }
    };

    const fetchCategories = async () => {
        try {
            const response = await axios.get('http://localhost:8000/admin/category');
            setCategories(response.data);
        } catch (error) {
            console.error('Error fetching categories:', error);
        }
    };

    const fetchBrands = async () => {
        try {
            const response = await axios.get('http://localhost:8000/admin/brand');
            setBrands(response.data);
        } catch (error) {
            console.error('Error fetching brands:', error);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setProduct({ ...product, [name]: value });
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        const reader = new FileReader();
        reader.onloadend = () => {
            setProduct({ ...product, image: reader.result }); // Base64 string
        };
        if (file) {
            reader.readAsDataURL(file);
        }
    };
    

    const handleSubmit = async (e) => {
        e.preventDefault();
        const payload = {
            ...product,
            image: product.image, // Base64 string
        };
    
        try {
            const response = await axios.put(`http://localhost:8000/admin/product/update/${id}`, payload, {
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            console.log('Product updated successfully:', response.data);
            navigate('/admin/products')
        } catch (error) {
            if (error.response) {
                // Log error details returned from the server (validation errors, etc.)
                console.log('Error details:', error.response.data); // Shows the Laravel validation messages
            } else if (error.request) {
                console.log('No response received:', error.request);
            } else {
                console.log('Error setting up the request:', error.message);
            }
        }
    };

    return (
        <div className="container mx-60 p-6">
            <h2 className="text-2xl font-bold mb-6">Chỉnh sửa sản phẩm</h2>
            <form onSubmit={handleSubmit} encType="multipart/form-data">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <div className="mb-5">
                            <label htmlFor="name" className="block text-sm font-medium text-gray-700">Tên sản phẩm</label>
                            <input
                                type="text"
                                name="name"
                                value={product.name}
                                onChange={handleInputChange}
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            />
                            {errors.name && <span className="text-red-500 text-sm">{errors.name}</span>}
                        </div>

                        <div className="mb-5">
                            <label htmlFor="detail" className="block text-sm font-medium text-gray-700">Chi tiết</label>
                            <textarea
                                name="detail"
                                value={product.detail}
                                onChange={handleInputChange}
                                rows="5"
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            />
                            {errors.detail && <span className="text-red-500 text-sm">{errors.detail}</span>}
                        </div>

                        <div className="mb-5">
                            <label htmlFor="description" className="block text-sm font-medium text-gray-700">Mô tả</label>
                            <textarea
                                name="description"
                                value={product.description}
                                onChange={handleInputChange}
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            />
                        </div>

                        <div className="mb-5">
                            <label htmlFor="category_id" className="block text-sm font-medium text-gray-700">Danh mục</label>
                            <select
                                name="category_id"
                                value={product.category_id}
                                onChange={handleInputChange}
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            >
                                {categories.map(category => (
                                    <option key={category.id} value={category.id}>
                                        {category.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div>
                        <div className="mb-5">
                            <label htmlFor="brand_id" className="block text-sm font-medium text-gray-700">Thương hiệu</label>
                            <select
                                name="brand_id"
                                value={product.brand_id}
                                onChange={handleInputChange}
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            >
                                {brands.map(brand => (
                                    <option key={brand.id} value={brand.id}>
                                        {brand.name}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="mb-5">
                            <label htmlFor="price" className="block text-sm font-medium text-gray-700">Giá</label>
                            <input
                                type="number"
                                name="price"
                                value={product.price}
                                onChange={handleInputChange}
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            />
                            {errors.price && <span className="text-red-500 text-sm">{errors.price}</span>}
                        </div>

                        <div className="mb-5">
                            <label htmlFor="pricesale" className="block text-sm font-medium text-gray-700">Giá khuyến mãi</label>
                            <input
                                type="number"
                                name="pricesale"
                                value={product.pricesale}
                                onChange={handleInputChange}
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            />
                            {errors.pricesale && <span className="text-red-500 text-sm">{errors.pricesale}</span>}
                        </div>

                        <div className="mb-5">
                            <label htmlFor="image" className="block text-sm font-medium text-gray-700">Hình</label>
                            <input
                                type="file"
                                name="image"
                                onChange={handleFileChange}
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            />
                            {errors.image && <span className="text-red-500 text-sm">{errors.image}</span>}
                        </div>

                        <div className="mb-5">
                            <label htmlFor="status" className="block text-sm font-medium text-gray-700">Trạng thái</label>
                            <select
                                name="status"
                                value={product.status}
                                onChange={handleInputChange}
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                            >
                                <option value="1">Xuất bản</option>
                                <option value="2">Chưa xuất bản</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div className="flex justify-end mt-6">
                    <button type="submit" className="bg-green-500 text-white px-4 py-2 rounded-md shadow hover:bg-green-600">Lưu</button>
                    <button
                        type="button"
                        onClick={() => navigate('/admin/products')}
                        className="ml-4 bg-gray-500 text-white px-4 py-2 rounded-md shadow hover:bg-gray-600"
                    >
                        Về danh sách
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditProduct;
