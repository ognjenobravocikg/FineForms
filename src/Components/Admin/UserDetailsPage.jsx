import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

export default function UserDetailsPage() {
  const { id } = useParams(); // user id from URL
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [forms, setForms] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    //code for button edit form card, waiting for backend linkage
    /*
    <button
      onClick={() => navigate(`/forms/${form.id}/edit`)}
      className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
    >
      Edit
    </button>;
    */

    // Fetch user info
    const fetchUser = async () => {
      try {
        const res = await fetch(`/api/users/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error("Failed to fetch user");

        const data = await res.json();
        setUser(data);

        // Dummy forms until backend endpoint is ready
        setForms([
          {
            id: 101,
            title: "Survey on UX",
            description: "Short feedback form",
          },
          {
            id: 102,
            title: "Registration Form",
            description: "Collecting user data",
          },
          {
            id: 103,
            title: "Bug Report",
            description: "Track reported issues",
          },
        ]);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [id, navigate]);

  if (loading) return <p>Loading...</p>;
  if (!user) return <p>User not found.</p>;

  return (
    <div className="max-w-6xl mx-auto p-8">
      {/* User Info */}
      <div className="bg-white shadow-lg rounded-lg p-6 mb-10">
        <h1 className="text-2xl font-bold text-gray-800 mb-2">
          {user.firstName} {user.lastName}
        </h1>
        <p className="text-gray-600 mb-1">
          <strong>Email:</strong> {user.email}
        </p>
        <p className="text-gray-600">
          <strong>Role:</strong> {user.role}
        </p>
      </div>

      {/* User Forms */}
      <h2 className="text-xl font-bold text-gray-800 mb-4">User Forms</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {forms.map((form) => (
          <div
            key={form.id}
            className="bg-white shadow-md rounded-lg p-6 flex flex-col justify-between"
          >
            <div>
              <h3 className="text-lg font-semibold text-gray-800">
                {form.title}
              </h3>
              <p className="text-gray-600 mb-4">{form.description}</p>
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => alert(`Edit form ${form.id}`)}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
              >
                Edit
              </button>
              <button
                onClick={() => alert(`Delete form ${form.id}`)}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
