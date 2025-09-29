// CollaboratorsModal.jsx
import React, { useMemo, useState } from "react";

export default function CollaboratorsModal({
  form,
  allUsers = [],
  onClose,
  onAdd,
  onRemove,
}) {
  const [query, setQuery] = useState("");

  // basic filtering on client side
  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return allUsers;
    return allUsers.filter(
      (u) =>
        (u.firstName && u.firstName.toLowerCase().includes(q)) ||
        (u.lastName && u.lastName.toLowerCase().includes(q)) ||
        (u.email && u.email.toLowerCase().includes(q))
    );
  }, [allUsers, query]);

  const collabIds = new Set(
    (form.collaborators || []).map((c) => String(c.id))
  );

  return (
    <div className="fixed inset-0 backdrop-blur-sm bg-black/30 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-2xl font-bold text-gray-800">
            Collaborators for: {form.title}
          </h2>
          <button
            onClick={onClose}
            className="text-sm text-gray-500 hover:text-gray-700"
          >
            ✕
          </button>
        </div>

        {/* Current collaborators */}
        <div className="mb-4">
          <h3 className="text-lg font-semibold text-gray-700 mb-2">
            Current Collaborators
          </h3>
          {form.collaborators && form.collaborators.length > 0 ? (
            <div className="space-y-2">
              {form.collaborators.map((c) => (
                <div
                  key={c.id}
                  className="flex justify-between items-center border p-2 rounded-lg bg-gray-50"
                >
                  <div>
                    <div className="font-medium">
                      {c.firstName} {c.lastName}
                    </div>
                    <div className="text-sm text-gray-500">{c.email}</div>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="text-xs text-gray-600">
                      {c.role || "USER"}
                    </div>
                    <button
                      onClick={() => onRemove(c.id)}
                      className="px-3 py-1 text-sm bg-red-500 text-white rounded-lg hover:bg-red-600"
                    >
                      Remove
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-500 text-sm italic">
              No collaborators yet.
            </p>
          )}
        </div>

        {/* Add collaborators */}
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search users by name or email..."
          className="w-full border px-4 py-2 rounded-lg mb-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />

        <div className="max-h-48 overflow-y-auto space-y-2">
          {filtered.map((u) => {
            const already = collabIds.has(String(u.id));
            return (
              <div
                key={u.id}
                className="flex justify-between items-center border p-3 rounded-lg hover:bg-gray-50"
              >
                <div>
                  <p className="font-semibold text-gray-800">
                    {u.firstName} {u.lastName}
                  </p>
                  <p className="text-sm text-gray-500">{u.email}</p>
                </div>
                <button
                  onClick={() => onAdd(u.id)}
                  disabled={already}
                  className={`px-4 py-2 rounded-lg text-sm transition ${
                    already
                      ? "bg-gray-200 text-gray-600 cursor-not-allowed"
                      : "border border-gray-300 text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  {already ? "Added" : "Add"}
                </button>
              </div>
            );
          })}
          {filtered.length === 0 && (
            <p className="text-gray-500 text-center">No users found</p>
          )}
        </div>

        <div className="mt-6 flex justify-end gap-3">
          <button
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
