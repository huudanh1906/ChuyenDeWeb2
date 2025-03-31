import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, Link } from 'react-router-dom';

const OrderDetail = () => {
  const { id } = useParams(); // Get order ID from URL
  const [order, setOrder] = useState(null);

  useEffect(() => {
    axios.get(`http://localhost:8000/admin/order/show/${id}`)
      .then(response => setOrder(response.data))
      .catch(error => console.error('Error fetching order details:', error));
  }, [id]);

  if (!order) {
    return <div className="text-center p-4">Loading...</div>;
  }

  return (
    <div className="container mx-60 p-4">
      <div className="bg-white shadow-md rounded-lg p-4">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-semibold">Chi tiết đơn hàng #{order[0].id}</h2>
          <Link to="/admin/order" className="bg-green-500 text-white px-4 py-2 rounded">
            <i className="fas fa-arrow-left mr-2"></i> Về Danh sách Đơn hàng
          </Link>
        </div>

        <div className="flex flex-wrap">
          {/* Order Summary */}
          <div className="w-full md:w-1/3">
            <p><strong>ID Đơn hàng:</strong> {order[0].id}</p>
            <p><strong>Người mua:</strong> {order[0].username}</p>
            <p><strong>Ngày tạo:</strong> {order[0].created_at}</p>
            <p><strong>Trạng thái:</strong> {order[0].status === 1 ? 'Đang xử lý' : 'Đã hoàn thành'}</p>
          </div>
        </div>

        <div className="mt-4">
          <h3 className="text-xl font-semibold mb-2">Chi tiết sản phẩm</h3>
          <table className="table-auto w-full bg-white shadow-md rounded-lg">
            <thead>
              <tr className="bg-gray-200 text-left text-sm">
                <th className="p-3">Tên sản phẩm</th>
                <th className="p-3">Giá</th>
                <th className="p-3">Số lượng</th>
                <th className="p-3">Tổng giá</th>
              </tr>
            </thead>
            <tbody>
              {order.map((item, index) => (
                <tr key={index} className="border-t">
                  <td className="p-3">{item.product_name}</td>
                  <td className="p-3">${item.price}</td>
                  <td className="p-3">{item.qty}</td>
                  <td className="p-3">${item.price * item.qty}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default OrderDetail;
