import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Menu = () => {
    const [categories, setCategories] = useState([]);
    const [brands, setBrands] = useState([]);
    const [topics, setTopics] = useState([]);
    const [pages, setPages] = useState([]);
    const [menus, setMenus] = useState([]);
    const [loading, setLoading] = useState(true); // Loading state
    const [error, setError] = useState(''); // Error state
    const [formData, setFormData] = useState({
        position: 'mainmenu',
        categoryIds: [],
        brandIds: [],
        topicIds: [],
        pageIds: [],
        name: '',
        link: '',
        status: 2,
    });

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get('http://localhost:8000/admin/menu');
                setMenus(response.data.menus);
                setCategories(response.data.categories);
                setBrands(response.data.brands);
                setTopics(response.data.topics);
                setPages(response.data.pages);
            } catch (error) {
                setError('Error fetching data');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    const handleCheckboxChange = (e) => {
        const { name, value, checked } = e.target;
        const updatedArray = checked
            ? [...formData[name], parseInt(value)]
            : formData[name].filter((id) => id !== parseInt(value));
        setFormData({ ...formData, [name]: updatedArray });
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const changeStatus = async (id) => {
        try {
            await axios.get(`http://localhost:8000/admin/menu/status/${id}`);
            const response = await axios.get('http://localhost:8000/admin/menu');
            setMenus(response.data.menus);
        } catch (error) {
            setError('Error changing status');
        }
    };

    const handleDelete = async (id) => {
        try {
            await axios.get(`http://localhost:8000/admin/menu/delete/${id}`);
            const response = await axios.get('http://localhost:8000/admin/menu');
            setMenus(response.data.menus);
        } catch (error) {
            setError('Error deleting menu');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            // Prepare the data for the request
            const updatedData = {
                createCategory: formData.categoryIds.length > 0,
                categoryid: formData.categoryIds,
                createBrand: formData.brandIds.length > 0,
                brandid: formData.brandIds,
                createTopic: formData.topicIds.length > 0,
                topicid: formData.topicIds,
                createPage: formData.pageIds.length > 0,
                pageid: formData.pageIds,
                createCustom: formData.name && formData.link,
                name: formData.name,
                link: formData.link,
                position: formData.position,
                status: formData.status,
            };
    
            console.log('Data to send:', updatedData);
    
            const response = await axios.post('http://localhost:8000/admin/menu/store', updatedData);
            
            console.log('Response from server:', response.data);
    
            // Refresh the menu list
            const newResponse = await axios.get('http://localhost:8000/admin/menu');
            setMenus(newResponse.data.menus);
    
            // Reset the form data after successful submission
            setFormData({
                position: 'mainmenu',
                categoryIds: [],
                brandIds: [],
                topicIds: [],
                pageIds: [],
                name: '',
                link: '',
                status: 2,
            });
        } catch (error) {
            console.error('Error creating menu:', error);
            setError('Error creating menu'); // Show error message
        }
    };
    

    return (
        <div className="container mx-60 p-5">
            <h1 className="text-2xl font-bold mb-5">Quản lí Menu</h1>
            <div className="flex justify-between mb-5">
                <Link to={`/admin/menu/trash`} className="bg-green-500 text-white px-4 py-2 rounded">
                    <i className="fas fa-trash mr-2"></i> Thùng rác
                </Link>
            </div>

            {/* Show error if any */}
            {error && <div className="text-red-500 mb-4">{error}</div>}

            <form onSubmit={handleSubmit} className="bg-white p-5 shadow rounded">
                <div className="mb-4">
                    <label className="block mb-2">Vị trí</label>
                    <select
                        name="position"
                        value={formData.position}
                        onChange={handleChange}
                        className="border p-2 rounded w-full"
                    >
                        <option value="mainmenu">Main Menu</option>
                        <option value="footermenu">Footer Menu</option>
                    </select>
                </div>

                {/* Accordion for Categories, Brands, Topics, Pages */}
                <Accordion title="Danh mục">
                    {categories.map((category) => (
                        <CheckboxItem
                            key={category.id}
                            id={`category${category.id}`}
                            label={category.name}
                            value={category.id}
                            name="categoryIds"
                            checked={formData.categoryIds.includes(category.id)}
                            onChange={handleCheckboxChange}
                        />
                    ))}
                </Accordion>

                <Accordion title="Thương hiệu">
                    {brands.map((brand) => (
                        <CheckboxItem
                            key={brand.id}
                            id={`brand${brand.id}`}
                            label={brand.name}
                            value={brand.id}
                            name="brandIds"
                            checked={formData.brandIds.includes(brand.id)}
                            onChange={handleCheckboxChange}
                        />
                    ))}
                </Accordion>

                <Accordion title="Chủ đề">
                    {topics.map((topic) => (
                        <CheckboxItem
                            key={topic.id}
                            id={`topic${topic.id}`}
                            label={topic.name}
                            value={topic.id}
                            name="topicIds"
                            checked={formData.topicIds.includes(topic.id)}
                            onChange={handleCheckboxChange}
                        />
                    ))}
                </Accordion>

                <Accordion title="Trang đơn">
                    {pages.map((page) => (
                        <CheckboxItem
                            key={page.id}
                            id={`page${page.id}`}
                            label={page.title}
                            value={page.id}
                            name="pageIds"
                            checked={formData.pageIds.includes(page.id)}
                            onChange={handleCheckboxChange}
                        />
                    ))}
                </Accordion>

                {/* Menu Name and Link Inputs */}
                <div className="mb-4">
                    <label className="block mb-2">Tên menu</label>
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        className="border p-2 rounded w-full"
                    />
                </div>
                <div className="mb-4">
                    <label className="block mb-2">Liên kết</label>
                    <input
                        type="text"
                        name="link"
                        value={formData.link}
                        onChange={handleChange}
                        className="border p-2 rounded w-full"
                    />
                </div>

                <div className="mb-4">
                    <label className="block mb-2">Trạng thái</label>
                    <select
                        name="status"
                        value={formData.status}
                        onChange={handleChange}
                        className="border p-2 rounded w-full"
                    >
                        <option value="2">Chưa xuất bản</option>
                        <option value="1">Xuất bản</option>
                    </select>
                </div>

                <button type="submit" className="bg-green-500 text-white px-4 py-2 rounded">
                    Thêm menu
                </button>
            </form>

            {/* Menu Table */}
            <div className="mt-5">
                {loading ? (
                    <div>Loading...</div>
                ) : (
                    <table className="min-w-full bg-white border shadow">
                        <thead>
                            <tr>
                                <th className="border px-4 py-2">#</th>
                                <th className="border px-4 py-2">Tên Menu</th>
                                <th className="border px-4 py-2">Kiểu Menu</th>
                                <th className="border px-4 py-2">Vị trí</th>
                                <th className="border px-4 py-2">Hoạt động</th>
                                <th className="border px-4 py-2">ID</th>
                            </tr>
                        </thead>
                        <tbody>
                            {menus.map((menu) => (
                                <tr key={menu.id}>
                                    <td className="border px-4 py-2"><input type="checkbox" /></td>
                                    <td className="border px-4 py-2">{menu.name}</td>
                                    <td className="border px-4 py-2">{menu.menuType}</td>
                                    <td className="border px-4 py-2">{menu.position}</td>
                                    <td className="border px-4 py-2">
                                    <button
                                        onClick={() => changeStatus(menu.id, menu.status)}
                                        className={menu.status === 1 ? "bg-green-500 text-white px-2 py-1 rounded mr-2" : "bg-red-500 text-white px-2 py-1 rounded mr-2"}>
                                        {menu.status === 1 ? 'Active' : 'Inactive'}
                                    </button>
                                        <Link to={`/admin/menu/show/${menu.id}`} className="bg-blue-500 text-white px-2 py-1 rounded mr-2">
                                            <i className="fas fa-edit">View</i>
                                        </Link>
                                        <button onClick={() => handleDelete(menu.id)} className="bg-red-500 text-white px-2 py-1 rounded">
                                            <i className="fas fa-trash">Delete</i>
                                        </button>
                                    </td>
                                    <td className="border px-4 py-2">{menu.id}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

// Accordion Component
const Accordion = ({ title, children }) => {
    const [isOpen, setIsOpen] = useState(false);
    return (
        <div className="mb-4">
            <button
                type="button"
                className="bg-gray-200 p-2 w-full text-left focus:outline-none"
                onClick={() => setIsOpen(!isOpen)}
            >
                {title} <i className={`fas ${isOpen ? 'fa-chevron-up' : 'fa-chevron-down'}`}></i>
            </button>
            {isOpen && <div className="p-2 bg-white">{children}</div>}
        </div>
    );
};

// CheckboxItem Component
const CheckboxItem = ({ id, label, value, name, checked, onChange }) => (
    <div className="mb-2">
        <input
            type="checkbox"
            id={id}
            value={value}
            name={name}
            checked={checked}
            onChange={onChange}
            className="mr-2"
        />
        <label htmlFor={id}>{label}</label>
    </div>
);

export default Menu;
