// FormBuilder.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import FormHeader from "./FormHeader";
import QuestionCard from "./QuestionCard";

const API_BASE = "http://localhost:8080/api";

export default function FormBuilder() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [questions, setQuestions] = useState([]);
  const [publicForm, setPublicForm] = useState(false);

  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();

  const addQuestion = () =>
    setQuestions((s) => [
      ...s,
      {
        id: null,
        text: "",
        required: false,
        type: "short_text",
        imageUrl: null,
        numberMin: 0,
        numberMax: 0,
        numberStep: 1,
        minRequiredAnswers: null,
        maxAllowedAnswers: null,
        options: [],
      },
    ]);

  const updateQuestion = (index, q) => {
    setQuestions((prev) => {
      const next = prev.slice();
      next[index] = q;
      return next;
    });
  };

  const removeQuestion = (index) =>
    setQuestions((prev) => prev.filter((_, i) => i !== index));

  // map frontend question -> CreateQuestionDto
  const toDto = (q) => {
    // NOTE: backend DTO has numberMin/numberMax fields often used for numeric questions.
    // We'll reuse numberMin/numberMax for short_text min/max characters (as requested).
    // If backend expects different fields for text length, change here accordingly.
    const options =
      Array.isArray(q.options) && q.options.length
        ? q.options.map((opt) =>
            typeof opt === "string"
              ? { id: null, text: opt }
              : { id: opt.id ?? null, text: opt.text ?? opt }
          )
        : [];

    return {
      id: q.id ?? null,
      text: q.text ?? "",
      required: !!q.required,
      type: q.type ?? "short_text",
      imageUrl: q.imageUrl ?? null,
      numberMin: typeof q.numberMin === "number" ? q.numberMin : 0,
      numberMax: typeof q.numberMax === "number" ? q.numberMax : 0,
      numberStep: typeof q.numberStep === "number" ? q.numberStep : 1,
      minRequiredAnswers: q.minRequiredAnswers ?? null,
      maxAllowedAnswers: q.maxAllowedAnswers ?? null,
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
      ownerId,
      publicForm, // included; change key if backend requires different name
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
        // try parse server message
        let msg = `Server returned ${res.status}`;
        try {
          const json = await res.json();
          msg = json.message || JSON.stringify(json);
        } catch {
          try {
            msg = await res.text();
          } catch {}
        }
        throw new Error(msg);
      }

      const created = await res.json();
      alert("Form created successfully");
      // navigate to edit or forms list
      if (created && created.id) navigate(`/`);
      else navigate("/forms");
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
        publicForm={publicForm}
        setPublicForm={setPublicForm}
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
        <button onClick={addQuestion} className="px-4 py-2 border rounded">
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
          className="px-4 py-2 border rounded"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
