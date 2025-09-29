import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Profile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    firstName: "",
    lastName: "",
    password: "",
  });
  const [saving, setSaving] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    if (!token || !userId) {
      setError("Not authenticated");
      setLoading(false);
      navigate("/login");
      return;
    }

    const fetchUser = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/api/users/${userId}/details`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) throw new Error("Failed to fetch user");
        const data = await response.json();
        setUser(data);
        setFormData({
          email: "",
          firstName: "",
          lastName: "",
          password: "",
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    navigate("/login");
  };

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSave = async () => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");
    setSaving(true);

    try {
      const response = await fetch(
        `http://localhost:8080/api/users/edit/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(formData),
        }
      );

      if (response.status === 404) throw new Error("User not found");
      if (response.status === 409) throw new Error("Email already exists");
      if (response.status === 400) throw new Error("Invalid input");

      if (!response.ok) throw new Error("Failed to update profile");

      const updatedUser = await response.json();
      setUser(updatedUser);
      setEditMode(false);
      setFormData({ email: "", firstName: "", lastName: "", password: "" });
      alert("Profile updated successfully!");
    } catch (err) {
      alert(err.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading)
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-600">Loading...</p>
      </div>
    );

  if (error)
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-red-500">Error: {error}</p>
      </div>
    );

  if (!user) return null;

  return (
    <div className="min-h-screen flex items-center justify-center bg-animated-gradient p-6">
      <div className="w-full max-w-md bg-white shadow-lg rounded-xl p-8 text-center">
        <img
          src={`https://ui-avatars.com/api/?name=${user.firstName}+${user.lastName}`}
          alt="Profile avatar"
          className="w-24 h-24 rounded-full mx-auto mb-4"
        />
        {editMode ? (
          <>
            <input
              name="firstName"
              placeholder={user.firstName}
              value={formData.firstName}
              onChange={handleChange}
              className="w-full border rounded p-2 mb-2"
            />
            <input
              name="lastName"
              placeholder={user.lastName}
              value={formData.lastName}
              onChange={handleChange}
              className="w-full border rounded p-2 mb-2"
            />
            <input
              name="email"
              placeholder={user.email}
              value={formData.email}
              onChange={handleChange}
              className="w-full border rounded p-2 mb-2"
            />
            <input
              name="password"
              type="password"
              placeholder="New Password"
              value={formData.password}
              onChange={handleChange}
              className="w-full border rounded p-2 mb-4"
            />
            <button
              onClick={handleSave}
              disabled={saving}
              className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 mr-2"
            >
              {saving ? "Saving..." : "Save"}
            </button>
            <button
              onClick={() => setEditMode(false)}
              className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
            >
              Cancel
            </button>
          </>
        ) : (
          <>
            <h2 className="text-2xl font-bold text-gray-800">
              {user.firstName} {user.lastName}
            </h2>
            <p className="text-gray-500 mb-2">{user.email}</p>
            <span className="text-sm text-blue-600 font-semibold mb-6 block">
              Role: {user.role}
            </span>
            <button
              onClick={() => setEditMode(true)}
              className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 mr-2"
            >
              Edit Profile
            </button>
            <button
              onClick={handleLogout}
              className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
            >
              Logout
            </button>
          </>
        )}
      </div>
    </div>
  );
}
