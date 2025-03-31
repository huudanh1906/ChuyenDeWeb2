import React, { useState } from 'react';
import { Link } from 'react-router-dom';

const Sidebar = () => {
  const [isProductOpen, setIsProductOpen] = useState(false);
  const [isPostOpen, setIsPostOpen] = useState(false);
  const [isAppearanceOpen, setIsAppearanceOpen] = useState(false);
  const [isUserOpen, setIsUserOpen] = useState(false);

  return (
    <aside className="bg-gray-800 text-white w-64 h-screen fixed">
      <div className="p-4">
        <Link to="/" className="flex items-center">
          <img
            src="https://t4.ftcdn.net/jpg/02/29/75/83/360_F_229758328_7x8jwCwjtBMmC6rgFzLFhZoEpLobB6L8.jpg"
            alt="AdminLTE Logo"
            className="h-8 w-8 mr-3 rounded-full opacity-80"
          />
          <span className="text-lg font-light">Admin</span>
        </Link>
      </div>
      <div className="p-4">
        <div className="flex items-center mb-4">
          <img
            src="https://cdn.pixabay.com/photo/2020/07/01/12/58/icon-5359553_640.png"
            alt="User Image"
            className="h-10 w-10 rounded-full"
          />
          <div className="ml-2">
            <p className="text-sm font-medium">Bui Huu Danh</p>
          </div>
        </div>
        <nav>
          <ul className="space-y-2">
            {/* Sản phẩm */}
            <li>
              <button
                onClick={() => setIsProductOpen(!isProductOpen)}
                className="flex items-center w-full p-2 hover:bg-gray-700 rounded"
              >
                <i className="fas fa-box-open"></i>
                <span className="ml-3">Sản phẩm  🔽</span>
                <i
                  className={`fas fa-angle-left ml-auto transition-transform duration-300 ${isProductOpen ? 'rotate-90' : ''
                    }`}
                ></i>
              </button>
              {isProductOpen && (
                <ul className="pl-6 mt-1 space-y-1">
                  <li>
                    <Link
                      to="/admin/products"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Tất cả sản phẩm</span>
                    </Link>
                  </li>
                  <li>
                    <Link
                      to="/admin/categories"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Danh mục</span>
                    </Link>
                  </li>
                  <li>
                    <Link
                      to="/admin/brands"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Thương hiệu</span>
                    </Link>
                  </li>
                </ul>
              )}
            </li>
            {/* Bài viết */}
            <li>
              <button
                onClick={() => setIsPostOpen(!isPostOpen)}
                className="flex items-center w-full p-2 hover:bg-gray-700 rounded"
              >
                <i className="fas fa-pen-alt"></i>
                <span className="ml-3">Bài viết 🔽</span>
                <i
                  className={`fas fa-angle-left ml-auto transition-transform duration-300 ${isPostOpen ? 'rotate-90' : ''
                    }`}
                ></i>
              </button>
              {isPostOpen && (
                <ul className="pl-6 mt-1 space-y-1">
                  <li>
                    <Link
                      to="/admin/post"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Tất cả bài viết</span>
                    </Link>
                  </li>
                  <li>
                    <Link
                      to="/admin/topic"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Chủ đề</span>
                    </Link>
                  </li>
                </ul>
              )}
            </li>
            {/* Giỏ hàng (no arrow) */}
            <li>
              <Link
                to="/admin/order"
                className="flex items-center p-2 hover:bg-gray-700 rounded"
              >
                <i className="fas fa-shopping-cart"></i>
                <span className="ml-3">Giỏ hàng</span>
              </Link>
            </li>
            {/* Liên hệ (no arrow) */}
            <li>
              <Link
                to="/admin/contact"
                className="flex items-center p-2 hover:bg-gray-700 rounded"
              >
                <i className="fas fa-id-card"></i>
                <span className="ml-3">Liên hệ</span>
              </Link>
            </li>
            {/* Giao diện */}
            <li>
              <button
                onClick={() => setIsAppearanceOpen(!isAppearanceOpen)}
                className="flex items-center w-full p-2 hover:bg-gray-700 rounded"
              >
                <i className="fas fa-paint-brush"></i>
                <span className="ml-3">Giao diện 🔽</span>
                <i
                  className={`fas fa-angle-left ml-auto transition-transform duration-300 ${isAppearanceOpen ? 'rotate-90' : ''
                    }`}
                ></i>
              </button>
              {isAppearanceOpen && (
                <ul className="pl-6 mt-1 space-y-1">
                  <li>
                    <Link
                      to="/admin/menu"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Menu</span>
                    </Link>
                  </li>
                  <li>
                    <Link
                      to="/admin/banners"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Banner</span>
                    </Link>
                  </li>
                </ul>
              )}
            </li>
            {/* Thành viên */}
            <li>
              <button
                onClick={() => setIsUserOpen(!isUserOpen)}
                className="flex items-center w-full p-2 hover:bg-gray-700 rounded"
              >
                <i className="fas fa-users"></i>
                <span className="ml-3">Thành viên 🔽</span>
                <i
                  className={`fas fa-angle-left ml-auto transition-transform duration-300 ${isUserOpen ? 'rotate-90' : ''
                    }`}
                ></i>
              </button>
              {isUserOpen && (
                <ul className="pl-6 mt-1 space-y-1">
                  <li>
                    <Link
                      to="/admin/user"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Tất cả thành viên</span>
                    </Link>
                  </li>
                  <li>
                    <Link
                      to="/admin/user/create"
                      className="flex items-center p-2 hover:bg-gray-700 rounded"
                    >
                      <i className="far fa-circle"></i>
                      <span className="ml-2">Thêm thành viên</span>
                    </Link>
                  </li>
                </ul>
              )}
            </li>
          </ul>
        </nav>
      </div>
    </aside>
  );
};

export default Sidebar;