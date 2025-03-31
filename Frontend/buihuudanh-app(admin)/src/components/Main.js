// Main.js
import React from 'react';
import { Routes, Route } from 'react-router-dom';

import Products from '../pages/product/Products';
import ProductTrash from '../pages/product/ProductTrash';
import ProductDetail from '../pages/product/ProductDetail';
import EditProduct from '../pages/product/EditProduct';
import Categories from '../pages/category/Categories';
import Brands from '../pages/brand/Brand';
import CreateProduct from '../pages/product/CreateProduct';
import CategoryTrash from '../pages/category/CategoryTrash';
import CategoryDetail from '../pages/category/CategoryDetail';
import EditCategory from '../pages/category/EditCategory';
import BrandTrash from '../pages/brand/BrandTrash';
import BrandDetail from '../pages/brand/BrandDetail';
import EditBrand from '../pages/brand/EditBrand';
import Banner from '../pages/banner/Banner';
import BannerTrash from '../pages/banner/BannerTrash';
import BannerDetail from '../pages/banner/BannerDetail';
import EditBanner from '../pages/banner/EditBanner';
import Menu from '../pages/menu/Menu';
import MenuDetail from '../pages/menu/MenuDetail';
import MenuTrash from '../pages/menu/MenuTrash';
import Contact from '../pages/contact/Contact';
import ContactTrash from '../pages/contact/ContactTrash';
import ContactDetail from '../pages/contact/ContactDetail';
import Post from '../pages/post/Post';
import PostDetail from '../pages/post/PostDetail';
import PostTrash from '../pages/post/PostTrash';
import EditPost from '../pages/post/EditPost';
import CreatePost from '../pages/post/CreatePost';
import Topic from '../pages/topic/Topic';
import TopicDetail from '../pages/topic/TopicDetail';
import EditTopic from '../pages/topic/EditTopic';
import TopicTrash from '../pages/topic/TopicTrash';
import User from '../pages/user/User';
import UserDetail from '../pages/user/UserDetail';
import EditUser from '../pages/user/EditUser';
import CreateUser from '../pages/user/CreateUser';
import UserTrash from '../pages/user/UserTrash';
import Order from '../pages/order/Order';
import OrderDetail from '../pages/order/OrderDetail';
import OrderTrash from '../pages/order/OrderTrash';
import CreateCategory from '../pages/category/CreateCategory';
import ForgotPassword from './ForgotPassword';


const Main = () => {
  return (
    <Routes>
      <Route path="/admin/forgot-password" element={<ForgotPassword />} />
      <Route path="/admin/products" element={<Products />} />
      <Route path="/admin/products" element={<Products />} />
      <Route path="/admin/products/trash" element={<ProductTrash />} />
      <Route path="/admin/products/show/:id" element={<ProductDetail />} />
      <Route path="/admin/products/edit/:id" element={<EditProduct />} />
      <Route path="/admin/products/create" element={<CreateProduct />} />

      <Route path="/admin/categories" element={<Categories />} />
      <Route path="/admin/categories/show/:id" element={<CategoryDetail />} />
      <Route path="/admin/categories/edit/:id" element={<EditCategory />} />
      <Route path="/admin/categories/trash" element={<CategoryTrash />} />
      <Route path="/admin/categories/create" element={<CreateCategory />} />

      <Route path="/admin/brands" element={<Brands />} />
      <Route path="/admin/brands/show/:id" element={<BrandDetail />} />
      <Route path="/admin/brands/edit/:id" element={<EditBrand />} />
      <Route path="/admin/brands/trash" element={<BrandTrash />} />

      <Route path="/admin/banners" element={<Banner />} />
      <Route path="/admin/banners/trash" element={<BannerTrash />} />
      <Route path="/admin/banners/show/:id" element={<BannerDetail />} />
      <Route path="/admin/banners/edit/:id" element={<EditBanner />} />

      <Route path="/admin/menu" element={<Menu />} />
      <Route path="/admin/menu/show/:id" element={<MenuDetail />} />
      <Route path="/admin/menu/trash" element={<MenuTrash />} />

      <Route path="/admin/contact" element={<Contact />} />
      <Route path="/admin/contact/show/:id" element={<ContactDetail />} />
      <Route path="/admin/contact/trash" element={<ContactTrash />} />

      <Route path="/admin/post" element={<Post />} />
      <Route path="/admin/post/show/:id" element={<PostDetail />} />
      <Route path="/admin/post/edit/:id" element={<EditPost />} />
      <Route path="/admin/post/trash" element={<PostTrash />} />
      <Route path="/admin/post/create" element={<CreatePost />} />

      <Route path="/admin/topic" element={<Topic />} />
      <Route path="/admin/topic/show/:id" element={<TopicDetail />} />
      <Route path="/admin/topic/edit/:id" element={<EditTopic />} />
      <Route path="/admin/topic/trash" element={<TopicTrash />} />

      <Route path="/admin/user" element={<User />} />
      <Route path="/admin/user/show/:id" element={<UserDetail />} />
      <Route path="/admin/user/edit/:id" element={<EditUser />} />
      <Route path="/admin/user/create" element={<CreateUser />} />
      <Route path="/admin/user/trash" element={<UserTrash />} />

      <Route path="/admin/order" element={<Order />} />
      <Route path="/admin/order/show/:id" element={<OrderDetail />} />
      <Route path="/admin/order/trash" element={<OrderTrash />} />
    </Routes>
  );
};

export default Main;
