import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import FormHeader from "./FormHeader";
import QuestionCard from "./QuestionCard";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";

const API_BASE = "http://localhost:8080/api";
const currentUserId = localStorage.getItem("userId");

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
  const token = localStorage.getItem("token");

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [questions, setQuestions] = useState([]);
  const [collaborators, setCollaborators] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [ownerId, setOwnerId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // --- Load form and collaborators ---
  useEffect(() => {
    if (!token) return navigate("/login");

    async function loadData() {
      try {
        setLoading(true);

        // --- Fetch form ---
        const formRes = await fetch(`${API_BASE}/form/${formId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!formRes.ok) throw new Error("Failed to fetch form");
        const form = await parseResponse(formRes);

        setTitle(form.title || "");
        setDescription(form.description || "");
        setOwnerId(form.ownerId ?? form.owner?.id ?? null);

        setQuestions(
          (form.questions || [])
            .sort((a, b) => (a.position ?? 0) - (b.position ?? 0))
            .map((q, idx) => ({
              id: q.id ?? null,
              text: q.text ?? "",
              type: q.type ?? "SHORT_ANSWER",
              required: q.required ?? false,
              numberMin: q.numberMin ?? 0,
              numberMax: q.numberMax ?? 0,
              numberStep: q.numberStep ?? 1,
              imageUrl: q.imageUrl ?? null,
              options: (q.options || []).map((o) => ({
                text: o.text ?? "",
                correct: o.isCorrect ?? false,
                imageUrl: o.imageUrl ?? null,
              })),
              position: q.position ?? idx,
            }))
        );

        // --- Fetch collaborators ---
        const collabRes = await fetch(`${API_BASE}/form/${formId}/collab`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const collabs = collabRes.ok ? await parseResponse(collabRes) : [];
        setCollaborators(collabs);

        // --- Fetch all users ---
        const usersRes = await fetch(`${API_BASE}/users`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const users = usersRes.ok ? await parseResponse(usersRes) : [];
        setAllUsers(
          users.filter((u) => String(u.id) !== String(currentUserId))
        );
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, [formId, token, navigate]);

  // --- Determine roles ---
  const isOwner = String(currentUserId) === String(ownerId);
  const myCollab = collaborators.find(
    (c) => String(c.userId) === String(currentUserId)
  );
  const myRole = myCollab?.role ?? (isOwner ? "OWNER" : "VIEWER");

  const canEditForm = isOwner || myRole === "EDITOR";
  const canManageCollabs = isOwner;

  // --- Question handlers ---
  const addQuestion = () => {
    setQuestions((prev) => [
      ...prev,
      {
        id: null,
        text: "",
        type: "SHORT_ANSWER",
        required: false,
        numberMin: 0,
        numberMax: 0,
        numberStep: 1,
        imageUrl: null,
        options: [],
        position: prev.length,
      },
    ]);
  };

  const updateQuestion = (index, q) => {
    if (!canEditForm) return;
    setQuestions((prev) => {
      const next = [...prev];
      next[index] = { ...q, position: index };
      return next;
    });
  };

  const removeQuestion = (index) => {
    if (!canEditForm) return;
    setQuestions((prev) =>
      prev.filter((_, i) => i !== index).map((q, i) => ({ ...q, position: i }))
    );
  };

  const onDragEnd = (result) => {
    if (!canEditForm) return;
    if (!result.destination) return;

    const newQuestions = Array.from(questions);
    const [moved] = newQuestions.splice(result.source.index, 1);
    newQuestions.splice(result.destination.index, 0, moved);

    setQuestions(newQuestions.map((q, i) => ({ ...q, position: i })));
  };

  // --- Collaborator handlers ---
  const addCollaborator = async (userIdToAdd, role = "VIEWER") => {
    if (!canManageCollabs) return;
    try {
      const res = await fetch(`${API_BASE}/form/${formId}/collab`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ userId: userIdToAdd, role }),
      });
      if (!res.ok) throw new Error("Failed to add collaborator");
      const updated = await fetch(`${API_BASE}/form/${formId}/collab`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCollaborators(updated.ok ? await parseResponse(updated) : []);
    } catch (err) {
      console.error(err);
      alert("Add collaborator failed: " + err.message);
    }
  };

  const removeCollaborator = async (userIdToRemove) => {
    if (!canManageCollabs) return;
    if (!window.confirm("Remove collaborator?")) return;
    try {
      const res = await fetch(
        `${API_BASE}/form/${formId}/collab/${userIdToRemove}`,
        {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      if (!res.ok) throw new Error("Failed to remove collaborator");
      setCollaborators((prev) =>
        prev.filter((c) => String(c.userId) !== String(userIdToRemove))
      );
    } catch (err) {
      console.error(err);
      alert("Remove collaborator failed: " + err.message);
    }
  };

  const changeRole = async (userId, newRole) => {
    if (!canManageCollabs) return;
    try {
      const res = await fetch(
        `${API_BASE}/form/${formId}/collab/${userId}/update-role?collaboratorRole=${encodeURIComponent(
          newRole
        )}`,
        { method: "POST", headers: { Authorization: `Bearer ${token}` } }
      );
      if (!res.ok) throw new Error("Failed to update role");
      setCollaborators((prev) =>
        prev.map((c) =>
          String(c.userId) === String(userId) ? { ...c, role: newRole } : c
        )
      );
    } catch (err) {
      console.error(err);
      alert("Role update failed: " + err.message);
    }
  };

  // --- Save form ---
  const handleSave = async () => {
    if (!canEditForm) return;
    setSaving(true);
    try {
      const payload = {
        id: Number(formId),
        title,
        description,
        questions: questions
          .filter((q) => q.text.trim() !== "")
          .map((q) => ({
            id: q.id ?? null,
            text: q.text,
            type: q.type,
            required: q.required ?? false,
            numberMin: q.numberMin ?? 0,
            numberMax: q.numberMax ?? 0,
            numberStep: q.numberStep ?? 1,
            imageUrl: q.imageUrl ?? null,
            position: q.position,
            options:
              q.type === "MULTIPLE_CHOICE" || q.type === "SINGLE_CHOICE"
                ? q.options?.map((o) => ({
                    text: o.text ?? "",
                    correct: o.correct ?? false,
                    imageUrl: o.imageUrl ?? null,
                  })) || []
                : null,
          })),
      };

      const res = await fetch(`${API_BASE}/form/${formId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
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

  if (loading) return <div className="p-6">Loading...</div>;
  if (error) return <div className="p-6 text-red-600">Error: {error}</div>;

  return (
    <div className="max-w-3xl mx-auto p-6 space-y-6">
      {/* Header */}
      <FormHeader
        title={title}
        setTitle={canEditForm ? setTitle : undefined}
        description={description}
        setDescription={canEditForm ? setDescription : undefined}
        readOnly={!canEditForm}
      />

      {/* Questions */}
      <DragDropContext onDragEnd={onDragEnd}>
        <Droppable droppableId="questions">
          {(provided) => (
            <div
              {...provided.droppableProps}
              ref={provided.innerRef}
              className="space-y-4"
            >
              {questions.map((q, i) => (
                <Draggable
                  key={q.id ?? i}
                  draggableId={String(q.id ?? i)}
                  index={i}
                >
                  {(prov) => (
                    <div
                      ref={prov.innerRef}
                      {...prov.draggableProps}
                      {...prov.dragHandleProps}
                    >
                      <QuestionCard
                        question={q}
                        updateQuestion={(newQ) => updateQuestion(i, newQ)}
                        removeQuestion={() => removeQuestion(i)}
                        readOnly={!canEditForm}
                      />
                    </div>
                  )}
                </Draggable>
              ))}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </DragDropContext>

      {/* Add Question */}
      {canEditForm && (
        <button
          onClick={addQuestion}
          className="px-4 py-2 border rounded-lg hover:bg-gray-100"
        >
          + Add Question
        </button>
      )}

      {/* Collaborators */}
      {canManageCollabs && (
        <div className="space-y-3 border-t pt-4">
          <h2 className="text-lg font-semibold">Collaborators</h2>

          {collaborators.length === 0 ? (
            <p className="text-sm text-gray-500">No collaborators yet</p>
          ) : (
            collaborators.map((c) => {
              const user =
                allUsers.find((u) => String(u.id) === String(c.userId)) || {};
              return (
                <div
                  key={c.collaborationId}
                  className="flex justify-between items-center border p-2 rounded"
                >
                  <div>
                    <div className="font-medium">
                      {user.firstName} {user.lastName}
                    </div>
                    <div className="text-sm text-gray-500">{user.email}</div>
                    <div className="text-sm text-gray-600">
                      Role:{" "}
                      <select
                        value={c.role}
                        onChange={(e) => changeRole(c.userId, e.target.value)}
                        className="border rounded px-1 py-0.5 text-sm"
                      >
                        <option value="VIEWER">Viewer</option>
                        <option value="EDITOR">Editor</option>
                      </select>
                    </div>
                  </div>
                  <button
                    onClick={() => removeCollaborator(c.userId)}
                    className="px-2 py-1 bg-red-500 text-white rounded"
                  >
                    Remove
                  </button>
                </div>
              );
            })
          )}

          <div className="pt-2">
            <h3 className="font-medium mb-1">Add Collaborator</h3>
            {allUsers.map((u) => {
              const already = collaborators.some(
                (c) => String(c.userId) === String(u.id)
              );
              return (
                <div
                  key={u.id}
                  className="flex justify-between items-center border p-2 rounded mb-1"
                >
                  <span>
                    {u.firstName} {u.lastName} – {u.email}
                  </span>
                  <button
                    disabled={already}
                    onClick={() => addCollaborator(u.id, "VIEWER")}
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
      )}

      {/* Footer */}
      <div className="flex justify-end pt-4 gap-3">
        <button
          onClick={() => navigate(-1)}
          className="px-4 py-2 border rounded"
        >
          Cancel
        </button>
        {canEditForm && (
          <button
            onClick={handleSave}
            className="px-6 py-2 bg-indigo-600 text-white rounded"
            disabled={saving}
          >
            {saving ? "Saving..." : "Save Changes"}
          </button>
        )}
      </div>
    </div>
  );
}
