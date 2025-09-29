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
            placeholder="Type your answer..."
            className="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none transition"
          />
        );

      case "LONG_ANSWER":
        return (
          <textarea
            value={value || ""}
            onChange={(e) => onChange(e.target.value)}
            rows={4}
            placeholder="Type your answer..."
            className="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none transition"
          />
        );

      case "NUMBER":
        return (
          <input
            type="number"
            value={value || ""}
            onChange={(e) => onChange(e.target.value)}
            placeholder="Enter a number"
            className="w-full border border-gray-300 rounded-xl px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none transition"
          />
        );

      case "RANGE":
      case "SCALE":
        return (
          <div className="flex items-center gap-4">
            <input
              type="range"
              min={question.numberMin ?? 0}
              max={question.numberMax ?? 10}
              step={question.numberStep ?? 1}
              value={value ?? question.numberMin ?? 0}
              onChange={(e) => onChange(Number(e.target.value))}
              className="w-full accent-blue-500 hover:accent-blue-600 transition"
            />
            <span className="w-12 text-right font-medium text-gray-700">
              {value ?? question.numberMin ?? 0}
            </span>
          </div>
        );

      case "SINGLE_CHOICE":
      case "RADIO":
        return (
          <div className="flex flex-col gap-2">
            {question.options?.map((opt, idx) => (
              <label
                key={idx}
                className={`flex items-center gap-3 p-2 rounded-xl border cursor-pointer transition 
                  ${
                    value === opt.text
                      ? "bg-blue-100 border-blue-400"
                      : "border-gray-300 hover:bg-gray-100"
                  }`}
              >
                <input
                  type="radio"
                  name={`question-${question.id}`}
                  value={opt.text}
                  checked={value === opt.text}
                  onChange={() => onChange(opt.text)}
                  className="accent-blue-500"
                />
                <span className="text-gray-800">{opt.text}</span>
              </label>
            ))}
          </div>
        );

      case "MULTIPLE_CHOICE":
      case "CHECKBOX":
        return (
          <div className="flex flex-col gap-2">
            {question.options?.map((opt, idx) => (
              <label
                key={idx}
                className={`flex items-center gap-3 p-2 rounded-xl border cursor-pointer transition
                  ${
                    Array.isArray(value) && value.includes(opt.text)
                      ? "bg-blue-100 border-blue-400"
                      : "border-gray-300 hover:bg-gray-100"
                  }`}
              >
                <input
                  type="checkbox"
                  value={opt.text}
                  checked={Array.isArray(value) && value.includes(opt.text)}
                  onChange={(e) => {
                    const checked = e.target.checked;
                    let newVal = Array.isArray(value) ? [...value] : [];
                    if (checked) newVal.push(opt.text);
                    else newVal = newVal.filter((v) => v !== opt.text);
                    onChange(newVal);
                  }}
                  className="accent-blue-500"
                />
                <span className="text-gray-800">{opt.text}</span>
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
    <div className="bg-white p-6 rounded-2xl shadow-md hover:shadow-lg transition space-y-2">
      <p className="font-semibold text-gray-800">
        {question.text}{" "}
        {question.required && <span className="text-red-500">*</span>}
      </p>
      {renderInput()}
    </div>
  );
}
