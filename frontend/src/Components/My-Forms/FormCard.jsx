import React from "react";

export default function FormCard({
  form,
  onEdit,
  onCollaborators,
  onViewButton,
  onDelete,
  extraActions,
}) {
  return (
    <div className="bg-gray-50 border rounded-lg p-6 shadow-sm hover:shadow-md transition md:hover:bg-indigo-50">
      <div className="flex items-start justify-between">
        <div>
          <h3 className="text-xl font-semibold text-gray-800">{form.title}</h3>
          <p className="text-gray-600 mt-1">{form.description}</p>
        </div>
      </div>

      <div className="flex gap-3 mt-4 flex-wrap">
        {onEdit && (
          <button
            onClick={onEdit}
            className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100"
          >
            View
          </button>
        )}

        {onViewButton && (
          <button
            onClick={onViewButton}
            className="px-4 py-2 border border-gray-300 text-gray-700 hover:bg-indigo-300 rounded-lg font-semibold py-2 px-4 rounded"
          >
            View All Responses
          </button>
        )}
      </div>
      {extraActions && <div className="mt-3">{extraActions}</div>}
    </div>
  );
}
