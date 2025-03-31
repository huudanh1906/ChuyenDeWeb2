import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

const TopicTrash = () => {
  const [topics, setTopics] = useState([]);

  // Fetching the trashed topics
  useEffect(() => {
    axios
      .get("http://localhost:8000/admin/topic/trash")
      .then((response) => {
        setTopics(response.data);
      })
      .catch((error) => {
        console.error("There was an error fetching the topics!", error);
      });
  }, []);

  const handleRestore = (id) => {
    axios
      .get(`http://localhost:8000/admin/topic/restore/${id}`)
      .then(() => {
        setTopics(topics.filter((topic) => topic.id !== id));
      })
      .catch((error) => {
        console.error("Error restoring the topic", error);
      });
  };

  const handleDelete = (id) => {
    axios
      .delete(`http://localhost:8000/admin/topic/destroy/${id}`)
      .then(() => {
        setTopics(topics.filter((topic) => topic.id !== id));
      })
      .catch((error) => {
        console.error("Error deleting the topic", error);
      });
  };

  return (
    <div className="container mx-60 my-5">
      <div className="flex justify-end mb-4">
        <Link to="/admin/topic" className="btn btn-success flex items-center">
          <i className="fas fa-arrow-left mr-2"></i>
          Back to List
        </Link>
      </div>
      <div className="bg-white shadow-md rounded-lg p-6">
        <table className="min-w-full bg-white table-auto border border-gray-200">
          <thead>
            <tr className="bg-gray-200 text-left">
              <th className="px-4 py-2 w-12 text-center">#</th>
              <th className="px-4 py-2">Topic Name</th>
              <th className="px-4 py-2 text-center">Actions</th>
              <th className="px-4 py-2 text-center w-16">ID</th>
            </tr>
          </thead>
          <tbody>
            {topics.map((topic, index) => (
              <tr key={topic.id} className="border-t border-gray-200">
                <td className="px-4 py-2 text-center">
                  <input type="checkbox" />
                </td>
                <td className="px-4 py-2">{topic.name}</td>
                <td className="px-4 py-2 text-center">
                  <button
                    onClick={() => handleRestore(topic.id)}
                    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-1 px-3 rounded mr-2"
                  >
                    <i className="fas fa-trash-restore-alt">Restore</i>
                  </button>
                  <Link
                    to={`/admin/topic/show/${topic.id}`}
                    className="bg-green-500 hover:bg-green-700 text-white font-bold py-1 px-3 rounded mr-2"
                  >
                    <i className="fas fa-eye">View</i>
                  </Link>
                  <button
                    onClick={() => handleDelete(topic.id)}
                    className="bg-red-500 hover:bg-red-700 text-white font-bold py-1 px-3 rounded"
                  >
                    <i className="fas fa-trash">Permantly Delete</i>
                  </button>
                </td>
                <td className="px-4 py-2 text-center">{topic.id}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TopicTrash;
