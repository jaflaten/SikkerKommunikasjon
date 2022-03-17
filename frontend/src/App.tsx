import logo from "./logo.svg";
import "./App.css";
import Form from './components/Form.tsx'

function App() {
  console.log(process.env)
  console.log(process.env.REACT_APP_API_URL)
  
  fetch("/api/v1/capabilities/991825827")
    .then((res) => res)
    .then((result) => {
      result.json().then((res) => console.log(res));
    })
    .catch(console.log);

  return (
    <Form />
  );
}

export default App;
