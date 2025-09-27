import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import AnswerCard from "./AnswerCard.jsx";

const API_BASE = "http://localhost:8080/api";

// Simple JWT decoder to extract payload
function parseJwt(token) {
  try {
    return JSON.parse(atob(token.split(".")[1]));
  } catch (e) {
    return null;
  }
}

export default function AnswerFormPage() {
  const { formId } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [form, setForm] = useState(null);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [userId, setUserId] = useState(null);
  const [userEmail, setUserEmail] = useState(null);

  // Decode JWT to get userId
  useEffect(() => {
    if (!token) return;
    const decoded = parseJwt(token);
    const uid = decoded?.id ?? null;
    setUserId(uid);
  }, [token]);

  // Fetch form
  useEffect(() => {
    const fetchForm = async () => {
      setLoading(true);
      try {
        const res = await fetch(`${API_BASE}/form/${formId}`, {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        });
        if (!res.ok) throw new Error("Failed to fetch form");
        const data = await res.json();
        setForm(data);

        // Initialize answers
        const initialAnswers = {};
        data.questions.forEach((q, idx) => {
          const key = `q${idx + 1}`;
          if (q.type === "MULTIPLE_CHOICE" || q.type === "CHECKBOX") {
            initialAnswers[key] = [];
          } else {
            initialAnswers[key] = "";
          }
        });
        setAnswers(initialAnswers);
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchForm();
  }, [formId, token]);

  // Fetch user email
  useEffect(() => {
    const fetchUserEmail = async () => {
      if (!userId || !token) return;

      try {
        const res = await fetch(
          `http://localhost:8080/api/users/${userId}/details`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        if (!res.ok) throw new Error("Failed to fetch user details");
        const data = await res.json();
        setUserEmail(data.email ?? "No email found");
      } catch (err) {
        console.error("Error fetching user details:", err);
        setUserEmail("Error fetching email");
      }
    };
    fetchUserEmail();
  }, [userId, token]);

  const updateAnswer = (index, value) => {
    const key = `q${index + 1}`;
    setAnswers((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async () => {
    if (!form) return;

    // Validate required questions
    for (let i = 0; i < form.questions.length; i++) {
      const q = form.questions[i];
      const key = `q${i + 1}`;
      const val = answers[key];
      if (
        q.required &&
        (val === "" || val === null || (Array.isArray(val) && val.length === 0))
      ) {
        alert(`Question ${i + 1} is required`);
        return;
      }
    }

    const payload = {
      formId: Number(formId),
      userId,
      userEmail,
      answers,
      isAuthenticated: !!token,
    };

    setSubmitting(true);
    try {
      const res = await fetch(`${API_BASE}/response`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message || "Submit failed");
      }

      alert("Response submitted successfully!");
      navigate("/my-forms");
    } catch (err) {
      console.error(err);
      alert("Failed to submit response: " + (err.message || "unknown"));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="p-6 text-gray-600">Loading form...</div>;
  if (error) return <div className="p-6 text-red-600">Error: {error}</div>;
  if (!form) return null;

  return (
    <div className="min-h-screen bg-gray-50 p-6 md:p-12 space-y-8">
      {/* Form Title Card */}
      <div className="bg-gradient-to-r from-indigo-50 to-indigo-100 shadow-md rounded-2xl p-8 space-y-4 border border-indigo-100">
        <h2 className="text-3xl font-bold text-indigo-900">{form.title}</h2>
        {form.description && (
          <p className="text-gray-700 text-lg">{form.description}</p>
        )}
      </div>

      {/* Questions */}
      <div className="space-y-6">
        {form.questions.map((q, i) => {
          const key = `q${i + 1}`;
          return (
            <div
              key={q.id ?? i}
              className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition"
            >
              <div className="text-gray-500 text-sm mb-2">
                Question {i + 1} of {form.questions.length}
              </div>
              <AnswerCard
                question={q}
                value={answers[key]}
                onChange={(val) => updateAnswer(i, val)}
              />
            </div>
          );
        })}
      </div>

      {/* Submit Buttons */}
      <div className="flex gap-3 mt-8 justify-end">
        <button
          onClick={handleSubmit}
          disabled={submitting || !userEmail}
          className="px-6 py-3 bg-indigo-600 text-white rounded-2xl hover:bg-indigo-700 disabled:opacity-50 transition"
        >
          {submitting ? "Submitting..." : "Submit Response"}
        </button>
        <button
          onClick={() => navigate("/")}
          className="px-6 py-3 border rounded-2xl hover:bg-gray-100 transition"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
