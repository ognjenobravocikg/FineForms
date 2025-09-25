// FormCard.jsx
import React from "react";

export default function FormCard({ form, onEdit, onCollaborators, onDelete }) {
  return (
    <div className="bg-gray-50 border rounded-lg p-6 shadow-sm hover:shadow-md transition">
      <div className="flex items-start justify-between">
        <div>
          <h3 className="text-xl font-semibold text-gray-800">{form.title}</h3>
          <p className="text-gray-600 mt-1">{form.description}</p>
        </div>

        <div className="flex flex-col items-end gap-2">
          <div className="text-sm text-gray-500">ID: {form.id}</div>
          <div className="text-xs text-gray-400">
            Owner:{" "}
            {String(
              form.ownerId ?? form.owner?.id ?? form.owner?.userId ?? "—"
            )}
          </div>
        </div>
      </div>

      <div className="flex gap-3 mt-4">
        <button
          onClick={onEdit}
          className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100"
        >
          Edit
        </button>

        <button
          onClick={onCollaborators}
          className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
        >
          Collaborators
        </button>

        <button
          onClick={onDelete}
          className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 ml-auto"
        >
          Delete
        </button>
      </div>
    </div>
  );
}
