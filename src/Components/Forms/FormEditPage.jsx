import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

export default function FormEditPage() {
  const { formId } = useParams();
  const navigate = useNavigate();

  const [form, setForm] = useState(null);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(true);
  const [fetchError, setFetchError] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    const fetchForm = async () => {
      try {
        const res = await fetch(`${API_BASE}/form/${formId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        });

        if (!res.ok) {
          // get body for helpful message
          let body;
          try {
            body = await res.json();
          } catch {
            body = await res.text();
          }
          throw new Error(
            `Failed to fetch form: ${res.status} ${JSON.stringify(body)}`
          );
        }

        const data = await res.json();
        setForm(data);
        setTitle(data.title || "");
        setDescription(data.description || "");
      } catch (err) {
        console.error(err);
        setFetchError(err.message || "Failed to load form");
      } finally {
        setLoading(false);
      }
    };

    fetchForm();
  }, [formId, navigate]);

  const handleSave = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");

    try {
      const res = await fetch(`${API_BASE}/form/${formId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ title, description }),
      });

      if (!res.ok) {
        let body;
        try {
          body = await res.json();
        } catch {
          body = await res.text();
        }
        throw new Error(
          `Failed to update form: ${res.status} ${JSON.stringify(body)}`
        );
      }

      alert("Form updated successfully!");
      navigate(-1);
    } catch (err) {
      console.error(err);
      alert(err.message || "Error while saving form.");
    }
  };

  const handleDeleteForm = async () => {
    if (!window.confirm("Are you sure you want to delete this form?")) return;

    const token = localStorage.getItem("token");
    try {
      const res = await fetch(`/api/form/${formId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(`Failed to delete form: ${errorText}`);
      }

      alert("Form deleted successfully!");
      navigate("/admin/users"); // or wherever you want to go after delete
    } catch (err) {
      console.error(err);
      alert(err.message || "Error while deleting form.");
    }
  };

  const handleQuestionChange = (index, newText) => {
    const updated = [...questions];
    updated[index].text = newText;
    setQuestions(updated);
  };

  const handleAddQuestion = () => {
    setQuestions([...questions, { id: null, text: "" }]);
  };

  const handleRemoveQuestion = (index) => {
    const updated = [...questions];
    updated.splice(index, 1);
    setQuestions(updated);
  };

  const handleAddCollaborator = async () => {
    if (!newCollaboratorId) return;

    const token = localStorage.getItem("token");

    try {
      const res = await fetch(`/api/form/${formId}/collaborators`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ userId: parseInt(newCollaboratorId, 10) }),
      });

      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(`Failed to add collaborator: ${errorText}`);
      }

      const added = await res.json();
      setCollaborators((prev) => [...prev, added]);
      setNewCollaboratorId("");
    } catch (err) {
      console.error(err);
      alert(err.message || "Error while adding collaborator.");
    }
  };

  if (loading) return <p className="p-6 text-center">Loading...</p>;
  if (fetchError)
    return <p className="p-6 text-center text-red-600">Error: {fetchError}</p>;
  if (!form) return <p className="p-6 text-center">Form not found.</p>;

  return (
    <div className="max-w-4xl mx-auto p-8 space-y-8">
      <h1 className="text-2xl font-bold text-gray-800">Edit Form</h1>

      {/* Form Info */}
      <form
        onSubmit={handleSave}
        className="bg-white shadow-md rounded-lg p-6 space-y-4"
      >
        <div>
          <label className="block text-gray-700 font-medium mb-2">Title</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-indigo-500"
            required
          />
        </div>

        <div>
          <label className="block text-gray-700 font-medium mb-2">
            Description
          </label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-indigo-500"
            rows="3"
            required
          />
        </div>

        {/* Questions Section */}
        <div>
          <h2 className="text-lg font-semibold text-gray-800 mb-3">
            Questions
          </h2>
          <div className="space-y-3">
            {questions.map((q, i) => (
              <div key={i} className="flex gap-2 items-center">
                <input
                  type="text"
                  value={q.text}
                  onChange={(e) => handleQuestionChange(i, e.target.value)}
                  className="flex-1 p-2 border rounded-lg"
                  placeholder={`Question ${i + 1}`}
                />
                <button
                  type="button"
                  onClick={() => handleRemoveQuestion(i)}
                  className="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700"
                >
                  Remove
                </button>
              </div>
            ))}
          </div>
          <button
            type="button"
            onClick={handleAddQuestion}
            className="mt-3 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
          >
            + Add Question
          </button>
        </div>

        {/* Collaborators Section */}
        <div>
          <h2 className="text-lg font-semibold text-gray-800 mb-3">
            Collaborators
          </h2>
          <ul className="list-disc list-inside mb-3 text-gray-700">
            {collaborators.map((c) => (
              <li key={c.id}>{c.email || `User ID: ${c.id}`}</li>
            ))}
          </ul>
          <div className="flex gap-2">
            <input
              type="number"
              value={newCollaboratorId}
              onChange={(e) => setNewCollaboratorId(e.target.value)}
              className="flex-1 p-2 border rounded-lg"
              placeholder="Enter User ID"
            />
            <button
              type="button"
              onClick={handleAddCollaborator}
              className="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700"
            >
              Add
            </button>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex justify-between pt-6">
          <button
            type="button"
            onClick={handleDeleteForm}
            className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
          >
            Delete Form
          </button>

          <div className="flex gap-4">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-6 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
            >
              Save Changes
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
