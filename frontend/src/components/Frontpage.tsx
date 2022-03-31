import ReactMarkdown from "react-markdown";
import { useState, useEffect } from "react";
import gfm from "remark-gfm";
const Frontpage = () => {
  const [md, setMd] = useState("");

  useEffect(() => {
    fetch(
      "https://raw.githubusercontent.com/jaflaten/SikkerKommunikasjon/main/README.md"
    )
      .then((res) => res.text())
      .then((res) => setMd(res));
  }, []);

  return (
    <div>
      <div className="fpDescription">
        <ReactMarkdown remarkPlugins={[gfm]}>{md}</ReactMarkdown>
      </div>
      <div className="fpButton">
        <button
          id="toFormButton"
          type="button"
          onClick={() => window.location.assign("/form")}
        >
          Send ett
          <br /> skjema
        </button>
      </div>
    </div>
  );
};

export default Frontpage;
