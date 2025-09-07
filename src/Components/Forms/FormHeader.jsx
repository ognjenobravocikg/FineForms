export default function FormHeader({
  title,
  setTitle,
  description,
  setDescription,
}) {
  return (
    <div className="bg-white dark:bg-gray-800 shadow p-6 rounded-lg mb-6">
      <input
        type="text"
        placeholder="Form Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        className="w-full text-2xl font-bold mb-3 p-2 border-b border-gray-300 dark:border-gray-600 focus:outline-none bg-transparent"
      />
      <textarea
        placeholder="Form Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        className="w-full text-gray-600 dark:text-gray-300 p-2 border-b border-gray-300 dark:border-gray-600 focus:outline-none bg-transparent"
      />
    </div>
  );
}
