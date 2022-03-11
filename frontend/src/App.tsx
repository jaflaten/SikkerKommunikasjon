import logo from "./logo.svg";
import "./App.css";

function App() {
  fetch("http://localhost:1337/api", { mode: "no-cors" })
    .then((res) => res)
    .then((result) => {
      console.log(result);
    });

  return (
    <div className="App">
      <header className="App-header">
        <h1 style={{ color: "#fba161" }}>Sikker Kommunikasjon</h1>
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
