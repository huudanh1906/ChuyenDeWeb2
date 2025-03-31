import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, Link } from 'react-router-dom';

const ProductDetail = () => {
  const { id } = useParams(); // Get product ID from URL
  const [product, setProduct] = useState(null);

  useEffect(() => {
    axios.get(`http://localhost:8000/admin/product/show/${id}`)
      .then(response => setProduct(response.data))
      .catch(error => console.error('Error fetching product details:', error));
  }, [id]);

  if (!product) {
    return <div className="text-center p-4">Loading...</div>;
  }

  return (
    <div className="container mx-60 p-4">
      <div className="bg-white shadow-md rounded-lg p-4">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-semibold">Tên sản phẩm: {product.name}</h2>
          <Link to="/admin/products" className="btn btn-success bg-green-500 text-white px-4 py-2 rounded">
            <i className="fas fa-arrow-left mr-2"></i> Về Danh sách
          </Link>
        </div>

        <div className="flex flex-wrap">
          <div className="w-full md:w-1/4">
            <img 
              src={`http://localhost:8000/imgs/products/${product.image}`} 
              alt={product.name} 
              className="w-full h-auto rounded"
              style={{ width: '150px' }}
            />
          </div>

          <div className="w-full md:w-1/4 mt-4 md:mt-0">
            <p><strong>ID:</strong> {product.id}</p>
            <p><strong>Giá:</strong> ${product.price}</p>
            <p><strong>Giá giảm:</strong> ${product.pricesale}</p>
            <p><strong>Chi tiết:</strong> {product.detail}</p>
          </div>

          <div className="w-full md:w-2/4 mt-4 md:mt-0">
            <p><strong>Miêu tả:</strong> {product.description}</p>
            <p><strong>Created At:</strong> {product.created_at}</p>
            <p><strong>Updated At:</strong> {product.updated_at}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;