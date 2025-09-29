import { ChartBar, Edit3, Share2 } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";

export default function Home() {
  const storedUserId = localStorage.getItem("userId");
  const navigate = useNavigate();
  const [selectedFeature, setSelectedFeature] = useState(1);

  const handleGetStarted = () => {
    navigate("/forms");
  };

  const features = [
    {
      id: 1,
      icon: <Edit3 className="w-12 h-12 text-indigo-600 mb-4" />,
      title: "Edit to Fit Your Needs",
      short:
        "Customize forms with fields, styles, and logic to match exactly what you need.",
      image: "/business-7785093_1280.png",
      long: "Our drag-and-drop editor allows you to create forms in minutes. Whether you need surveys, registration forms, or feedback collection, you can design every detail without writing a single line of code.",
    },
    {
      id: 2,
      icon: <ChartBar className="w-12 h-12 text-indigo-600 mb-4" />,
      title: "Make Decisions with Data",
      short:
        "Collect responses in real time and gain insights with clean analytics.",
      image: "/550.jpg",
      long: "With powerful analytics built-in, you can track responses in real time, visualize trends, and export data seamlessly to make better business decisions.",
    },
    {
      id: 3,
      icon: <Share2 className="w-12 h-12 text-indigo-600 mb-4" />,
      title: "Share Anywhere",
      short:
        "Send links, embed in websites, or integrate with tools you already use.",
      image: "/Sandy_Tech-13_Single-06.jpg",
      long: "Easily distribute your forms by sharing links, embedding in websites, or integrating with tools like Slack, Google Sheets, or CRMs you already use.",
    },
  ];

  const activeFeature = features.find((f) => f.id === selectedFeature);

  return (
    <div>
      {/* Hero Section */}
      <section className="flex flex-col items-center justify-center text-center py-20 px-6 bg-animated-gradient dark:bg-gray-900">
        <h1 className="text-4xl md:text-6xl font-bold text-white dark:text-white mb-6">
          Create & Share Forms Easily
        </h1>
        <p className="text-lg md:text-xl text-white mb-8 max-w-2xl">
          FineForms lets you design, customize, and share forms effortlessly —
          whether for surveys, feedback, or data collection.
        </p>
        <div className="flex space-x-4">
          <button
            onClick={handleGetStarted}
            className="px-6 py-3 bg-indigo-600 text-white rounded-lg shadow hover:bg-indigo-700 transition"
          >
            Get Started
          </button>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-6 bg-gray-50 dark:bg-gray-950">
        <div className="max-w-6xl mx-auto text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white">
            Why FineForms?
          </h2>
          <p className="mt-4 text-gray-600 dark:text-gray-400">
            Powerful tools to help you collect and use data effectively.
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-10 max-w-6xl mx-auto">
          {features.map((feature) => (
            <div
              key={feature.id}
              onClick={() => setSelectedFeature(feature.id)}
              className={`cursor-pointer flex flex-col items-center text-center p-6 rounded-xl shadow transition ${
                selectedFeature === feature.id
                  ? "bg-blue-50 dark:bg-gray-700 ring-2 ring-blue-600"
                  : "bg-gray-50 dark:bg-gray-800 hover:shadow-lg"
              }`}
            >
              {feature.icon}
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
                {feature.title}
              </h3>
              <p className="mt-2 text-gray-600 dark:text-gray-400">
                {feature.short}
              </p>
            </div>
          ))}
        </div>

        {/* Animated Feature Detail — */}
        <div className="max-w-7xl mx-auto mt-16 relative min-h-[350px]">
          <AnimatePresence mode="wait">
            {activeFeature && (
              <motion.div
                key={activeFeature.id}
                initial={{ opacity: 0, x: 50 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -50 }}
                transition={{ duration: 0.2 }}
                className="absolute w-full flex flex-col md:flex-row items-center gap-10 bg-white dark:bg-gray-800 rounded-xl shadow-lg p-8"
              >
                {/* 👇 FIXED IMAGE SIZE — consistent height, crops to fit */}
                <div className="w-full md:w-1/2 h-80 rounded-lg overflow-hidden bg-gray-200 dark:bg-gray-700">
                  <img
                    src={activeFeature.image}
                    alt={activeFeature.title}
                    className="w-full h-full object-cover"
                    onError={(e) => {
                      e.target.src =
                        "https://placehold.co/600x400/f0f0f0/333333?text=Feature+Image";
                    }}
                  />
                </div>
                <div className="w-full md:w-1/2">
                  <h3 className="text-4xl font-bold text-gray-900 dark:text-white mb-4">
                    {activeFeature.title}
                  </h3>
                  <p className="text-gray-600 dark:text-gray-300 text-lg leading-relaxed">
                    {activeFeature.long}
                  </p>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </section>
    </div>
  );
}
