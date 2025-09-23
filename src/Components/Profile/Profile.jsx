import { useEffect, useState } from "react";

export default function Profile() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Replace with your backend endpoint
    fetch("http://localhost:8080/api/users/me")
      .then((res) => res.json())
      .then((data) => setUser(data))
      .catch((err) => console.error("Failed to fetch user:", err));
  }, []);

  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <p className="text-gray-600 text-lg">Loading profile...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="w-full max-w-3xl bg-white shadow-lg rounded-xl p-8">
        {/* Header */}
        <div className="flex items-center space-x-6 mb-6">
          <img
            src={user.avatarUrl || "/default-avatar.png"}
            alt="User Avatar"
            className="w-24 h-24 rounded-full border-2 border-gray-300 object-cover"
          />
          <div>
            <h2 className="text-2xl font-bold text-gray-800">
              {user.firstName} {user.lastName}
            </h2>
            <p className="text-gray-500">{user.email}</p>
          </div>
        </div>

        <hr className="my-6" />

        {/* Info Section */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h3 className="text-lg font-semibold text-gray-700 mb-2">
              Personal Info
            </h3>
            <p>
              <span className="font-medium">First Name:</span> {user.firstName}
            </p>
            <p>
              <span className="font-medium">Last Name:</span> {user.lastName}
            </p>
            <p>
              <span className="font-medium">Role:</span> {user.role || "User"}
            </p>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-gray-700 mb-2">
              Account
            </h3>
            <p>
              <span className="font-medium">Email:</span> {user.email}
            </p>
            <p>
              <span className="font-medium">Member since:</span>{" "}
              {new Date(user.createdAt).toLocaleDateString()}
            </p>
          </div>
        </div>

        <hr className="my-6" />

        {/* Actions */}
        <div className="flex space-x-4">
          <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">
            Edit Profile
          </button>
          <button className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
            Log Out
          </button>
        </div>
      </div>
    </div>
  );
}
