import React from "react";
import { useState } from "react";

interface IFormData {
  ssn: string;
  name: string;
  email: string;
  receiver: string;
  title: string;
  message: string;
  isSensitive: boolean;
  selectedFile: File;
}

const Form = () => {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);

  const [searchResult, setSearchResult] = useState({
    list: [],
  });

  const [formData, setFormData] = useState<IFormData>({
    ssn: "",
    name: "",
    email: "",
    receiver: urlParams.get("receiver") || "",
    title: "",
    message: "",
    isSensitive: false,
    selectedFile: null,
  });

  const [orgLookup, setOrgLookup] = useState({
    org: null,
  });

  const brRegURL = "https://data.brreg.no/enhetsregisteret/api/enheter/";

  const brRegURLSearch =
    "https://data.brreg.no/enhetsregisteret/api/enheter?navn=";
  const searchOrgs = async (searchString) => {
    if (!searchString) return;
    fetch(brRegURLSearch + searchString)
      .then((response) => response.json())
      .then((result) => result["_embedded"]["enheter"])
      .then((newList) => {
        console.log(newList);
        setSearchResult(newList);
      })
      .catch((error) => console.log("error", error));
  };
  /**
   * Fetches org data from brRegURL
   * @param newOrgnr orgnazation number of org
   * @param force optional ignore validity requirements and try fetch anyways default:false
   */
  const fetchOrg = async (newOrgnr, force = false) => {
    const recElement = document.getElementById("receiver") as HTMLInputElement;
    if (force || recElement.checkValidity()) {
      setOrgLookup({ org: { name: "Getting Name..." } });
      console.log("Fetching:");
      console.log(brRegURL + newOrgnr);
      try {
        let responseJson = await (await fetch(brRegURL + newOrgnr)).json();
        setOrgLookup({ org: responseJson });
      } catch (e) {
        console.log("Something went wrong with fetch");
        console.log(e);
      }
    } else {
      setOrgLookup({ org: null });
    }
  };
  const handleSubmit = () => {
    //implement handleSubmit
    let form = document.getElementById("form") as HTMLFormElement;
    if (form.checkValidity() && window.confirm("Er du sikker?")) submit();
  };

  const submit = () => {
    console.log("TODO: Handle submit form");
  };

  const styles = {
    container: { marginLeft: "20px" },
  } as const;

  return (
    <div style={styles.container}>
      <form id="form">
        <div>
          <h2>Hvem Sender Inn?</h2>
          <label>
            Personnummer
            <br />
            <input
              required
              pattern="^(0[1-9]|[1-2][0-9]|31(?!(?:0[2469]|11))|30(?!02))(0[1-9]|1[0-2])\d{7}$"
              type="text"
              name="ssn"
              value={formData.ssn}
              onChange={(e) =>
                setFormData({ ...formData, ssn: e.target.value })
              }
            />
            <div>*11 siffer</div>
          </label>
          <br />
          <label>
            Navn
            <br />
            <input
              required
              type="text"
              name="name"
              value={formData.name}
              onChange={(e) =>
                setFormData({ ...formData, name: e.target.value })
              }
            />
            <div>*Ditt fulle navn</div>
          </label>
          <br />
          <label>
            Epost
            <br />
            <input
              required
              type="email"
              name="email"
              value={formData.email}
              onChange={(e) =>
                setFormData({ ...formData, email: e.target.value })
              }
            />
            <div>*gyldig epost kreves</div>
          </label>
        </div>

        <div>
          <h2>Hvem er Mottaker?</h2>
          <label>
            Mottaker
            <br />
            <input
              required
              id="receiver"
              type="text"
              name="receiver"
              pattern="^([0-9]{4}:)?([0-9]{9})$"
              value={formData.receiver}
              onChange={(e) => {
                setFormData({ ...formData, receiver: e.target.value });
                if (e.currentTarget.checkValidity()) {
                  fetchOrg(e.target.value);
                } else {
                  searchOrgs(e.target.value);
                }
              }}
            />
            <div>*gyldig organisasjonsnummer (9 siffer)</div>
          </label>
          <br />
          <a href={orgLookup.org ? "//" + orgLookup.org["hjemmeside"] : ""}>
            {orgLookup.org ? orgLookup.org["navn"] : ""}
          </a>
        </div>

        <div>
          <h2>Hva skal sendes?</h2>
          <label>
            Tittel
            <br />
            <input
              required
              type="text"
              name="title"
              value={formData.title}
              onChange={(e) =>
                setFormData({ ...formData, title: e.target.value })
              }
            />
            <div>*Tittel</div>
          </label>
          <br />
          <label>
            Kommentar
            <br />
            <textarea
              required
              name="message"
              value={formData.message}
              onChange={(e) =>
                setFormData({ ...formData, message: e.target.value })
              }
            />
            <div>*Kommentar</div>
          </label>
        </div>

        <div>
          <h2>
            Inneholder henvendelsen sensitive eller følsomme opplysninger?
          </h2>
          <label>
            Ja:
            <input
              checked={formData.isSensitive}
              type="radio"
              name="isSensitive"
              onChange={(e) =>
                setFormData({ ...formData, isSensitive: e.target.checked })
              }
            />
          </label>
          <label>
            Nei:
            <input
              checked={!formData.isSensitive}
              type="radio"
              name="isSensitive"
              onChange={(e) =>
                setFormData({ ...formData, isSensitive: !e.target.checked })
              }
            />
          </label>
        </div>

        <div>
          <label>
            <input
              type="file"
              onChange={(e) =>
                setFormData({ ...formData, selectedFile: e.target.files[0] })
              } //only accepts 1 fileupload
            />
          </label>
        </div>

        <button type="button" onClick={handleSubmit}>
          Send Sikker Melding
        </button>
      </form>

      <button
        type="button"
        onClick={() => {
          let data = {
            ssn: "01129955131",
            name: "Ola Nordmann",
            email: "Ola.Nordmann@email.no",
            receiver: "971524960",
            title: "Min tå er vond",
            message: "au au",
            isSensitive: true,
            selectedFile: null,
          };
          setFormData(data);
          fetchOrg(data.receiver, true);
        }}
      >
        Fill Mock Data
      </button>
      <div>
        <img src="/logo.svg"></img>
      </div>
    </div>
  );
};

export default Form;
