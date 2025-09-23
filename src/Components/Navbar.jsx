import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="bg-white shadow w-[100%]">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        {/* Logo */}
        <h1 className="text-2xl font-bold text-indigo-600">FineForms</h1>

        {/* Navigation Links */}
        <ul className="flex gap-6 text-gray-700">
          <li>
            <Link to="/" className="hover:text-indigo-600 transition-colors">
              Home
            </Link>
          </li>
          <li>
            <Link
              to="/my-forms"
              className="hover:text-indigo-600 transition-colors"
            >
              My Forms
            </Link>
          </li>
          <li>
            <Link
              to="/about"
              className="hover:text-indigo-600 transition-colors"
            >
              About
            </Link>
          </li>
          <li>
            <Link
              to="/profile"
              className="hover:text-indigo-600 transition-colors"
            >
              Profile
            </Link>
          </li>
          <li>
            <Link
              to="/login"
              className="hover:text-indigo-600 transition-colors"
            >
              Log Out
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
}
