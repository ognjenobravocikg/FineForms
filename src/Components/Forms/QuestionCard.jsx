import React, { useState } from "react";

export default function QuestionCard({
  question,
  updateQuestion,
  removeQuestion,
  index,
}) {
  const [newOptionText, setNewOptionText] = useState("");

  const setField = (patch) => updateQuestion({ ...question, ...patch });

  const handleFile = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onloadend = () => setField({ imageUrl: reader.result });
    reader.readAsDataURL(file);
  };

  const addOption = () => {
    if (!newOptionText.trim()) return;
    const opts = Array.isArray(question.options)
      ? question.options.slice()
      : [];
    if (question.type === "SINGLE_CHOICE" && opts.length >= 1) return;
    opts.push({ text: newOptionText.trim(), correct: false, imageUrl: null });
    setField({ options: opts });
    setNewOptionText("");
  };

  const updateOption = (i, text) => {
    const opts = question.options ? question.options.slice() : [];
    opts[i] = { ...opts[i], text };
    setField({ options: opts });
  };

  const removeOptionAt = (i) => {
    const opts = question.options ? question.options.slice() : [];
    opts.splice(i, 1);
    setField({ options: opts });
  };

  return (
    <div className="bg-white shadow-md rounded-lg p-5 mb-5 md:hover:bg-indigo-50">
      <div className="flex justify-between items-start">
        <button
          onClick={removeQuestion}
          className="text-red-600 hover:underline text-sm"
        >
          Delete
        </button>
      </div>

      <div className="mt-4 space-y-4">
        <input
          value={question.text}
          onChange={(e) => setField({ text: e.target.value })}
          placeholder="Question text..."
          className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
        />

        <div className="flex flex-wrap items-center gap-4">
          <label className="text-gray-600 text-sm">Type:</label>
          <select
            value={question.type}
            onChange={(e) => {
              const v = e.target.value;
              setField({
                type: v,
                options:
                  v === "MULTIPLE_CHOICE" || v === "SINGLE_CHOICE"
                    ? question.options || []
                    : [],
              });
            }}
            className="border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
          >
            <option value="SHORT_ANSWER">Short answer</option>
            <option value="LONG_ANSWER">Long answer</option>
            <option value="MULTIPLE_CHOICE">Multiple choice</option>
            <option value="SINGLE_CHOICE">Single choice</option>
            <option value="NUMBER">Number</option>
            <option value="RANGE">Range</option>
            <option value="DATE">Date</option>
            <option value="IMAGE">Image upload</option>
          </select>

          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={!!question.required}
              onChange={(e) => setField({ required: e.target.checked })}
              className="h-4 w-4"
            />
            <span className="text-gray-700 text-sm">Required</span>
          </label>
        </div>

        {/* SHORT_ANSWER only min/max chars */}
        {question.type === "SHORT_ANSWER" && (
          <div className="flex gap-3">
            <div>
              <label className="text-gray-500 text-xs">Min chars</label>
              <input
                type="number"
                min={0}
                value={question.numberMin ?? ""}
                onChange={(e) =>
                  setField({ numberMin: Number(e.target.value) })
                }
                className="w-24 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
            <div>
              <label className="text-gray-500 text-xs">Max chars</label>
              <input
                type="number"
                min={0}
                value={question.numberMax ?? ""}
                onChange={(e) =>
                  setField({ numberMax: Number(e.target.value) })
                }
                className="w-24 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
          </div>
        )}

        {/* MULTIPLE_CHOICE / SINGLE_CHOICE */}
        {(question.type === "MULTIPLE_CHOICE" ||
          question.type === "SINGLE_CHOICE") && (
          <div>
            <label className="font-medium text-gray-700 text-sm">Options</label>
            <div className="space-y-2 mt-2">
              {(question.options || []).map((opt, i) => (
                <div key={i} className="flex items-center gap-2">
                  <input
                    value={opt.text}
                    onChange={(e) => updateOption(i, e.target.value)}
                    className="flex-1 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                  />
                  <button
                    type="button"
                    onClick={() => removeOptionAt(i)}
                    className="px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                  >
                    Remove
                  </button>
                </div>
              ))}
              <div className="flex gap-2">
                <input
                  value={newOptionText}
                  onChange={(e) => setNewOptionText(e.target.value)}
                  placeholder={
                    question.type === "SINGLE_CHOICE"
                      ? "Option..."
                      : "New option..."
                  }
                  className="flex-1 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                  disabled={
                    question.type === "SINGLE_CHOICE" &&
                    question.options.length >= 1
                  }
                />
                <button
                  onClick={addOption}
                  className="px-3 py-1 bg-indigo-600 text-white rounded hover:bg-indigo-700 disabled:opacity-50"
                  disabled={
                    question.type === "SINGLE_CHOICE" &&
                    question.options.length >= 1
                  }
                >
                  Add
                </button>
              </div>
            </div>
          </div>
        )}

        {/* NUMBER only min/max, no step */}
        {question.type === "NUMBER" && (
          <div className="flex gap-3">
            <div>
              <label className="text-gray-500 text-xs">Min</label>
              <input
                type="number"
                value={question.numberMin ?? ""}
                onChange={(e) =>
                  setField({ numberMin: Number(e.target.value) })
                }
                className="w-24 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
            <div>
              <label className="text-gray-500 text-xs">Max</label>
              <input
                type="number"
                value={question.numberMax ?? ""}
                onChange={(e) =>
                  setField({ numberMax: Number(e.target.value) })
                }
                className="w-24 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
          </div>
        )}

        {/* RANGE */}
        {question.type === "RANGE" && (
          <div className="flex gap-3">
            <div>
              <label className="text-gray-500 text-xs">Min</label>
              <input
                type="number"
                value={question.numberMin ?? ""}
                onChange={(e) =>
                  setField({ numberMin: Number(e.target.value) })
                }
                className="w-24 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
            <div>
              <label className="text-gray-500 text-xs">Max</label>
              <input
                type="number"
                value={question.numberMax ?? ""}
                onChange={(e) =>
                  setField({ numberMax: Number(e.target.value) })
                }
                className="w-24 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
            <div>
              <label className="text-gray-500 text-xs">Step</label>
              <input
                type="number"
                value={question.numberStep ?? 1}
                onChange={(e) =>
                  setField({ numberStep: Number(e.target.value) })
                }
                className="w-20 border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
          </div>
        )}

        {/* DATE */}
        {question.type === "DATE" && (
          <div className="flex gap-3">
            <div>
              <label className="text-gray-500 text-xs">Earliest</label>
              <input
                type="date"
                value={question.minDate || ""}
                onChange={(e) => setField({ minDate: e.target.value })}
                className="border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
            <div>
              <label className="text-gray-500 text-xs">Latest</label>
              <input
                type="date"
                value={question.maxDate || ""}
                onChange={(e) => setField({ maxDate: e.target.value })}
                className="border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>
          </div>
        )}

        {/* IMAGE */}
        {question.type === "IMAGE" && (
          <div>
            <label className="text-gray-600 text-sm">Upload Image</label>
            <div className="flex items-center gap-3 mt-2">
              <label className="px-3 py-1 bg-gray-200 rounded cursor-pointer hover:bg-gray-300">
                Choose
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleFile}
                  className="hidden"
                />
              </label>
              {question.imageUrl && (
                <>
                  <img
                    src={question.imageUrl}
                    alt="preview"
                    className="h-20 object-contain rounded border"
                  />
                  <button
                    onClick={() => setField({ imageUrl: null })}
                    className="text-red-600 hover:underline text-sm"
                  >
                    Remove
                  </button>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
