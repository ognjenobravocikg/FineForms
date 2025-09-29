export default function AuthHeader({ title, subtitle }) {
  return (
    <div className="text-center mb-8">
      <h1 className="text-3xl font-bold text-gray-800">{title}</h1>
      <p className="text-gray-600">{subtitle}</p>
    </div>
  );
}
