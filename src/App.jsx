import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Navbar from "./Components/Navbar.jsx";
import Footer from "./Components/Footer.jsx";
import Home from "./Components/Home.jsx";
import FormBuilder from "./Components/Forms/FormBuilder.jsx";
import Login from "./Components/Authentication/Login.jsx";
import Register from "./Components/Authentication/Register.jsx";
import About from "./Components/About/About.jsx";
import Profile from "./Components/Profile/Profile.jsx";
import FormsPage from "./Components/My-Forms/FormsPage.jsx";

/* ------------------ Layouts ------------------ */

function MainLayout({ children }) {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-grow">{children}</main>
      <Footer />
    </div>
  );
}

function AuthLayout({ children }) {
  return <div>{children}</div>;
}

/* ------------------ App ------------------ */

export default function App() {
  return (
    <Router>
      <Routes>
        {/* Main layout pages */}
        <Route
          path="/"
          element={
            <MainLayout>
              <Home />
            </MainLayout>
          }
        />
        <Route
          path="/forms"
          element={
            <MainLayout>
              <FormBuilder />
            </MainLayout>
          }
        />
        <Route
          path="/about"
          element={
            <MainLayout>
              <About />
            </MainLayout>
          }
        />
        <Route
          path="/profile"
          element={
            <MainLayout>
              <Profile />
            </MainLayout>
          }
        />
        <Route
          path="/my-forms"
          element={
            <MainLayout>
              <FormsPage />
            </MainLayout>
          }
        />

        {/* Auth layout pages */}
        <Route
          path="/login"
          element={
            <AuthLayout>
              <Login />
            </AuthLayout>
          }
        />
        <Route
          path="/register"
          element={
            <AuthLayout>
              <Register />
            </AuthLayout>
          }
        />
      </Routes>
    </Router>
  );
}
