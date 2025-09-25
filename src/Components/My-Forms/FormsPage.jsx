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

  const getOwnerIdCandidate = (form) => {
    // accept multiple shapes
    return (
      form.ownerId ??
      form.owner?.id ??
      form.owner?.userId ??
      form.ownerIdString ??
      form.owner?.ownerId ??
      null
    );
  };

  const normalizeFormsArray = (raw) => {
    if (!raw) return [];
    const arr = Array.isArray(raw) ? raw : [raw];
    // Filter to forms that belong to current user (if userId present)
    if (userId) {
      return arr.filter((f) => {
        const cand = getOwnerIdCandidate(f);
        return cand != null && String(cand) === String(userId);
      });
    }
    return arr;
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
      // Try GET /form (most common)
      const res = await fetch(`${API_BASE}/form`, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) {
        // fallback: try GET /form/{userId} if userId present
        if (userId) {
          const alt = await fetch(`${API_BASE}/form/${userId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          });
          if (!alt.ok) {
            const txt = await alt.text();
            throw new Error(`Forms fetch failed: ${alt.status} ${txt}`);
          }
          const altBody = await alt.json();
          const normalized = normalizeFormsArray(altBody);
          setForms(normalized);
          return;
        } else {
          const txt = await res.text();
          throw new Error(`Forms fetch failed: ${res.status} ${txt}`);
        }
      }

      const body = await res.json();
      const normalized = normalizeFormsArray(body);
      setForms(normalized);
    } catch (err) {
      console.error("fetchForms error:", err);
      setError(err.message || "Failed to load forms");
    } finally {
      setLoading(false);
    }
  }, [token, userId]);

  useEffect(() => {
    // initial load: user details, all users and forms
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
        // 1) fetch user details if userId present
        if (userId) {
          const userRes = await fetch(`${API_BASE}/users/${userId}/details`, {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          });
          if (userRes.ok) {
            const u = await userRes.json();
            setUser(u);
          } else {
            // fallback to localStorage user object
            const stored = localStorage.getItem("user");
            if (stored) {
              try {
                setUser(JSON.parse(stored));
              } catch {
                setUser(null);
              }
            }
          }
        } else {
          const stored = localStorage.getItem("user");
          if (stored) setUser(JSON.parse(stored));
        }

        // 2) fetch all users (for collaborator search)
        const usersRes = await fetch(`${API_BASE}/users`, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        if (usersRes.ok) {
          setAllUsers(await usersRes.json());
        } else {
          setAllUsers([]);
        }

        // 3) fetch forms for this owner
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

  // delete a form
  const handleDeleteForm = async (formId) => {
    if (!confirm("Delete this form? This cannot be undone.")) return;
    if (!token) return setError("Not authenticated");

    try {
      const res = await fetch(`${API_BASE}/form/${formId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(`Delete failed: ${res.status} ${txt}`);
      }
      // remove locally
      setForms((prev) => prev.filter((f) => String(f.id) !== String(formId)));
    } catch (err) {
      console.error("delete error:", err);
      alert("Could not delete form: " + (err.message || "unknown"));
    }
  };

  // open collaborators modal and preload collaborators into selectedForm.collaborators
  const openCollaboratorsModal = async (form) => {
    if (!token) {
      navigate("/login");
      return;
    }
    setSelectedForm(form);
    setShowModal(true);

    try {
      const res = await fetch(`${API_BASE}/form/${form.id}/collab`, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      if (!res.ok) {
        // allow empty list if endpoint returns 404
        setSelectedForm((s) => ({ ...s, collaborators: [] }));
        return;
      }
      const collabs = await res.json();
      setSelectedForm((s) => ({ ...s, collaborators: collabs }));
    } catch (err) {
      console.error("collabs fetch error:", err);
      setSelectedForm((s) => ({ ...s, collaborators: [] }));
    }
  };

  // Adds collaborator (parent-level so we can refresh forms list if needed)
  const handleAddCollaborator = async (userIdToAdd) => {
    if (!selectedForm) return alert("No form selected");
    if (!token) return alert("Not authenticated");

    try {
      const res = await fetch(`${API_BASE}/form/${selectedForm.id}/collab`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ userId: userIdToAdd }),
      });

      if (!res.ok) {
        let msg;
        try {
          msg = (await res.json()).message;
        } catch {
          msg = await res.text();
        }
        throw new Error(msg || `Add collaborator failed (${res.status})`);
      }

      // re-fetch collaborators list
      const updated = await fetch(
        `${API_BASE}/form/${selectedForm.id}/collab`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      const collabs = updated.ok ? await updated.json() : [];
      setSelectedForm((s) => ({ ...s, collaborators: collabs }));

      // optional: refetch forms to get backend's updated form object (if it returns updated metadata)
      await fetchForms();
    } catch (err) {
      console.error("add collaborator error:", err);
      alert("Failed to add collaborator: " + (err.message || "unknown"));
    }
  };

  const handleRemoveCollaborator = async (userIdToRemove) => {
    if (!selectedForm) return;
    if (!token) return alert("Not authenticated");

    try {
      const res = await fetch(
        `${API_BASE}/form/${selectedForm.id}/collab/${userIdToRemove}`,
        {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!res.ok) {
        let msg;
        try {
          msg = (await res.json()).message;
        } catch {
          msg = await res.text();
        }
        throw new Error(msg || `Remove collaborator failed (${res.status})`);
      }

      // update local collaborators
      setSelectedForm((s) => ({
        ...s,
        collaborators: s.collaborators?.filter(
          (c) => String(c.id) !== String(userIdToRemove)
        ),
      }));
      await fetchForms(); // refresh forms as well
    } catch (err) {
      console.error("remove collaborator error:", err);
      alert("Failed to remove collaborator: " + (err.message || "unknown"));
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

  return (
    <div className="min-h-screen flex flex-col items-center bg-gray-50 p-6">
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

        {/* Forms list */}
        <div className="space-y-4">
          {forms.length > 0 ? (
            forms.map((form) => (
              <FormCard
                key={form.id}
                form={form}
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

      {/* Collaborators Modal */}
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
