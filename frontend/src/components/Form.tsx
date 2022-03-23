import React, { useEffect, useState } from "react";
import AsyncSelect from "react-select/async";

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
  const fetchOrg = async (orgNr) => {
    try {
      let _url = brRegURL + orgNr;
      console.log("fetchOrg:");
      console.log(_url);
      let responseJson = await fetch(_url).then((res) => {
        if (res.status === 404) {
          return {
            navn: "Fant ikke organisasjonsnr",
            organisasjonsnummer: false,
          };
        } else if (res.status === 400) {
          return {
            navn: "ugyldig organisasjonsnr (9 siffer)",
            organisasjonsnummer: false,
          };
        } else {
          return res.json();
        }
      });
      return responseJson;
    } catch (e) {
      console.log("Something went wrong with fetchOrg");
      console.log(e);
    }
  };

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);

  const [selectedValue, setSelectedValue] = useState(null);
  useEffect(() => {
    async function getReceiver(_query) {
      setSelectedValue(await fetchOrg(_query));
    }

    let query = urlParams.get("receiver");
    if (query) getReceiver(query);
  }, []);

  const orgNrRegex = /^([0-9]{4}:)?([0-9]{9})$/;
  // handle input change event
  const handleInputChange = async (value) => {
    if (orgNrRegex.test(value)) {
      setSelectedValue(await fetchOrg(value));
      blurAll();
    }
  };

  function blurAll() {
    var tmp = document.createElement("input");
    document.body.appendChild(tmp);
    tmp.focus();
    document.body.removeChild(tmp);
  }
  // handle selection
  const handleChange = (value) => {
    setSelectedValue(value);
  };

  // load options using API call
  const loadOptions = (inputValue) => {
    console.log("Fetching loadoptions");
    console.log(brRegURLSearch + inputValue);

    return fetch(brRegURLSearch + inputValue)
      .then((res) => res.json())
      .then((jo) => {
        let embedded = jo["_embedded"];
        return embedded ? embedded["enheter"] : [];
      })
      .catch((e) => []);
  };

  const [formData, setFormData] = useState<IFormData>({
    ssn: "",
    name: "",
    email: "",
    receiver: "",
    title: "",
    message: "",
    isSensitive: false,
    selectedFile: null,
  });
  useEffect(() => {
    setFormData({
      ...formData,
      receiver: selectedValue ? selectedValue["organisasjonsnummer"] : "",
    });
  }, [selectedValue]);

  const brRegURL = "https://data.brreg.no/enhetsregisteret/api/enheter/";

  const brRegURLSearch =
    "https://data.brreg.no/enhetsregisteret/api/enheter?navn=";

  const handleSubmit = () => {
    //implement handleSubmit
    let form = document.getElementById("form") as HTMLFormElement;
    if (
      form.checkValidity() &&
      formData?.receiver &&
      window.confirm("Er du sikker?")
    )
      submit();
  };

  const submit = () => {
    console.log("TODO: Handle submit form");
  };

  const handleKeyDown = (e) => {
    if (e.keyCode === 46 || e.keyCode === 8) {
      setSelectedValue(null);
    }
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
            <AsyncSelect
              cacheOptions
              defaultOptions
              value={selectedValue}
              getOptionLabel={(e) => e["navn"]}
              getOptionValue={(e) => e["organisasjonsnummer"]}
              loadOptions={loadOptions}
              onInputChange={handleInputChange}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
            />
          </label>
          <br />
          <a href={selectedValue ? "//" + selectedValue["hjemmeside"] : ""}>
            {selectedValue ? selectedValue["navn"] : ""}
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
        onClick={async () => {
          let receiver = "987464291";

          setFormData({
            ...formData,
            ssn: "01129955131",
            name: "Ola Nordmann",
            email: "Ola.Nordmann@email.no",
            title: "Min tå er vond",
            message: "au au",
            isSensitive: true,
            selectedFile: null,
          });

          setSelectedValue(await fetchOrg(receiver));
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
