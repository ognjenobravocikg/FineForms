import React from "react";

export default function AnswerCard({ question, value, onChange }) {
  const renderInput = () => {
    switch (question.type) {
      case "SHORT_ANSWER":
        return (
          <input
            type="text"
            value={value || ""}
            onChange={(e) => onChange(e.target.value)}
            className="w-full border rounded px-3 py-2 focus:ring-2 focus:ring-blue-400"
          />
        );

      case "LONG_ANSWER":
        return (
          <textarea
            value={value || ""}
            onChange={(e) => onChange(e.target.value)}
            rows={4}
            className="w-full border rounded px-3 py-2 focus:ring-2 focus:ring-blue-400"
          />
        );

      case "NUMBER":
        return (
          <input
            type="number"
            value={value || ""}
            onChange={(e) => onChange(Number(e.target.value))}
            className="w-full border rounded px-3 py-2 focus:ring-2 focus:ring-blue-400"
          />
        );

      case "RANGE":
      case "SCALE":
        return (
          <div>
            <input
              type="range"
              value={value || question.numberMin || 0}
              min={question.numberMin ?? 0}
              max={question.numberMax ?? 10}
              step={question.numberStep ?? 1}
              onChange={(e) => onChange(Number(e.target.value))}
              className="w-full"
            />
            <div className="text-right text-sm text-gray-500">
              {value ?? question.numberMin ?? 0}
            </div>
          </div>
        );

      case "SINGLE_CHOICE":
      case "RADIO":
        return (
          <div className="space-y-1">
            {question.options?.map((opt, idx) => (
              <label key={idx} className="flex items-center gap-2">
                <input
                  type="radio"
                  name={`question-${question.id}`}
                  value={opt.text}
                  checked={value === opt.text}
                  onChange={() => onChange(opt.text)}
                  className="accent-blue-500"
                />
                <span>{opt.text}</span>
              </label>
            ))}
          </div>
        );

      case "MULTIPLE_CHOICE":
      case "CHECKBOX":
        return (
          <div className="space-y-1">
            {question.options?.map((opt, idx) => (
              <label key={idx} className="flex items-center gap-2">
                <input
                  type="checkbox"
                  value={opt.text}
                  checked={
                    Array.isArray(value) ? value.includes(opt.text) : false
                  }
                  onChange={(e) => {
                    const checked = e.target.checked;
                    const arr = Array.isArray(value) ? [...value] : [];
                    if (checked) arr.push(opt.text);
                    else arr.splice(arr.indexOf(opt.text), 1);
                    onChange(arr);
                  }}
                  className="accent-blue-500"
                />
                <span>{opt.text}</span>
              </label>
            ))}
          </div>
        );

      default:
        return (
          <p className="text-red-500">
            Unsupported question type: {question.type}
          </p>
        );
    }
  };

  return (
    <div className="border rounded-xl p-5 shadow-sm bg-white hover:shadow-md transition">
      <p className="font-semibold mb-3 text-gray-800">
        {question.text}{" "}
        {question.required && <span className="text-red-500">*</span>}
      </p>
      {renderInput()}
    </div>
  );
}
