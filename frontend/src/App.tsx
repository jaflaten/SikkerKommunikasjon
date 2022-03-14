import logo from "./logo.svg";
import "./App.css";

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
    <div className="App">
      <header className="App-header">
        <h1 style={{ color: "#fba161" }}>pre-demo test Sikker Kommunikasjon</h1>
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
