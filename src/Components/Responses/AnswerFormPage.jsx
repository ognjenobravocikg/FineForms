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
    <div className="max-w-3xl mx-auto p-6 space-y-6">
      <div className="bg-white shadow-lg rounded-xl p-8 space-y-4">
        <h2 className="text-3xl font-bold text-gray-800">{form.title}</h2>
      </div>

      <div className="space-y-4">
        {form.questions.map((q, i) => {
          const key = `q${i + 1}`;
          return (
            <AnswerCard
              key={q.id ?? i}
              question={q}
              value={answers[key]}
              onChange={(val) => updateAnswer(i, val)}
            />
          );
        })}
      </div>

      <div className="flex gap-3 mt-6 justify-end">
        <button
          onClick={handleSubmit}
          disabled={submitting || !userEmail}
          className="px-6 py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:opacity-60"
        >
          {submitting ? "Submitting..." : "Submit Response"}
        </button>
        <button
          onClick={() => navigate("/")}
          className="px-6 py-2 border rounded-xl hover:bg-gray-100"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
