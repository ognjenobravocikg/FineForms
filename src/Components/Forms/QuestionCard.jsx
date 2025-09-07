import { useState } from "react";

export default function QuestionCard({
  question,
  updateQuestion,
  removeQuestion,
}) {
  const { text, type, options, required } = question;
  const [newOption, setNewOption] = useState("");

  const handleAddOption = () => {
    if (newOption.trim() === "") return;
    updateQuestion({ ...question, options: [...options, newOption] });
    setNewOption("");
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onloadend = () => {
      updateQuestion({ ...question, image: reader.result }); // base64 string
    };
    reader.readAsDataURL(file);
  };

  return (
    <div className="bg-gray-100 shadow p-4 rounded-lg mb-4">
      {/* Question Text */}
      <input
        className="w-full font-semibold border-b mb-2 outline-none"
        placeholder="Question text..."
        value={text}
        onChange={(e) => updateQuestion({ ...question, text: e.target.value })}
      />

      {/* Question Type Selector */}
      <select
        value={type}
        onChange={(e) =>
          updateQuestion({ ...question, type: e.target.value, options: [] })
        }
        className="border rounded px-2 py-1 mb-3 mt-2"
      >
        <option value="text">Text Answer</option>
        <option value="multiple">Multiple Choice</option>
        <option value="dropdown">Dropdown</option>
      </select>

      {/* Render Based on Type */}
      {type === "text" && (
        <input
          type="text"
          disabled
          placeholder="User will type an answer..."
          className="w-full border px-2 py-1 rounded text-gray-500"
        />
      )}

      {type === "multiple" && (
        <div>
          {options.map((opt, i) => (
            <div key={i} className="flex items-center mb-1">
              <input type="checkbox" disabled className="mr-2" />
              <span>{opt}</span>
            </div>
          ))}
          <div className="flex mt-2">
            <input
              value={newOption}
              onChange={(e) => setNewOption(e.target.value)}
              className="flex-grow border px-2 py-1 rounded-l"
              placeholder="Add option"
            />
            <button
              onClick={handleAddOption}
              className="bg-blue-600 text-white px-3 rounded-r hover:bg-blue-700"
            >
              +
            </button>
          </div>
        </div>
      )}

      {type === "dropdown" && (
        <div>
          {options.map((opt, i) => (
            <div
              key={i}
              className="flex items-center mb-1 pl-3 border border-gray-400"
            >
              <span>{opt}</span>
            </div>
          ))}
          <div className="flex mt-2">
            <input
              value={newOption}
              onChange={(e) => setNewOption(e.target.value)}
              className="flex-grow border px-2 py-1 rounded-l"
              placeholder="Add option"
            />
            <button
              onClick={handleAddOption}
              className="bg-blue-600 text-white px-3 rounded-r hover:bg-blue-700"
            >
              +
            </button>
          </div>
        </div>
      )}

      {/* Show Uploaded Image */}
      {question.image && (
        <img
          src={question.image}
          alt="Question"
          className="h-32 object-contain mb-3 pt-3"
        />
      )}

      {/* Actions Row */}
      <div className="flex items-center justify-between pt-3">
        {/* Upload + Delete */}
        <div className="flex items-center space-x-4">
          <label className="cursor-pointer px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">
            Upload Image
            <input
              type="file"
              accept="image/*"
              onChange={handleImageUpload}
              className="hidden"
            />
          </label>
          <button
            onClick={removeQuestion}
            className="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition"
          >
            Delete Question
          </button>
        </div>

        {/* Required Toggle */}
        <div className="flex items-center space-x-2">
          <label htmlFor="required" className="text-sm text-gray-700">
            Required
          </label>
          <input
            type="checkbox"
            checked={required || false}
            onChange={(e) =>
              updateQuestion({ ...question, required: e.target.checked })
            }
            className="w-4 h-4 text-blue-600 border-gray-300 rounded"
          />
        </div>
      </div>
    </div>
  );
}
