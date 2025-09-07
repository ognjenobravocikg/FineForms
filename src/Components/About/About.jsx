export default function About() {
  const team = [
    {
      name: "Ognjen Obradović",
      role: "Frontend Developer",
      github: "https://github.com/ognjenobravocikg",
    },
    {
      name: "Aleksandar Djokić",
      role: "Backend Engineer",
      github: "https://github.com/caojasamalex",
    },
    {
      name: "Janko Jakovljević",
      role: "Backend Engineer/Joker",
      github: "https://github.com/K3nza2",
    },
    {
      name: "Arsenije Jokić",
      role: "Testing, Project Managment",
      github: "https://github.com/JokicArsenije",
    },
    {
      name: "Mihajlo Spasić",
      role: "Dev/Ops Engineer",
      github: "https://github.com/Mihajlo-Spasic",
    },
  ];

  return (
    <div className="max-w-6xl mx-auto p-8">
      <h1 className="text-4xl font-bold text-center mb-10 text-gray-800">
        Meet Our Team
      </h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6">
        {team.map((member, index) => (
          <div
            key={index}
            className="bg-white shadow-lg rounded-lg p-6 text-center hover:shadow-xl transition"
          >
            <div className="w-20 h-20 mx-auto bg-gray-200 rounded-full mb-4 flex items-center justify-center text-2xl font-bold text-gray-600">
              {member.name[0]}
            </div>
            <h2 className="text-xl font-semibold text-gray-800">
              {member.name}
            </h2>
            <p className="text-gray-500 mb-3">{member.role}</p>
            <a
              href={member.github}
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-600 hover:underline"
            >
              GitHub Profile
            </a>
          </div>
        ))}
      </div>
    </div>
  );
}
