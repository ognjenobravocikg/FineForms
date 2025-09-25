import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import FormHeader from "./FormHeader";
import QuestionCard from "./QuestionCard";

const API_BASE = "http://localhost:8080/api";
const userId = localStorage.getItem("userId");

async function parseResponse(res) {
  const ct = res.headers.get("content-type") || "";
  if (ct.includes("application/json")) return res.json();
  const text = await res.text();
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

export default function FormEditPage() {
  const { formId } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [questions, setQuestions] = useState([]);
  const [collaborators, setCollaborators] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [userSearch, setUserSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [rolesMap, setRolesMap] = useState({});

  const token = localStorage.getItem("token");

  // --- Load form, questions, collaborators, users ---
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    async function load() {
      try {
        setLoading(true);

        // Fetch form
        const formRes = await fetch(`${API_BASE}/form/${formId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!formRes.ok) throw new Error("Failed to fetch form");
        const form = await parseResponse(formRes);

        setTitle(form.title || "");
        setDescription(form.description || "");
        setQuestions(
          (form.questions || []).map((q, idx) => ({
            id: Number(formId),
            text: q.text ?? "",
            requiredQuestion: q.required ?? false,
            type: q.type || "short_text",
            imageUrl: q.imageUrl ?? null,
            numberMin: q.numberMin ?? 0,
            numberMax: q.numberMax ?? 0,
            numberStep: q.numberStep ?? 1,
            minRequiredAnswers: q.minRequiredAnswers ?? null,
            maxAllowedAnswers: q.maxAllowedAnswers ?? null,
            position: q.position ?? idx,
            options: (q.options || []).map((o, oidx) => ({
              id: o.id ?? null,
              text: o.text ?? "",
              order: o.order ?? oidx,
              isCorrect: o.isCorrect ?? false,
              imageUrl: o.imageUrl ?? null,
              questionId: q.id ?? null,
            })),
          }))
        );

        // Fetch collaborators
        const collabRes = await fetch(`${API_BASE}/form/${formId}/collab`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (collabRes.ok) setCollaborators(await parseResponse(collabRes));

        // Fetch all users
        const usersRes = await fetch(`${API_BASE}/users`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (usersRes.ok) {
          const users = await parseResponse(usersRes);
          setAllUsers(users);
          const map = {};
          users.forEach((u) => (map[u.id] = "VIEWER"));
          setRolesMap(map);
        }
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [formId, token, navigate]);

  // --- Question handlers ---
  const addQuestion = () => {
    setQuestions((prev) => [
      ...prev,
      {
        id: null,
        text: "",
        requiredQuestion: false,
        type: "short_text",
        imageUrl: null,
        numberMin: 0,
        numberMax: 0,
        numberStep: 1,
        minRequiredAnswers: null,
        maxAllowedAnswers: null,
        position: prev.length,
        options: [],
      },
    ]);
  };

  const updateQuestion = (index, q) => {
    setQuestions((prev) => {
      const next = [...prev];
      next[index] = q;
      return next;
    });
  };

  const removeQuestion = (index) => {
    setQuestions((prev) => prev.filter((_, i) => i !== index));
  };

  // --- Save form ---
  const handleSave = async () => {
    console.log(q.id);

    setSaving(true);
    try {
      const payloadQuestions = questions.map((q, idx) => ({
        id: q.id,
        text: q.text,
        requiredQuestion: q.requiredQuestion ?? false,
        type: q.type,
        position: q.position ?? idx,
        imageUrl: q.imageUrl ?? null,
        numberMin: q.numberMin ?? 0,
        numberMax: q.numberMax ?? 0,
        numberStep: q.numberStep ?? 1,
        minRequiredAnswers: q.minRequiredAnswers ?? null,
        maxAllowedAnswers: q.maxAllowedAnswers ?? null,
        options: (q.options || []).map((o, oidx) => ({
          id: o.id ?? null,
          text: o.text ?? "",
          order: o.order ?? oidx,
          isCorrect: o.isCorrect ?? false,
          imageUrl: o.imageUrl ?? null,
          questionId: q.id ?? null,
        })),
      }));

      const res = await fetch(`${API_BASE}/form/${formId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          id: Number(formId),
          title,
          description,
          questions: payloadQuestions,
        }),
      });

      if (!res.ok) {
        const errBody = await parseResponse(res);
        throw new Error(errBody.message || "Failed to save form");
      }

      alert("Form updated successfully!");
      navigate("/my-forms");
    } catch (err) {
      console.error(err);
      alert("Save failed: " + err.message);
    } finally {
      setSaving(false);
    }
  };

  // --- Delete form ---
  const handleDelete = async () => {
    if (!window.confirm("Delete this form?")) return;
    try {
      const res = await fetch(`${API_BASE}/form/${formId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error("Failed to delete form");
      alert("Form deleted.");
      navigate("/forms");
    } catch (err) {
      console.error(err);
      alert("Delete failed: " + err.message);
    }
  };

  // --- Collaborators ---
  const handleAddCollaborator = async (userId) => {
    try {
      const role = (rolesMap[userId] || "VIEWER").toUpperCase();
      const res = await fetch(`${API_BASE}/form/${formId}/collab`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ userId, role }),
      });
      if (!res.ok) {
        const errBody = await parseResponse(res);
        throw new Error(errBody.message || "Failed to add collaborator");
      }
      await reloadCollaborators();
    } catch (err) {
      console.error(err);
      alert("Add collaborator failed: " + err.message);
    }
  };

  const handleRemoveCollaborator = async (collaboratorId) => {
    if (!window.confirm("Remove collaborator?")) return;
    try {
      const res = await fetch(
        `${API_BASE}/form/${formId}/collab/${collaboratorId}`,
        {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      if (!res.ok) throw new Error("Failed to remove collaborator");
      await reloadCollaborators();
    } catch (err) {
      console.error(err);
      alert("Remove collaborator failed: " + err.message);
    }
  };

  const reloadCollaborators = async () => {
    const res = await fetch(`${API_BASE}/form/${formId}/collab`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) setCollaborators(await parseResponse(res));
  };

  // --- Filter users ---
  const filteredUsers = allUsers
    .filter((u) => String(u.id) !== String(userId)) // <-- convert both to string
    .filter((u) => {
      const q = userSearch.toLowerCase();
      return (
        u.firstName?.toLowerCase().includes(q) ||
        u.lastName?.toLowerCase().includes(q) ||
        u.email?.toLowerCase().includes(q)
      );
    });

  // --- Image upload ---
  const handleImageUpload = async (file, cb) => {
    const formData = new FormData();
    formData.append("file", file);
    const res = await fetch(`${API_BASE}/upload`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      body: formData,
    });
    if (!res.ok) throw new Error("Image upload failed");
    const data = await res.json();
    cb(data.url);
  };

  if (loading) return <div className="p-6">Loading...</div>;
  if (error) return <div className="p-6 text-red-600">Error: {error}</div>;

  return (
    <div className="max-w-3xl mx-auto p-6 space-y-6">
      <FormHeader
        title={title}
        setTitle={setTitle}
        description={description}
        setDescription={setDescription}
      />

      {questions.map((q, i) => (
        <QuestionCard
          key={i}
          question={q}
          updateQuestion={(newQ) => updateQuestion(i, newQ)}
          removeQuestion={() => removeQuestion(i)}
          onUploadImage={(file) =>
            handleImageUpload(file, (url) =>
              updateQuestion(i, { ...q, imageUrl: url })
            )
          }
          onUploadOptionImage={(optIndex, file) =>
            handleImageUpload(file, (url) => {
              const newOptions = [...q.options];
              newOptions[optIndex] = { ...newOptions[optIndex], imageUrl: url };
              updateQuestion(i, { ...q, options: newOptions });
            })
          }
        />
      ))}

      <button
        onClick={addQuestion}
        className="px-4 py-2 border rounded-lg hover:bg-gray-100"
      >
        + Add Question
      </button>

      {/* Collaborators */}
      <div className="space-y-3 border-t pt-4">
        <h2 className="text-lg font-semibold">Collaborators</h2>
        {collaborators.length === 0 ? (
          <p className="text-sm text-gray-500">No collaborators yet</p>
        ) : (
          collaborators.map((c) => {
            const user = allUsers.find((u) => u.id === c.userId);
            if (!user) return null; // skip if user not found

            return (
              <div
                key={c.collaborationId} // use collaborationId as key
                className="flex justify-between items-center border p-2 rounded"
              >
                <div>
                  <div className="font-medium">
                    {user.firstName} {user.lastName}
                  </div>
                  <div className="text-sm text-gray-500">{user.email}</div>
                  <div className="text-sm text-gray-600">Role: {c.role}</div>
                </div>
                <button
                  onClick={() => handleRemoveCollaborator(c.userId)}
                  className="px-2 py-1 bg-red-500 text-white rounded"
                >
                  Remove
                </button>
              </div>
            );
          })
        )}

        <input
          value={userSearch}
          onChange={(e) => setUserSearch(e.target.value)}
          placeholder="Search users..."
          className="w-full border rounded p-2"
        />
        <div className="space-y-2 max-h-48 overflow-y-auto">
          {filteredUsers.map((u) => {
            const already = collaborators.some((c) => c.userId === u.id);
            return (
              <div
                key={u.id}
                className="flex justify-between items-center border p-2 rounded"
              >
                <div className="flex flex-col">
                  <span>
                    {u.firstName} {u.lastName} – {u.email}
                  </span>
                  <select
                    value={rolesMap[u.id] || "VIEWER"}
                    onChange={(e) =>
                      setRolesMap((prev) => ({
                        ...prev,
                        [u.id]: e.target.value,
                      }))
                    }
                    disabled={already}
                    className="border rounded mt-1 text-sm p-1"
                  >
                    <option value="VIEWER">Viewer</option>
                    <option value="EDITOR">Editor</option>
                  </select>
                </div>
                <button
                  disabled={already}
                  onClick={() => handleAddCollaborator(u.id)}
                  className={`px-2 py-1 rounded ${
                    already ? "bg-gray-300" : "bg-blue-500 text-white"
                  }`}
                >
                  {already ? "Added" : "Add"}
                </button>
              </div>
            );
          })}
        </div>
      </div>

      <div className="flex justify-between pt-4">
        <button
          onClick={handleDelete}
          className="px-4 py-2 bg-red-600 text-white rounded"
        >
          Delete Form
        </button>
        <div className="flex gap-3">
          <button
            onClick={() => navigate(-1)}
            className="px-4 py-2 border rounded"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="px-6 py-2 bg-indigo-600 text-white rounded"
            disabled={saving}
          >
            {saving ? "Saving..." : "Save Changes"}
          </button>
        </div>
      </div>
    </div>
  );
}
