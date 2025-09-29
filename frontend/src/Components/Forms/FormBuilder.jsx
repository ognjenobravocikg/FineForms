import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import FormHeader from "./FormHeader";
import QuestionCard from "./QuestionCard";

const API_BASE = "http://localhost:8080/api";

export default function FormBuilder() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [requiresAuth, setRequiresAuth] = useState(true);
  const [questions, setQuestions] = useState([]);
  const [saving, setSaving] = useState(false);

  const navigate = useNavigate();

  const addQuestion = () =>
    setQuestions((s) => [
      ...s,
      {
        id: null,
        text: "",
        required: false,
        type: "SHORT_ANSWER",
        imageUrl: null,
        numberMin: null,
        numberMax: null,
        numberStep: 1,
        options: [],
      },
    ]);

  const updateQuestion = (index, q) => {
    setQuestions((prev) => {
      const next = [...prev];
      next[index] = q;
      return next;
    });
  };

  const removeQuestion = (index) =>
    setQuestions((prev) => prev.filter((_, i) => i !== index));

  const toDto = (q) => {
    const options =
      Array.isArray(q.options) && q.options.length
        ? q.options.map((opt) => ({
            text: opt.text ?? "",
            correct: opt.correct ?? false,
            imageUrl: opt.imageUrl ?? null,
          }))
        : null;

    return {
      id: q.id ?? null,
      text: q.text ?? "",
      type: q.type,
      required: !!q.required,
      imageUrl: q.imageUrl ?? null,
      numberMin: q.numberMin,
      numberMax: q.numberMax,
      numberStep: q.numberStep,
      options,
    };
  };

  const handleSubmit = async () => {
    if (!title.trim()) return alert("Title is required");
    if (questions.length === 0) return alert("Add at least one question");

    const token = localStorage.getItem("token");
    const ownerIdRaw =
      localStorage.getItem("userId") ||
      (localStorage.getItem("user") &&
        JSON.parse(localStorage.getItem("user")).id);
    const ownerId = ownerIdRaw ? parseInt(ownerIdRaw, 10) : null;

    if (!token || !ownerId) {
      return alert("You must be logged in to create a form.");
    }

    const payload = {
      title,
      description,
      requiresAuth,
      ownerId,
      questions: questions.map(toDto),
    };

    setSaving(true);
    try {
      const res = await fetch(`${API_BASE}/form`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        let msg = `Server returned ${res.status}`;
        try {
          const json = await res.json();
          msg = json.message || JSON.stringify(json);
        } catch {
          msg = await res.text();
        }
        throw new Error(msg);
      }

      const created = await res.json();
      alert("Form created successfully");
      navigate("/my-forms");
    } catch (err) {
      console.error("Create form failed:", err);
      alert("Could not save form: " + (err.message || "network error"));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto p-6">
      <FormHeader
        title={title}
        setTitle={setTitle}
        description={description}
        setDescription={setDescription}
        requiresAuth={requiresAuth}
        setRequiresAuth={setRequiresAuth}
      />

      {questions.map((q, i) => (
        <QuestionCard
          key={i}
          index={i}
          question={q}
          updateQuestion={(newQ) => updateQuestion(i, newQ)}
          removeQuestion={() => removeQuestion(i)}
        />
      ))}

      <div className="flex gap-3 mt-6">
        <button
          onClick={addQuestion}
          className="px-4 py-2 border rounded hover:bg-gray-100"
        >
          + Add Question
        </button>
        <button
          onClick={handleSubmit}
          disabled={saving}
          className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-60"
        >
          {saving ? "Saving..." : "Save Form"}
        </button>
        <button
          onClick={() => navigate(-1)}
          className="px-4 py-2 border rounded hover:bg-gray-100"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
