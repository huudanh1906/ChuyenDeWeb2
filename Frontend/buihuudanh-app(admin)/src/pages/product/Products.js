import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const ProductList = () => {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:8000/admin/product')
            .then(response => {
                console.log("API Response:", response.data);
                setProducts(Array.isArray(response.data) ? response.data : []);
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }, []);

    // Toggle product status
    const toggleStatus = (id, currentStatus) => {
        axios.get(`http://localhost:8000/admin/product/status/${id}`)
            .then(response => {
                setProducts(products.map(product =>
                    product.id === id ? { ...product, status: currentStatus === 1 ? 2 : 1 } : product
                ));
            })
            .catch(error => {
                console.error('Error toggling status', error);
            });
    };

    // Delete product
    const deleteProduct = (id) => {
        axios.get(`http://localhost:8000/admin/product/delete/${id}`)
            .then(() => {
                setProducts(products.filter(product => product.id !== id));
            })
            .catch(error => console.error('Error deleting product', error));
    };

    return (
        <div className="container mx-60 p-4">
            <div className="flex justify-end mb-4">
                <button className="bg-green-500 text-white px-4 py-2 rounded">
                    <Link to="/admin/products/trash" className="flex items-center justify-center h-full w-full">Trash</Link>
                </button>
                <button className="bg-blue-500 text-white ml-2 px-4 py-2 rounded">
                    <Link to="/admin/products/create" className="flex items-center justify-center h-full w-full">Add Product</Link>
                </button>
            </div>
            <div className="overflow-x-auto">
                <table className="table-auto w-full bg-white shadow-md rounded-lg">
                    <thead>
                        <tr className="bg-gray-200 text-left text-sm">
                            <th className="p-3">#</th>
                            <th className="p-3">Image</th>
                            <th className="p-3">Product Name</th>
                            <th className="p-3">Price</th>
                            <th className="p-3">Discount Price</th>
                            <th className="p-3">Category</th>
                            <th className="p-3">Brand</th>
                            <th className="p-3">Actions</th>
                            <th className="p-3">ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map((product, index) => (
                            <tr key={product.id} className="border-t">
                                <td className="p-3"><input type="checkbox" /></td>
                                <td className="p-3">
                                    <img
                                        src={`http://localhost:8000/imgs/products/${product.image}`}
                                        alt={product.name}
                                        className="w-16 h-16 object-cover rounded"
                                    />
                                </td>
                                <td className="p-3">{product.name}</td>
                                <td className="p-3">${product.price}</td>
                                <td className="p-3">${product.pricesale}</td>
                                <td className="p-3">{product.categoryname}</td>
                                <td className="p-3">{product.brandname}</td>
                                <td className="p-3 space-x-2">
                                    {/* Toggle status button */}
                                    <button
                                        onClick={() => toggleStatus(product.id, product.status)}
                                        className={product.status === 1 ? "bg-green-500 text-white px-2 py-1 rounded" : "bg-red-500 text-white px-2 py-1 rounded"}>
                                        {product.status === 1 ? 'Active' : 'Inactive'}
                                    </button>

                                    {/* View button to navigate to ProductDetail */}
                                    <Link to={`/admin/products/show/${product.id}`} className="bg-blue-500 text-white px-2 py-1 rounded">
                                        View
                                    </Link>

                                    {/* Edit button to navigate to EditProduct */}
                                    <Link to={`/admin/products/edit/${product.id}`} className="bg-blue-500 text-white px-2 py-1 rounded">
                                        Edit
                                    </Link>

                                    <button
                                        onClick={() => deleteProduct(product.id)}
                                        className="bg-red-500 text-white px-2 py-1 rounded"
                                    >
                                        Delete
                                    </button>
                                </td>
                                <td className="p-3">{product.id}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ProductList;
