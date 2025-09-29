import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";

export default function AdminPage() {
  const [users, setUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    const fetchUsers = async () => {
      try {
        const res = await fetch("/api/users/", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (!res.ok) {
          throw new Error("Failed to fetch users");
        }

        const data = await res.json();
        setUsers(data);
        setFilteredUsers(data);
      } catch (err) {
        console.error("Error fetching users:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [navigate]);

  // Handle search
  useEffect(() => {
    if (search.trim() === "") {
      setFilteredUsers(users);
    } else {
      const lower = search.toLowerCase();
      setFilteredUsers(
        users.filter(
          (u) =>
            u.firstName.toLowerCase().includes(lower) ||
            u.lastName.toLowerCase().includes(lower) ||
            u.email.toLowerCase().includes(lower)
        )
      );
    }
  }, [search, users]);

  if (loading) return <p>Loading...</p>;

  return (
    <div className="max-w-5xl mx-auto p-8 bg-white shadow-lg rounded-lg">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">Admin Dashboard</h1>
      <p className="text-gray-600 mb-6">Manage users and assign admin roles.</p>

      {/*  Search Box */}
      <div className="mb-6">
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by name or email..."
          className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      <table className="w-full border-collapse border border-gray-300">
        <thead className="bg-gray-100">
          <tr>
            <th className="border border-gray-300 p-3 text-left">Name</th>
            <th className="border border-gray-300 p-3 text-left">Email</th>
            <th className="border border-gray-300 p-3 text-left">Role</th>
            <th className="border border-gray-300 p-3 text-center">Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredUsers.length > 0 ? (
            filteredUsers.map((u) => (
              <tr key={u.id} className="hover:bg-gray-50">
                <td className="border border-gray-300 p-3">
                  <Link
                    to={`/admin/users/${u.id}`}
                    className="text-indigo-600 hover:underline"
                  >
                    {u.firstName} {u.lastName}
                  </Link>
                </td>
                <td className="border border-gray-300 p-3">{u.email}</td>
                <td className="border border-gray-300 p-3 font-semibold">
                  {u.role}
                </td>
                <td className="border border-gray-300 p-3 text-center">
                  {u.role === "USER" ? (
                    <button
                      onClick={() => alert(`Promote ${u.email} to ADMIN`)}
                      className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
                    >
                      Make Admin
                    </button>
                  ) : (
                    <span className="text-green-600 font-semibold">
                      Already Admin
                    </span>
                  )}
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="4" className="text-center text-gray-500 py-6 italic">
                No users found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
