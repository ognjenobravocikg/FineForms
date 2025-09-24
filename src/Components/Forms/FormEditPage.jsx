import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

export default function FormEditPage() {
  const { formId } = useParams();
  const navigate = useNavigate();

  const [form, setForm] = useState(null);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    const fetchForm = async () => {
      try {
        // Replace with backend GET endpoint for a single form
        const res = await fetch(`/api/forms/${formId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) throw new Error("Failed to fetch form");
        const data = await res.json();

        setForm(data);
        setTitle(data.title || "");
        setDescription(data.description || "");
      } catch (err) {
        console.error(err);
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
      // Replace with backend PUT/PATCH endpoint
      const res = await fetch(`/api/forms/${formId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ title, description }),
      });

      if (res.ok) {
        alert("Form updated successfully!");
        navigate(-1); // go back to previous page
      } else {
        alert("Failed to update form.");
      }
    } catch (err) {
      console.error(err);
      alert("Error while saving form.");
    }
  };

  if (loading) return <p>Loading...</p>;
  if (!form) return <p>Form not found.</p>;

  return (
    <div className="max-w-3xl mx-auto p-8">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Edit Form</h1>

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
            rows="4"
            required
          ></textarea>
        </div>

        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-6 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
          >
            Save Changes
          </button>
        </div>
      </form>
    </div>
  );
}
