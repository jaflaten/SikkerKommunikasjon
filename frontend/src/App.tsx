import logo from "./logo.svg";
import "./App.css";
import Form from './components/Form.tsx'

function App() {
  console.log("Environment:");
  console.log(process.env);
  console.log("Test Fetching:");
  let capabilitiesURL = process.env.REACT_APP_API_URL + "/v1/capabilities/991825827";
  console.log(capabilitiesURL);

  fetch(capabilitiesURL)
    .then((res) => res)
    .then((result) =>
      result.json()
    )
    .then((res) => console.log(res))
    .catch((e) => {
      if(e.message === "Failed to fetch"){
        console.log("Failed to fetch endpoint, are you sure service is running?")
      }else {
      console.log(e)
      }
    });

  return (
    <Form />
  );
}

export default App;
