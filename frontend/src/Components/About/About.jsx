export default function About() {
  const team = [
    {
      name: "Ognjen Obradović",
      role: "Frontend Developer",
      github: "ognjenobravocikg",
    },
    {
      name: "Aleksandar Djokić",
      role: "Backend Engineer/Joker",
      github: "caojasamalex",
    },
    {
      name: "Janko Jakovljević",
      role: "Backend Engineer",
      github: "K3nza2",
    },
    {
      name: "Arsenije Jokić",
      role: "Testing, Project Management",
      github: "JokicArsenije",
    },
    {
      name: "Mihajlo Spasić",
      role: "Dev/Ops Engineer",
      github: "Mihajlo-Spasic",
    },
  ];

  return (
    <div className="max-w-7xl mx-auto p-8">
      {/* Intro */}
      <section className="text-center mb-16">
        <h1 className="text-4xl font-bold mb-4 text-gray-900 dark:text-white">
          Meet Our Team
        </h1>
        <p className="text-lg text-gray-600 dark:text-gray-300 max-w-2xl mx-auto">
          We’re a passionate group of developers, testers, and engineers working
          together to build <span className="font-semibold">FineForms</span>.
          Our mission is to make form building and data collection effortless
          and powerful for everyone.
        </p>
      </section>

      {/* Team Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-8">
        {team.map((member, index) => (
          <div
            key={index}
            className="bg-white dark:bg-gray-800 shadow-lg rounded-2xl p-6 text-center transform hover:-translate-y-2 hover:shadow-2xl transition duration-300"
          >
            {/* Profile Image */}
            <img
              src={`https://github.com/${member.github}.png`}
              alt={member.name}
              className="w-24 h-24 mx-auto rounded-full mb-4 border-4 border-indigo-600 object-cover"
            />

            {/* Name + Role */}
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white">
              {member.name}
            </h2>
            <p className="text-gray-500 dark:text-gray-400 mb-3 text-sm">
              {member.role}
            </p>

            {/* GitHub Link */}
            <a
              href={`https://github.com/${member.github}`}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-block px-4 py-2 text-sm font-medium text-white bg-indigo-700 rounded-lg hover:bg-blue-700 transition"
            >
              GitHub
            </a>
          </div>
        ))}
      </div>

      {/* Closing Section */}
      <section className="mt-20 text-center">
        <h2 className="text-2xl font-bold mb-4 text-gray-900 dark:text-white">
          Want to Learn More?
        </h2>
        <p className="text-gray-600 dark:text-gray-300 mb-6 max-w-xl mx-auto">
          Check out our project on GitHub, follow our progress, and join the
          community. We’re always open to feedback and collaboration!
        </p>
        <a
          href="https://github.com/ognjenobravocikg/FineForms"
          target="_blank"
          rel="noopener noreferrer"
          className="px-6 py-3 bg-gray-900 dark:bg-indigo-700 text-white rounded-lg shadow hover:bg-indigo-800 transition"
        >
          Visit Project Repo
        </a>
      </section>
    </div>
  );
}
