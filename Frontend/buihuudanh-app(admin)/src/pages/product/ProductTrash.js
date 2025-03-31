import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ProductTrash = () => {
    const [deletedProducts, setDeletedProducts] = useState([]);

    // Fetch the list of deleted products from the server
    const fetchDeletedProducts = () => {
        axios.get('http://localhost:8000/admin/product/trash')
            .then(response => {
                console.log("Deleted Products Response:", response.data);
                setDeletedProducts(Array.isArray(response.data) ? response.data : []);
            })
            .catch(error => {
                console.error('Error fetching deleted products!', error);
            });
    };

    useEffect(() => {
        fetchDeletedProducts(); // Fetch products on component mount
    }, []);

    // Restore product
    const restoreProduct = (id) => {
        axios.get(`http://localhost:8000/admin/product/restore/${id}`)
            .then(() => {
                // Filter the product out of the list without re-fetching
                setDeletedProducts(deletedProducts.filter(product => product.id !== id));
            })
            .catch(error => {
                console.error('Error restoring product', error);
            });
    };

    // Permanently delete product and re-fetch the list
    const permanentlyDeleteProduct = (id) => {
        axios.delete(`http://localhost:8000/admin/product/destroy/${id}`)
            .then(() => {
                // Re-fetch the deleted products list after permanent deletion
                fetchDeletedProducts();
            })
            .catch(error => {
                console.error('Error deleting product', error);
            });
    };

    return (
        <div className="container mx-60 p-6">
            <div className="flex justify-end mb-4">
                <a href="/admin/products" className="bg-green-500 text-white px-4 py-2 rounded shadow hover:bg-green-600 transition">
                    <i className="fas fa-arrow-left mr-2"></i>
                    Về Danh sách
                </a>
            </div>
            <table className="table-auto w-full bg-white shadow-md rounded-lg overflow-hidden">
                <thead className="bg-gray-200">
                    <tr>
                        <th className="px-4 py-2 text-left">#</th>
                        <th className="px-4 py-2 text-left">Image</th>
                        <th className="px-4 py-2 text-left">Product Name</th>
                        <th className="px-4 py-2 text-center">Actions</th>
                        <th className="px-4 py-2 text-left">ID</th>
                    </tr>
                </thead>
                <tbody>
                    {deletedProducts.map((product, index) => (
                        <tr key={product.id} className="border-b hover:bg-gray-100">
                            <td className="px-4 py-2"><input type="checkbox" /></td>
                            <td className="px-4 py-2">
                                <img src={`http://localhost:8000/imgs/products/${product.image}`}
                                    alt={product.name}
                                    className="w-24 h-auto rounded"
                                />
                            </td>
                            <td className="px-4 py-2">{product.name}</td>
                            <td className="px-4 py-2 text-center">
                                <button
                                    onClick={() => restoreProduct(product.id)}
                                    className="bg-blue-500 text-white px-3 py-1 rounded shadow hover:bg-blue-600 transition mr-2"
                                >
                                    <i className="fas fa-trash-restore-alt mr-1"></i>
                                    Restore
                                </button>
                                <button
                                    onClick={() => permanentlyDeleteProduct(product.id)}
                                    className="bg-red-500 text-white px-3 py-1 rounded shadow hover:bg-red-600 transition"
                                >
                                    <i className="fas fa-trash mr-1"></i>
                                    Permanently delete
                                </button>
                            </td>
                            <td className="px-4 py-2">{product.id}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ProductTrash;
