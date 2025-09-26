import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

export default function Register() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      setError("Passwords do not match!");
      return;
    }

    const registerData = { email, password, firstName, lastName };

    try {
      const response = await fetch("http://localhost:8080/api/users/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(registerData),
      });

      if (response.status === 201) {
        const data = await response.json();

        // if backend sends back a token here, save it
        if (data.token) {
          localStorage.setItem("token", data.token);
          const decoded = jwtDecode(data.token);
          console.log(decoded);
          const userId = decoded.id;
          console.log(userId);
          localStorage.setItem("userId", userId);
        }

        navigate("/");
        setSuccess(
          `User ${jwtDecode(data.token).sub} registered successfully!`
        );

        setError("");
        navigate("/");
      } else if (response.status === 409) {
        const errData = await response.json();
        setError(errData.message || "User already exists!");
      } else {
        setError("Unexpected error occurred!");
      }
    } catch (err) {}
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
            <img src="logo_green.png" className="h-6" alt="Logo" />
          </div>

          <h2 className="text-3xl font-bold mb-6 text-gray-800">Register</h2>

          {success && <p className="text-green-600 mb-4">{success}</p>}
          {error && <p className="text-red-600 mb-4">{error}</p>}

          <input
            type="text"
            placeholder="First Name"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            className="w-full p-3 mb-4 border rounded-lg focus:ring-2 focus:ring-blue-500"
            required
          />

          <input
            type="text"
            placeholder="Last Name"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
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
              type="button"
              onClick={() => navigate("/login")}
              className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition text-sm"
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
