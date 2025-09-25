// FormHeader.jsx
import React from "react";

export default function FormHeader({
  title,
  setTitle,
  description,
  setDescription,
  publicForm,
  setPublicForm,
}) {
  return (
    <div className="bg-white p-6 rounded-lg shadow mb-6">
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700">
          Form Title
        </label>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Enter form title"
          className="mt-1 w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700">
          Description
        </label>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Short description about the form"
          rows={3}
          className="mt-1 w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      <div className="flex items-center gap-3">
        <label className="flex items-center gap-2 cursor-pointer select-none">
          <input
            type="checkbox"
            checked={publicForm}
            onChange={(e) => setPublicForm(e.target.checked)}
            className="h-4 w-4"
          />
          <span className="text-sm text-gray-700">
            Allow guests to fill this form
          </span>
        </label>
        <span className="text-xs text-gray-400">
          {" "}
          (If enabled, the form can be opened via a public link)
        </span>
      </div>
    </div>
  );
}
