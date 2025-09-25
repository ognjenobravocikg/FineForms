import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

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
        // 1. Fetch logged-in user
        const userRes = await fetch(`${API_BASE}/users/me`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!userRes.ok) {
          const txt = await userRes.text();
          throw new Error(`Failed to fetch user: ${userRes.status} ${txt}`);
        }

        const userData = await userRes.json();
        setUser(userData);

        // 2. Fetch all users (for collaborators)
        const usersRes = await fetch(`${API_BASE}/users`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!usersRes.ok) {
          const txt = await usersRes.text();
          throw new Error(`Failed to fetch users: ${usersRes.status} ${txt}`);
        }

        const usersData = await usersRes.json();
        setAllUsers(usersData);

        // 3. Fetch all forms
        const formsRes = await fetch(`${API_BASE}/form`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!formsRes.ok) {
          const txt = await formsRes.text();
          throw new Error(`Failed to fetch forms: ${formsRes.status} ${txt}`);
        }

        const formsData = await formsRes.json();
        setForms(formsData);
      } catch (err) {
        console.error(err);
        setError(err.message || "Unknown error");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [navigate]);

  // Filter users: exclude current user + search
  const filteredUsers = allUsers.filter((u) => {
    if (u.id === user?.id) return false; // ← Prevent self-add
    const matchesSearch =
      u.firstName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.lastName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.email.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesSearch;
  });

  // Open modal and load collaborators
  const openCollaboratorsModal = async (form) => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    setSelectedForm(form);
    setShowModal(true);

    try {
      const res = await fetch(`${API_BASE}/form/${form.id}/collab`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) {
        const txt = await res.text();
        console.warn("Could not fetch collaborators:", res.status, txt);
        setSelectedForm((s) => ({ ...s, collaborators: [] }));
        return;
      }

      const collabs = await res.json();
      setSelectedForm((s) => ({ ...s, collaborators: collabs }));
    } catch (err) {
      console.error("Error fetching collaborators:", err);
      setSelectedForm((s) => ({ ...s, collaborators: [] }));
    }
  };

  // Add collaborator
  const handleAddCollaborator = async (userIdToAdd) => {
    if (!selectedForm) return alert("No form selected");
    const token = localStorage.getItem("token");
    try {
      const res = await fetch(`${API_BASE}/form/${selectedForm.id}/collab`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ userId: userIdToAdd }),
      });

      if (!res.ok) {
        const bodyText = await res.text();
        console.log("Add collaborator failed. Raw response:", bodyText); // 🔍 DEBUG

        let msg;
        try {
          const json = JSON.parse(bodyText);
          msg = json.message || bodyText;
        } catch {
          msg = bodyText || "Server returned an invalid response";
        }
        throw new Error(`Failed to add collaborator: ${res.status} ${msg}`);
      }

      // Re-fetch collaborators
      const updated = await fetch(
        `${API_BASE}/form/${selectedForm.id}/collab`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!updated.ok) {
        const txt = await updated.text();
        throw new Error(
          `Added but failed to reload collaborators: ${updated.status} ${txt}`
        );
      }

      const collaborators = await updated.json();
      setSelectedForm((s) => ({ ...s, collaborators }));
    } catch (err) {
      console.error(err);
      alert(err.message || "Error adding collaborator");
    }
  };

  // Remove collaborator
  const handleRemoveCollaborator = async (userIdToRemove) => {
    if (!selectedForm) return;
    const token = localStorage.getItem("token");
    try {
      const res = await fetch(
        `${API_BASE}/form/${selectedForm.id}/collab/${userIdToRemove}`,
        {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!res.ok) {
        const bodyText = await res.text();
        let msg;
        try {
          const json = JSON.parse(bodyText);
          msg = json.message || bodyText;
        } catch {
          msg = bodyText;
        }
        throw new Error(`Failed to remove collaborator: ${res.status} ${msg}`);
      }

      setSelectedForm((s) => ({
        ...s,
        collaborators: s.collaborators
          ? s.collaborators.filter((c) => c.id !== userIdToRemove)
          : [],
      }));
    } catch (err) {
      console.error(err);
      alert(err.message || "Error removing collaborator");
    }
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

  // Determine if user owns a form (adjust field name if needed: createdBy, ownerId, etc.)
  const isFormOwner = (form) => {
    return (
      form.ownerId === user?.id ||
      form.createdBy === user?.id ||
      form.userId === user?.id
    );
  };

  return (
    <div className="min-h-screen flex flex-col items-center bg-gray-100 p-6">
      <div className="w-full max-w-4xl bg-white shadow-lg rounded-xl p-8 mb-8">
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

        <div className="space-y-4">
          {forms.length > 0 ? (
            forms.map((form) => (
              <div
                key={form.id}
                className="bg-gray-50 border rounded-lg p-6 shadow-sm hover:shadow-md transition"
              >
                <h3 className="text-xl font-semibold text-gray-800">
                  {form.title}
                </h3>
                <p className="text-gray-600">{form.description}</p>
                <div className="flex gap-3 mt-3">
                  <button
                    onClick={() => navigate(`/form/${form.id}`)}
                    className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition"
                  >
                    Open Form
                  </button>

                  {/* Only show "Add Collaborators" if user owns the form */}
                  {isFormOwner(form) && (
                    <button
                      onClick={() => openCollaboratorsModal(form)}
                      className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
                    >
                      Add Collaborators
                    </button>
                  )}
                </div>
              </div>
            ))
          ) : (
            <p className="text-gray-500 text-center">No forms available</p>
          )}
        </div>
      </div>

      {/* Collaborators Modal */}
      {showModal && selectedForm && (
        <div className="fixed inset-0 backdrop-blur-sm bg-black/30 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-lg w-full max-w-lg p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-2xl font-bold text-gray-800">
                Collaborators for: {selectedForm.title}
              </h2>
              <button
                onClick={() => {
                  setShowModal(false);
                  setSelectedForm(null);
                }}
                className="text-sm text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>

            <div className="mb-4">
              <h3 className="text-lg font-semibold text-gray-700 mb-2">
                Current Collaborators
              </h3>
              <div className="space-y-2">
                {selectedForm.collaborators?.length > 0 ? (
                  selectedForm.collaborators.map((c) => (
                    <div
                      key={c.id}
                      className="flex justify-between items-center border p-2 rounded-lg bg-gray-50"
                    >
                      <div>
                        <p className="font-semibold">
                          {c.firstName} {c.lastName}
                        </p>
                        <p className="text-sm text-gray-500">{c.email}</p>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className="text-sm text-gray-600">
                          {c.role || "USER"}
                        </span>
                        <button
                          onClick={() => handleRemoveCollaborator(c.id)}
                          className="px-3 py-1 text-sm bg-red-500 text-white rounded-lg hover:bg-red-600"
                        >
                          Remove
                        </button>
                      </div>
                    </div>
                  ))
                ) : (
                  <p className="text-gray-500 text-sm italic">
                    No collaborators yet.
                  </p>
                )}
              </div>
            </div>

            <input
              type="text"
              placeholder="Search users by name or email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full border px-4 py-2 rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />

            <div className="max-h-60 overflow-y-auto space-y-2">
              {filteredUsers.length > 0 ? (
                filteredUsers.map((u) => {
                  const already = selectedForm.collaborators?.some(
                    (c) => c.id === u.id
                  );
                  return (
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
                      <button
                        onClick={() => handleAddCollaborator(u.id)}
                        disabled={already}
                        className={`px-4 py-2 rounded-lg text-sm transition ${
                          already
                            ? "bg-gray-200 text-gray-600 cursor-not-allowed"
                            : "border border-gray-300 text-gray-700 hover:bg-gray-100"
                        }`}
                      >
                        {already ? "Added" : "Add"}
                      </button>
                    </div>
                  );
                })
              ) : (
                <p className="text-gray-500 text-center">
                  {searchQuery
                    ? "No users match your search"
                    : "No other users available"}
                </p>
              )}
            </div>

            <div className="mt-6 flex justify-end gap-3">
              <button
                onClick={() => {
                  setShowModal(false);
                  setSelectedForm(null);
                }}
                className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
