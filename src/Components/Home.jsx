import { ChartBar, Edit3, Share2 } from "lucide-react";

export default function Home() {
  return (
    <div>
      {/* Hero Section */}
      <section className="flex flex-col items-center justify-center text-center py-20 px-6 bg-white dark:bg-gray-900">
        <h1 className="text-4xl md:text-6xl font-bold text-gray-900 dark:text-white mb-6">
          Create & Share Forms Easily
        </h1>
        <p className="text-lg md:text-xl text-gray-600 dark:text-gray-300 mb-8 max-w-2xl">
          FineForms lets you design, customize, and share forms effortlessly —
          whether for surveys, feedback, or data collection.
        </p>
        <div className="flex space-x-4">
          <button className="px-6 py-3 bg-blue-600 text-white rounded-lg shadow hover:bg-blue-700 transition">
            Get Started
          </button>
          <button className="px-6 py-3 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition">
            Learn More
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
          {/* Feature 1 */}
          <div className="flex flex-col items-center text-center p-6 bg-gray-50 dark:bg-gray-800 rounded-xl shadow hover:shadow-lg transition">
            <Edit3 className="w-12 h-12 text-blue-600 mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
              Edit to Fit Your Needs
            </h3>
            <p className="mt-2 text-gray-600 dark:text-gray-400">
              Customize forms with fields, styles, and logic to match exactly
              what you need.
            </p>
          </div>

          {/* Feature 2 */}
          <div className="flex flex-col items-center text-center p-6 bg-gray-50 dark:bg-gray-800 rounded-xl shadow hover:shadow-lg transition">
            <ChartBar className="w-12 h-12 text-blue-600 mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
              Make Decisions with Data
            </h3>
            <p className="mt-2 text-gray-600 dark:text-gray-400">
              Collect responses in real time and gain insights with clean
              analytics.
            </p>
          </div>

          {/* Feature 3 */}
          <div className="flex flex-col items-center text-center p-6 bg-gray-50 dark:bg-gray-800 rounded-xl shadow hover:shadow-lg transition">
            <Share2 className="w-12 h-12 text-blue-600 mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
              Share Anywhere
            </h3>
            <p className="mt-2 text-gray-600 dark:text-gray-400">
              Send links, embed in websites, or integrate with tools you already
              use.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}
