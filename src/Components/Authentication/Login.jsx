import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    const loginData = { email, password };

    try {
      const response = await fetch("http://localhost:8080/api/users/login/", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginData),
      });

      if (response.ok) {
        const data = await response.json();

        // ✅ Save JWT token
        localStorage.setItem("token", data.token);

        // Save user info if backend sends it
        if (data.user) {
          localStorage.setItem("user", JSON.stringify(data.user));
        }

        alert("Login successful!");
        navigate("/forms");
      } else {
        alert("Invalid email or password.");
      }
    } catch (err) {
      console.error("Error:", err);
      alert("Could not connect to backend.");
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* Left half - form */}
      <div className="w-1/2 flex items-center justify-center bg-white">
        <form
          onSubmit={handleLogin}
          className="w-3/4 max-w-md p-8 bg-white shadow-lg rounded-lg"
        >
          <div className="flex justify-center pb-2">
            <img src="logo.png" className="h-6" alt="Logo" />
          </div>
          <h2 className="text-3xl font-bold mb-6 text-gray-800">Login</h2>

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
            className="w-full p-3 mb-6 border rounded-lg focus:ring-2 focus:ring-blue-500"
            required
          />

          <button
            type="submit"
            className="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            Log In
          </button>

          <div className="flex justify-center pt-3">
            <button
              type="button"
              onClick={() => navigate("/register")}
              className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition text-sm"
            >
              Don't have an Account? Register!
            </button>
          </div>
        </form>
      </div>

      {/* Right half - splash */}
      <div className="w-1/2 flex items-center justify-center bg-gradient-to-br from-blue-400 to-purple-600 text-white">
        <h1 className="text-4xl font-bold text-center px-8">
          Welcome back to FineForms
        </h1>
      </div>
    </div>
  );
}
