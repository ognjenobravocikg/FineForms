export default function Navbar() {
  return (
    <nav className="bg-white shadow w-[100%]">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        {/* Logo */}
        <h1 className="text-2xl font-bold text-indigo-600">FineForms</h1>

        {/* Navigation Links */}
        <ul className="flex gap-6 text-gray-700">
          <li>
            <a href="#" className="hover:text-indigo-600 transition-colors">
              Home
            </a>
          </li>
          <li>
            <a href="#" className="hover:text-indigo-600 transition-colors">
              My Forms
            </a>
          </li>
          <li>
            <a href="#" className="hover:text-indigo-600 transition-colors">
              About
            </a>
          </li>
        </ul>
      </div>
    </nav>
  );
}
