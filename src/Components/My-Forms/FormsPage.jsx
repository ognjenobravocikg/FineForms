import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function FormsPage() {
  const [user, setUser] = useState(null);
  const [forms, setForms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [showModal, setShowModal] = useState(false);
  const [selectedForm, setSelectedForm] = useState(null);
  const [allUsers, setAllUsers] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        setError("Not authenticated");
        setLoading(false);
        navigate("/login");
        return;
      }

      try {
        const userRes = await fetch(
          "http://localhost:8080/api/users/2/details/",
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!userRes.ok) throw new Error("Failed to fetch user");
        const userData = await userRes.json();
        setUser(userData);

        // Fetch all users (for collaborators)
        const usersRes = await fetch("http://localhost:8080/api/users/", {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        if (!usersRes.ok) throw new Error("Failed to fetch users");
        const usersData = await usersRes.json();
        setAllUsers(usersData);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const filteredUsers = allUsers.filter(
    (u) =>
      u.firstName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.lastName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.email.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-600">Loading...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-red-500">Error: {error}</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col items-center bg-gray-100 p-6">
      <div className="w-full max-w-4xl bg-white shadow-lg rounded-xl p-8 mb-8">
        {/* User Info */}
        {user && (
          <div className="text-center mb-6">
            <h2 className="text-2xl font-bold text-gray-800">
              {user.firstName} {user.lastName}
            </h2>
            <p className="text-gray-500">{user.email}</p>
            <span className="text-sm text-blue-600 font-semibold">
              Role: {user.role}
            </span>
          </div>
        )}

        <hr className="my-6" />

        {/* Forms List */}
        <div className="space-y-4">
          {forms.map((form) => (
            <div
              key={form.id}
              className="bg-gray-50 border rounded-lg p-6 shadow-sm hover:shadow-md transition"
            >
              <h3 className="text-xl font-semibold text-gray-800">
                {form.title}
              </h3>
              <p className="text-gray-600">{form.description}</p>
              <div className="flex gap-3 mt-3">
                <button className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition">
                  Open Form
                </button>
                <button
                  onClick={() => {
                    setSelectedForm(form);
                    setShowModal(true);
                  }}
                  className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
                >
                  Add Collaborators
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Collaborators Modal */}
      {showModal && (
        <div className="fixed inset-0 backdrop-blur-sm bg-black/30 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-lg w-full max-w-lg p-6">
            <h2 className="text-2xl font-bold text-gray-800 mb-4">
              Add Collaborators to {selectedForm?.title}
            </h2>
            <input
              type="text"
              placeholder="Search users by name or email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full border px-4 py-2 rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <div className="max-h-60 overflow-y-auto space-y-2">
              {filteredUsers.map((u) => (
                <div
                  key={u.id}
                  className="flex justify-between items-center border p-3 rounded-lg hover:bg-gray-50"
                >
                  <div>
                    <p className="font-semibold text-gray-800">
                      {u.firstName} {u.lastName}
                    </p>
                    <p className="text-sm text-gray-500">{u.email}</p>
                  </div>
                  <button className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition">
                    Add
                  </button>
                </div>
              ))}
              {filteredUsers.length === 0 && (
                <p className="text-gray-500 text-center">No users found</p>
              )}
            </div>
            <div className="mt-6 flex justify-end gap-3">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition"
              >
                Close
              </button>
              <button className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition">
                Save Changes
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
