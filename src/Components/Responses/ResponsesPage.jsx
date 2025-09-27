// ResponsesPage.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

export default function ResponsesPage() {
  const { formId } = useParams();
  const navigate = useNavigate();

  const [form, setForm] = useState(null);
  const [responses, setResponses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchFormAndResponses = async () => {
      setLoading(true);
      setError(null);
      try {
        // Fetch form
        const formRes = await fetch(`${API_BASE}/form/${formId}`, {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        });
        if (!formRes.ok)
          throw new Error(`Failed to fetch form (${formRes.status})`);
        const formData = await formRes.json();

        // Sort questions by position
        formData.questions.sort(
          (a, b) => Number(a.position) - Number(b.position)
        );
        setForm(formData);

        // Fetch responses
        const respRes = await fetch(`${API_BASE}/response/${formId}`, {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        });
        if (!respRes.ok)
          throw new Error(`Failed to fetch responses (${respRes.status})`);
        const respData = await respRes.json();
        setResponses(Array.isArray(respData) ? respData : []);
      } catch (err) {
        setError(err.message || "Failed to load data");
      } finally {
        setLoading(false);
      }
    };

    fetchFormAndResponses();
  }, [formId, token]);

  const handleDeleteResponse = async (responseId) => {
    if (!confirm("Delete this response? This cannot be undone.")) return;
    if (!token) return alert("You must be logged in to delete responses.");

    try {
      setDeletingId(responseId);
      const res = await fetch(`${API_BASE}/response/${responseId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error(`Delete failed (${res.status})`);
      setResponses((prev) => prev.filter((r) => r.id !== responseId));
    } catch (err) {
      alert("Failed to delete response: " + (err.message || "unknown"));
    } finally {
      setDeletingId(null);
    }
  };

  if (loading)
    return <div className="p-6 text-gray-600">Loading responses...</div>;
  if (error) return <div className="p-6 text-red-600">Error: {error}</div>;
  if (!form) return <div className="p-6 text-gray-600">Form not found.</div>;

  return (
    <div className="p-6 min-h-screen bg-gray-50">
      {/* Form header */}
      <div className="bg-white p-6 rounded-lg shadow mb-8">
        <h1 className="text-3xl font-bold mb-2">{form.title}</h1>
        <p className="text-gray-600 mb-2">{form.description}</p>
        <p className="text-sm text-gray-500">Form ID: {form.id}</p>
        <p className="text-sm text-gray-500">
          Total Responses: {responses.length}
        </p>
      </div>

      {/* Questions without answers */}
      <div className="space-y-6 mb-8">
        {form.questions.map((q) => (
          <div key={q.id} className="bg-white p-5 rounded-lg shadow">
            <div className="flex justify-between items-center mb-3">
              <div>
                <h2 className="text-lg font-semibold text-gray-800">
                  {q.text}{" "}
                  {q.requiredQuestion && (
                    <span className="text-red-500">*</span>
                  )}
                </h2>
                <p className="text-sm text-gray-500">
                  Type: {q.type}
                  {q.type === "RANGE" &&
                    ` (min: ${q.numberMin} — max: ${q.numberMax}, step: ${q.numberStep})`}
                  {q.options?.length > 0 &&
                    ` • Options: ${q.options.map((o) => o.text).join(", ")}`}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Responses with answers */}
      <div className="space-y-4">
        {responses.map((resp, idx) => (
          <div
            key={resp.id}
            className="bg-white p-4 rounded-lg shadow flex flex-col md:flex-row md:justify-between"
          >
            <div>
              <div className="text-sm text-gray-500">Response #{idx + 1}</div>
              <div className="font-medium text-gray-800 mb-2">
                ID: {resp.id}
              </div>
              <div className="text-sm text-gray-500 mb-2">
                {resp.isAuthenticated ? "Authenticated" : "Anonymous"}
                {resp.userEmail ? ` • ${resp.userEmail}` : ""}
              </div>
              <div className="space-y-1 text-sm">
                {Object.entries(resp.answers).map(([k, v]) => (
                  <div key={k}>
                    <span className="text-gray-500">{k}:</span>{" "}
                    <span className="text-gray-900">
                      {Array.isArray(v) ? v.join(", ") : String(v)}
                    </span>
                  </div>
                ))}
              </div>
            </div>
            <div className="mt-3 md:mt-0 flex gap-2 items-center">
              <button
                onClick={() => handleDeleteResponse(resp.id)}
                disabled={deletingId === resp.id}
                className="px-3 py-2 bg-red-600 text-white rounded hover:bg-red-700 disabled:opacity-60"
              >
                {deletingId === resp.id ? "Deleting..." : "Delete"}
              </button>
              <button
                onClick={() => navigate(`/form/${formId}`)}
                className="px-3 py-2 border rounded hover:bg-gray-100"
              >
                View Form
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
