import React, { useEffect, useState } from "react";
import AsyncSelect from "react-select/async";
import BrRegService from "../services/BrRegService";
import BackendAPIService from "../services/BackendAPIService";

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

const urlParams = new URLSearchParams(window.location.search);
const getReceiverParam = () => urlParams.get("receiver");

const Form = () => {
  const [selectedReceiverValue, setSelectedValue] = useState(null);

  /**
   * Gets receiver param from url only on on first render
   */
  useEffect(() => {
    async function getReceiver(_query: any) {
      setSelectedValue(await BrRegService.getAnyOrgEntityByNumAsync(_query));
    }
    const query = getReceiverParam();
    if (query) getReceiver(query);
  }, []);

  /**
   * Regex to check if valid organisation number Norway
   */
  const orgNrRegex = /^(\d{4}:)?(\d{9})$/;

  /**
   * Handles the Receiver Searchbar InputChange
   * @param value current value of input element
   */
  const handleReceiverInputChange = async (value: string) => {
    //if valid orgnumber, set value, then unfocus("simulating hitting enter")
    if (orgNrRegex.test(value)) {
      setSelectedValue(await BrRegService.getAnyOrgEntityByNumAsync(value));
      blurAll();
    }

    /**
     * unfocus all everything, scroll to bottom
     */
    function blurAll() {
      var tmp = document.createElement("input");
      document.body.appendChild(tmp);
      tmp.focus();
      document.body.removeChild(tmp);
    }
  };

  /**
   * Handles the Receiver Searchbar selection
   * @param value selected element
   */
  const handleChange = (value: React.ChangeEvent<HTMLInputElement>) => {
    setSelectedValue(value);
  };

  /**
   * Statehook for formdata, dependant on @selectedReceiverValue
   */
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
  /**
   * updates formdata receiver value on selectedReiver change
   */
  useEffect(() => {
    setFormData((prevState) => {
      return {
        ...prevState,
        receiver: selectedReceiverValue
          ? selectedReceiverValue["organisasjonsnummer"]
          : "",
      };
    });
  }, [selectedReceiverValue]);

  /**
   * Handles submit button
   */
  const handleSubmit = () => {
    let form = document.getElementById("form") as HTMLFormElement;
    if (
      form.checkValidity() && //check most of form validity
      formData?.receiver && // check receiver validity
      window.confirm("Er du sikker?") //confirm with user
    )
      submit();
  };

  /**
   * Submit form action
   */
  const submit = () => {
    BackendAPIService.postForm(
      new FormData(document.getElementById("form") as HTMLFormElement)
    )
      .then((response) => response.text())
      .then((result) => {
        console.log(result);

        alert("Melding har blitt sendt, du blir sendt til fremsiden");
        window.location.assign("/");
      })
      .catch((error) => {
        console.log("error", error);
        alert("Noe gikk feil, prøv igjen?");
      });
  };

  /**
   * Allows for ReceiverInput to be cleared if delete or backspace is hit when in focus
   * @param e event with keycode
   */
  const handleKeyDownReceiver = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === `Delete` || e.key === `Backspace`) {
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
              name="receiver"
              cacheOptions
              defaultOptions
              value={selectedReceiverValue}
              getOptionLabel={(e) => e["navn"]}
              getOptionValue={(e) => e["organisasjonsnummer"]}
              loadOptions={BrRegService.searchAnyByNameAsync}
              onInputChange={handleReceiverInputChange}
              onChange={handleChange}
              onKeyDown={handleKeyDownReceiver}
            />
          </label>
          <br />
          <a
            href={
              selectedReceiverValue
                ? "//" + selectedReceiverValue["hjemmeside"]
                : ""
            }
          >
            {selectedReceiverValue ? selectedReceiverValue["navn"] : ""}
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
              name="content"
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
              name="attachment"
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

          setSelectedValue(
            await BrRegService.getAnyOrgEntityByNumAsync(receiver)
          );
        }}
      >
        Fill Mock Data
      </button>
      <div>
        <img src="/logo.svg" alt="Logo SK"></img>
      </div>
    </div>
  );
};

export default Form;
