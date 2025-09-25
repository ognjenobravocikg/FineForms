// QuestionCard.jsx
import React, { useState } from "react";

export default function QuestionCard({
  question,
  updateQuestion,
  removeQuestion,
  index,
}) {
  // question shape expected by parent:
  // {
  //   id: null,
  //   text: "",
  //   required: false,
  //   type: "short_text" | "long_text" | "multi_choice",
  //   imageUrl: null,
  //   numberMin: 0, numberMax: 0, numberStep: 1,
  //   minRequiredAnswers: null, maxAllowedAnswers: null,
  //   options: []
  // }

  const [newOptionText, setNewOptionText] = useState("");

  const setField = (patch) => updateQuestion({ ...question, ...patch });

  const handleFile = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onloadend = () => {
      setField({ imageUrl: reader.result });
    };
    reader.readAsDataURL(file);
  };

  const addOption = () => {
    if (!newOptionText.trim()) return;
    const opts = Array.isArray(question.options)
      ? question.options.slice()
      : [];
    opts.push(newOptionText.trim());
    setField({ options: opts });
    setNewOptionText("");
  };

  const updateOption = (i, text) => {
    const opts = question.options ? question.options.slice() : [];
    opts[i] = text;
    setField({ options: opts });
  };

  const removeOptionAt = (i) => {
    const opts = question.options ? question.options.slice() : [];
    opts.splice(i, 1);
    setField({ options: opts });
  };

  return (
    <div className="bg-white shadow rounded-lg p-4 mb-4">
      <div className="flex items-start justify-between">
        <h4 className="font-semibold">Question {index + 1}</h4>
        <button
          onClick={removeQuestion}
          className="text-sm text-red-600 hover:underline"
          aria-label={`Remove question ${index + 1}`}
        >
          Delete
        </button>
      </div>

      <div className="mt-3 space-y-3">
        <input
          value={question.text}
          onChange={(e) => setField({ text: e.target.value })}
          placeholder="Question text..."
          className="w-full border rounded px-3 py-2"
        />

        <div className="flex items-center gap-3">
          <label className="text-sm text-gray-600">Type:</label>
          <select
            value={question.type}
            onChange={(e) => {
              const v = e.target.value;
              // reset options for non-multi choices
              setField({
                type: v,
                options: v === "multi_choice" ? question.options || [] : [],
              });
            }}
            className="border rounded px-2 py-1"
          >
            <option value="short_text">Short text</option>
            <option value="long_text">Long text</option>
            <option value="multi_choice">Multiple choice</option>
          </select>

          <label className="flex items-center gap-2 ml-4">
            <input
              type="checkbox"
              checked={!!question.required}
              onChange={(e) => setField({ required: e.target.checked })}
            />
            <span className="text-sm text-gray-700">Required</span>
          </label>
        </div>

        {/* short_text constraints */}
        {question.type === "short_text" && (
          <div className="flex gap-3 items-center">
            <div>
              <label className="text-xs text-gray-600">Min chars</label>
              <input
                type="number"
                min={0}
                value={question.numberMin ?? 0}
                onChange={(e) =>
                  setField({ numberMin: Number(e.target.value) })
                }
                className="w-28 border rounded px-2 py-1"
              />
            </div>
            <div>
              <label className="text-xs text-gray-600">Max chars</label>
              <input
                type="number"
                min={0}
                value={question.numberMax ?? 0}
                onChange={(e) =>
                  setField({ numberMax: Number(e.target.value) })
                }
                className="w-28 border rounded px-2 py-1"
              />
            </div>
          </div>
        )}

        {/* multi_choice options */}
        {question.type === "multi_choice" && (
          <div>
            <label className="text-sm font-medium">Options</label>
            <div className="space-y-2 mt-2">
              {(question.options || []).map((opt, i) => (
                <div key={i} className="flex items-center gap-2">
                  <input
                    value={typeof opt === "object" ? opt.text : opt}
                    onChange={(e) => updateOption(i, e.target.value)}
                    className="flex-1 border rounded px-2 py-1"
                  />
                  <button
                    onClick={() => removeOptionAt(i)}
                    className="px-2 py-1 bg-red-500 text-white rounded"
                    type="button"
                  >
                    Remove
                  </button>
                </div>
              ))}

              <div className="flex gap-2">
                <input
                  value={newOptionText}
                  onChange={(e) => setNewOptionText(e.target.value)}
                  placeholder="New option..."
                  className="flex-1 border rounded px-2 py-1"
                />
                <button
                  onClick={addOption}
                  className="px-3 py-1 bg-indigo-600 text-white rounded"
                >
                  Add
                </button>
              </div>
            </div>
          </div>
        )}

        {/* image upload */}
        <div>
          <label className="text-sm text-gray-600">Image (optional)</label>
          <div className="flex items-center gap-3 mt-2">
            <label className="px-3 py-1 bg-gray-200 rounded cursor-pointer">
              Choose image
              <input
                onChange={handleFile}
                type="file"
                accept="image/*"
                className="hidden"
              />
            </label>
            {question.imageUrl && (
              <img
                src={question.imageUrl}
                alt="preview"
                className="h-20 object-contain rounded border"
              />
            )}
            {question.imageUrl && (
              <button
                onClick={() => setField({ imageUrl: null })}
                className="text-sm text-red-600 hover:underline"
              >
                Remove
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
