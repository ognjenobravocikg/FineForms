import { useState } from "react";
import FormHeader from "./FormHeader";
import QuestionCard from "./QuestionCard";
import { useNavigate } from "react-router-dom";

export default function FormBuilder() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [questions, setQuestions] = useState([]);

  const navigate = useNavigate();

  const addQuestion = () => {
    setQuestions([
      ...questions,
      { text: "", image: null, type: "text", options: [] },
    ]);
  };

  const updateQuestion = (index, newQuestion) => {
    const updated = [...questions];
    updated[index] = newQuestion;
    setQuestions(updated);
  };

  const removeQuestion = (index) => {
    setQuestions(questions.filter((_, i) => i !== index));
  };

  const handleSubmit = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("You must be logged in to create a form.");
      return;
    }

    const formData = {
      title,
      description,
      questions,
    };

    console.log("Sending to backend:", formData);

    try {
      const response = await fetch("http://localhost:8080/api/form", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`, // Add the token here
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        alert("Form saved successfully!");
        // Optionally, navigate to another page
        // navigate("/dashboard");
      } else {
        const errData = await response.json();
        alert(errData.message || "Error saving form.");
      }
    } catch (err) {
      console.error("Error:", err);
      alert("Could not connect to backend.");
    }
  };

  return (
    <div className="max-w-3xl mx-auto p-6">
      {/* Form Title + Description */}
      <FormHeader
        title={title}
        setTitle={setTitle}
        description={description}
        setDescription={setDescription}
      />

      {/* All Questions */}
      {questions.map((q, index) => (
        <QuestionCard
          key={index}
          question={q}
          updateQuestion={(newQ) => updateQuestion(index, newQ)}
          removeQuestion={() => removeQuestion(index)}
        />
      ))}

      {/* Buttons */}
      <div className="flex space-x-4 mt-6">
        <button
          onClick={addQuestion}
          className="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition"
        >
          + Add Question
        </button>
        <button
          onClick={handleSubmit}
          className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          Save Form
        </button>
      </div>
    </div>
  );
}
