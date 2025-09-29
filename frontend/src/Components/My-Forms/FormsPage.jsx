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
      const res = await fetch(`${API_BASE}/form`, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      if (!res.ok) throw new Error(`Failed to fetch forms (${res.status})`);
      const allForms = await res.json();

      const formsWithDetails = await Promise.all(
        allForms.map(async (form) => {
          const collabs = await fetchFormCollaborators(form.id);

          let userRole = "NONE";
          if (String(form.ownerId) === String(userId)) userRole = "OWNER";
          else {
            const c = collabs.find((c) => String(c.userId) === String(userId));
            if (c) userRole = c.role; // EDITOR or VIEWER
          }

          // Fetch answers count
          let answersCount = 0;
          try {
            const currentUserId = parseUserFromStorage();
            const resCount = await fetch(
              `${API_BASE}/response/${form.id}?currentUserId=${currentUserId}`,
              {
                headers: { Authorization: `Bearer ${token}` },
              }
            );
            if (resCount.ok) {
              const responses = await resCount.json();
              answersCount = Array.isArray(responses) ? responses.length : 0;
            }
          } catch (err) {
            console.warn("Failed to fetch responses count", err);
          }

          return { ...form, userRole, collaborators: collabs, answersCount };
        })
      );

      const visibleForms = formsWithDetails.filter(
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
        if (userId) {
          const userRes = await fetch(`${API_BASE}/users/${userId}/details`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          if (userRes.ok) setUser(await userRes.json());
          else {
            const stored = localStorage.getItem("user");
            if (stored) setUser(JSON.parse(stored));
          }
        }

        const usersRes = await fetch(`${API_BASE}/users`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setAllUsers(usersRes.ok ? await usersRes.json() : []);

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

  const handleExportCSV = async (formId) => {
    if (!token) {
      alert("You must be logged in to export responses");
      return;
    }

    try {
      const res = await fetch(`${API_BASE}/response/export?formId=${formId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error(`Export failed (${res.status})`);

      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `form_${formId}_responses.csv`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Export CSV error:", err);
      alert("Failed to export CSV: " + (err.message || "unknown"));
    }
  };

  if (loading) return <div className="p-6 text-gray-600">Loading...</div>;
  if (error) return <div className="p-6 text-red-600">Error: {error}</div>;

  return (
    <div className="min-h-screen flex flex-col items-center bg-indigo-400 p-6">
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

        <div className="space-y-4">
          {forms.length > 0 ? (
            forms.map((form) => {
              const answerUrl = `${window.location.origin}/form/${form.id}/answer`;
              return (
                <FormCard
                  key={form.id}
                  form={form}
                  onEdit={() => navigate(`/form/${form.id}`)}
                  onViewButton={() => navigate(`/responses/${form.id}`)}
                  onCollaborators={() => openCollaboratorsModal(form)}
                  onDelete={() => handleDeleteForm(form.id)}
                  extraActions={
                    <div className="flex gap-2 mt-2 flex-wrap items-center">
                      <span className="text-gray-500 text-sm">
                        Answers: {form.answersCount ?? 0}
                      </span>
                      <a
                        href={answerUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="px-3 py-1 rounded hover:bg-green-200 text-sm"
                      >
                        Answer
                      </a>
                      <button
                        onClick={() => navigator.clipboard.writeText(answerUrl)}
                        className="px-3 py-1 rounded hover:bg-indigo-300 text-sm"
                      >
                        Copy Link
                      </button>
                      <button
                        onClick={() => handleExportCSV(form.id)}
                        className="px-3 py-1 rounded hover:bg-indigo-300 text-sm"
                      >
                        Export CSV
                      </button>
                    </div>
                  }
                />
              );
            })
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
