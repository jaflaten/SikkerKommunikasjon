import Form from "./components/Form.tsx";
import Frontpage from "./components/Frontpage.tsx";

import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

function App() {
  console.log("Environment:");
  console.log(process.env);
  console.log("Test Fetching:");
  let capabilitiesURL =
    process.env.REACT_APP_API_URL + "/v1/capabilities/991825827";
  console.log(capabilitiesURL);

  fetch(capabilitiesURL)
    .then((res) => res)
    .then((result) => result.json())
    .then((res) => console.log(res))
    .catch((e) => {
      if (e.message === "Failed to fetch") {
        console.log(
          "Failed to fetch endpoint, are you sure service is running?"
        );
      } else {
        console.log(e);
      }
    });

  return (
    <div className="mainTheme">
      <Router>
        <Routes>
          <Route path="/" element={<Frontpage />} />
          <Route path="/form" element={<Form />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
