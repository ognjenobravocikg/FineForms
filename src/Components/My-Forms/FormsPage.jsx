// FormsPage.jsx
import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import FormCard from "./FormCard";
import CollaboratorsModal from "./CollaboratorsModal";

const API_BASE = "http://localhost:8080/api";

function parseUserFromStorage() {
  const rawId = localStorage.getItem("userId");
  if (rawId) return String(rawId);
  const stored = localStorage.getItem("user");
  if (stored) {
    try {
      const parsed = JSON.parse(stored);
      return parsed?.id ? String(parsed.id) : null;
    } catch {
      return null;
    }
  }
  return null;
}

export default function FormsPage() {
  const [user, setUser] = useState(null);
  const [forms, setForms] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [showModal, setShowModal] = useState(false);
  const [selectedForm, setSelectedForm] = useState(null);

  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const userId = parseUserFromStorage();

  // Helper to fetch collaborators for each form
  const fetchFormCollaborators = async (formId) => {
    try {
      const res = await fetch(`${API_BASE}/form/${formId}/collab`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) return [];
      return await res.json();
    } catch {
      return [];
    }
  };

  const fetchForms = useCallback(async () => {
    if (!token) {
      setError("Not authenticated (no token)");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // 1️⃣ Fetch all forms (backend should ideally provide all forms visible to this user)
      const res = await fetch(`${API_BASE}/form`, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      if (!res.ok) throw new Error(`Failed to fetch forms (${res.status})`);
      const allForms = await res.json();

      // 2️⃣ Fetch collaborator info for each form
      const formsWithRole = await Promise.all(
        allForms.map(async (form) => {
          const collabs = await fetchFormCollaborators(form.id);

          // Determine user's role in this form
          let userRole = "NONE";
          if (String(form.ownerId) === String(userId)) {
            userRole = "OWNER";
          } else {
            const c = collabs.find((c) => String(c.userId) === String(userId));
            if (c) userRole = c.role; // EDITOR or VIEWER
          }

          return { ...form, userRole, collaborators: collabs };
        })
      );

      // 3️⃣ Keep only forms where user is owner or collaborator
      const visibleForms = formsWithRole.filter(
        (f) =>
          f.userRole === "OWNER" ||
          f.userRole === "EDITOR" ||
          f.userRole === "VIEWER"
      );

      setForms(visibleForms);
    } catch (err) {
      console.error("fetchForms error:", err);
      setError(err.message || "Failed to load forms");
    } finally {
      setLoading(false);
    }
  }, [token, userId]);

  useEffect(() => {
    // initial load: user details, all users, forms
    const load = async () => {
      if (!token) {
        setError("Not authenticated");
        setLoading(false);
        navigate("/login");
        return;
      }

      setLoading(true);
      setError(null);

      try {
        // 1) fetch user details
        if (userId) {
          const userRes = await fetch(`${API_BASE}/users/${userId}/details`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          if (userRes.ok) {
            setUser(await userRes.json());
          } else {
            const stored = localStorage.getItem("user");
            if (stored) setUser(JSON.parse(stored));
          }
        }

        // 2) fetch all users (for collaborator search)
        const usersRes = await fetch(`${API_BASE}/users`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setAllUsers(usersRes.ok ? await usersRes.json() : []);

        // 3) fetch forms
        await fetchForms();
      } catch (err) {
        console.error("initial load error:", err);
        setError(err.message || "Initialization failed");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [token, userId, navigate, fetchForms]);

  const handleDeleteForm = async (formId) => {
    if (!confirm("Delete this form? This cannot be undone.")) return;
    if (!token) return setError("Not authenticated");

    try {
      const res = await fetch(`${API_BASE}/form/${formId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error(`Delete failed (${res.status})`);
      setForms((prev) => prev.filter((f) => String(f.id) !== String(formId)));
    } catch (err) {
      console.error("delete error:", err);
      alert("Could not delete form: " + (err.message || "unknown"));
    }
  };

  const openCollaboratorsModal = (form) => {
    if (!token) {
      navigate("/login");
      return;
    }
    setSelectedForm(form);
    setShowModal(true);
  };

  const handleAddCollaborator = async (userIdToAdd) => {
    if (!selectedForm || !token) return;

    try {
      const res = await fetch(`${API_BASE}/form/${selectedForm.id}/collab`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ userId: userIdToAdd }),
      });
      if (!res.ok) throw new Error("Add collaborator failed");

      // refresh collaborators & forms
      const collabs = await fetchFormCollaborators(selectedForm.id);
      setSelectedForm((s) => ({ ...s, collaborators: collabs }));
      await fetchForms();
    } catch (err) {
      console.error("add collaborator error:", err);
      alert("Failed to add collaborator: " + (err.message || "unknown"));
    }
  };

  const handleRemoveCollaborator = async (userIdToRemove) => {
    if (!selectedForm || !token) return;

    try {
      const res = await fetch(
        `${API_BASE}/form/${selectedForm.id}/collab/${userIdToRemove}`,
        { method: "DELETE", headers: { Authorization: `Bearer ${token}` } }
      );
      if (!res.ok) throw new Error("Remove collaborator failed");

      const collabs = selectedForm.collaborators.filter(
        (c) => String(c.userId) !== String(userIdToRemove)
      );
      setSelectedForm((s) => ({ ...s, collaborators: collabs }));
      await fetchForms();
    } catch (err) {
      console.error("remove collaborator error:", err);
      alert("Failed to remove collaborator: " + (err.message || "unknown"));
    }
  };

  if (loading) return <div className="p-6 text-gray-600">Loading...</div>;
  if (error) return <div className="p-6 text-red-600">Error: {error}</div>;

  return (
    <div className="min-h-screen flex flex-col items-center bg-gray-50 p-6">
      <div className="w-full max-w-4xl bg-white shadow-lg rounded-xl p-8 mb-8">
        {user && (
          <div className="text-center mb-6">
            <h2 className="text-2xl font-bold text-gray-800">
              {user.firstName} {user.lastName}
            </h2>
            <p className="text-gray-500">{user.email}</p>
          </div>
        )}

        <hr className="my-6" />

        {/* Forms list */}
        <div className="space-y-4">
          {forms.length > 0 ? (
            forms.map((form) => (
              <FormCard
                key={form.id}
                form={form}
                userRole={form.userRole}
                onEdit={() => navigate(`/form/${form.id}`)}
                onCollaborators={() => openCollaboratorsModal(form)}
                onDelete={() => handleDeleteForm(form.id)}
              />
            ))
          ) : (
            <p className="text-gray-500 text-center">No forms available</p>
          )}
        </div>
      </div>

      {showModal && selectedForm && (
        <CollaboratorsModal
          form={selectedForm}
          allUsers={allUsers}
          onClose={() => {
            setShowModal(false);
            setSelectedForm(null);
          }}
          onAdd={handleAddCollaborator}
          onRemove={handleRemoveCollaborator}
        />
      )}
    </div>
  );
}
