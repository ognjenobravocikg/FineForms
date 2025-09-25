import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Profile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) throw new Error("Failed to fetch user");
        const data = await response.json();
        setUser(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

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

  if (!user) return null;

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-6">
      <div className="w-full max-w-md bg-white shadow-lg rounded-xl p-8 text-center">
        <img
          src={`https://ui-avatars.com/api/?name=${user.firstName}+${user.lastName}`}
          alt="Profile avatar"
          className="w-24 h-24 rounded-full mx-auto mb-4"
        />
        <h2 className="text-2xl font-bold text-gray-800">
          {user.firstName} {user.lastName}
        </h2>
        <p className="text-gray-500 mb-2">{user.email}</p>
        <span className="text-sm text-blue-600 font-semibold mb-6 block">
          Role: {user.role}
        </span>

        <button
          onClick={handleLogout}
          className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
        >
          Logout
        </button>
      </div>
    </div>
  );
}
