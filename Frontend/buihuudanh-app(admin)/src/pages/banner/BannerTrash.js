import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

const BannerTrash = () => {
  const [banners, setBanners] = useState([]);

  useEffect(() => {
    // Fetch trashed banners
    axios
      .get('http://localhost:8000/admin/banner/trash')
      .then((response) => {
        setBanners(response.data);
      })
      .catch((error) => {
        console.error('There was an error fetching the banners!', error);
      });
  }, []);

  const handleRestore = (id) => {
    axios
      .get(`http://localhost:8000/admin/banner/restore/${id}`)
      .then((response) => {
        setBanners(banners.filter((banner) => banner.id !== id)); // Remove restored banner from the list
      })
      .catch((error) => {
        console.error('Error restoring banner:', error);
      });
  };

  const handleDelete = (id) => {
    axios
      .get(`http://localhost:8000/admin/banner/destroy/${id}`)
      .then(() => {
        setBanners(banners.filter((banner) => banner.id !== id)); // Remove deleted banner from the list
      })
      .catch((error) => {
        console.error('Error deleting banner:', error);
      });
  };

  return (
    <div className="container mx-60 px-8">
      <div className="flex justify-between mb-4">
        <h1 className="text-xl font-bold">Deleted Banners</h1>
        <Link to="/admin/banners" className="btn bg-green-500 text-white py-2 px-4 rounded">
          <i className="fas fa-arrow-left mr-2"></i>Back to Banner List
        </Link>
      </div>
      <div className="bg-white shadow-md rounded">
        <table className="min-w-full table-auto">
          <thead className="bg-gray-200 text-gray-600 uppercase text-sm leading-normal">
            <tr>
              <th className="py-3 px-6 text-left">#</th>
              <th className="py-3 px-6 text-left">Image</th>
              <th className="py-3 px-6 text-left">Banner Name</th>
              <th className="py-3 px-6 text-center">Actions</th>
              <th className="py-3 px-6 text-center">ID</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm">
            {banners.map((banner, index) => (
              <tr key={banner.id} className="border-b border-gray-200 hover:bg-gray-100">
                <td className="py-3 px-6 text-left">
                  <input type="checkbox" />
                </td>
                <td className="py-3 px-6">
                  <img
                    src={`http://localhost:8000/imgs/banners/${banner.image}`}
                    alt={banner.name}
                    className="w-12"
                  />
                </td>
                <td className="py-3 px-6">{banner.name}</td>
                <td className="py-3 px-6 text-center">
                  <button
                    onClick={() => handleRestore(banner.id)}
                    className="bg-blue-500 text-white py-1 px-3 rounded mx-1"
                  >
                    <i className="fas fa-trash-restore-alt">Restore</i>
                  </button>
                  <Link
                    to={`/admin/banners/show/${banner.id}`}
                    className="bg-green-500 text-white py-1 px-3 rounded mx-1"
                  >
                    <i className="fas fa-eye">View</i>
                  </Link>
                  <button
                    onClick={() => handleDelete(banner.id)}
                    className="bg-red-500 text-white py-1 px-3 rounded mx-1"
                  >
                    <i className="fas fa-trash">Permantly Delete</i>
                  </button>
                </td>
                <td className="py-3 px-6 text-center">{banner.id}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default BannerTrash;
