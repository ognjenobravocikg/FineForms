import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleRegister = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      alert("Passwords do not match!");
      return;
    }

    const registerData = { username, email, password };

    console.log("Register attempt:", registerData);

    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(registerData),
      });

      if (response.ok) {
        alert("Account created successfully!");
      } else {
        alert("Error creating account.");
      }
    } catch (err) {
      console.error("Error:", err);
      alert("Could not connect to backend.");
    }
  };

  const navigate = useNavigate();

  const handleLogin = () => {
    navigate("/login");
  };

  return (
    <div className="min-h-screen flex">
      {/* Left half - form */}
      <div className="w-1/2 flex items-center justify-center bg-white">
        <form
          onSubmit={handleRegister}
          className="w-3/4 max-w-md p-8 bg-white shadow-lg rounded-lg"
        >
          <div className="flex justify-center pb-2">
            <img src="logo_green.png" class="h-6"></img>
          </div>

          <h2 className="text-3xl font-bold mb-6 text-gray-800">Register</h2>

          <input
            type="text"
            placeholder="User Name"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full p-3 mb-4 border rounded-lg focus:ring-2 focus:ring-blue-500"
            required
          />

          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-3 mb-4 border rounded-lg focus:ring-2 focus:ring-blue-500"
            required
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-3 mb-4 border rounded-lg focus:ring-2 focus:ring-blue-500"
            required
          />

          <input
            type="password"
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            className="w-full p-3 mb-6 border rounded-lg focus:ring-2 focus:ring-blue-500"
            required
          />

          <button
            type="submit"
            className="w-full py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
          >
            Register
          </button>

          <div className="flex justify-center pt-3">
            <button
              onClick={handleLogin}
              className="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition text-sm"
            >
              Already have an Account? Log in!
            </button>
          </div>
        </form>
      </div>

      {/* Right half - splash */}
      <div className="w-1/2 flex items-center justify-center bg-gradient-to-br from-green-400 to-teal-600 text-white">
        <h1 className="text-4xl font-bold text-center px-8">
          Start building smarter forms with FineForms
        </h1>
      </div>
    </div>
  );
}
