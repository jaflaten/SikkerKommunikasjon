import React from "react";
import { useState } from "react";

const Form = () => {
    const [formData, setFormData] = useState({
        ssn: "",
        name: "",
        email: "",
        receiver: "",
        title: "",
        message: "",
        isSensitive: false,
        selectedFile: null,
    });

    const [orgName, setOrgName] = useState({
        name: ""
    });

    const brRegURL = "https://data.brreg.no/enhetsregisteret/api/enheter/"

    const fetchOrgName = async (newOrgnr) => {
        const recElement = document.getElementById("receiver") as HTMLInputElement;
        if (recElement.checkValidity()) {
            setOrgName({ name: "gettingName" })
            console.log("Fetching:")
            console.log(brRegURL + newOrgnr);
            let responseJson: string = await (await fetch(brRegURL + newOrgnr)).json();
            console.log(responseJson)
            setOrgName({ name: responseJson["navn"] })
        }
        else {
            setOrgName({ name: "" })
        }

    }
    const handleSubmit = () => {
        //implement handleSubmit
        let form = document.getElementById("form") as HTMLFormElement;
        if (form.checkValidity() && window.confirm("Er du sikker?")) submit();
    };


    const submit = () => {
        console.log("TODO: Handle submit form");
    };
    return (
        <div>
            <form id="form">
                <div>
                    <h2>Hvem Sender Inn?</h2>
                    <label>
                        Personnummer
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
                    </label>
                    <label>
                        Navn
                        <input
                            required
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={(e) =>
                                setFormData({ ...formData, name: e.target.value })
                            }
                        />
                    </label>
                    <label>
                        Epost
                        <input
                            required
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={(e) =>
                                setFormData({ ...formData, email: e.target.value })
                            }
                        />
                    </label>
                </div>

                <div>
                    <h2>Hvem er Mottaker?</h2>
                    <label>
                        Mottaker
                        <input
                            required
                            id="receiver"
                            type="text"
                            name="receiver"
                            pattern="^([0-9]{4}:)?([0-9]{9})$"
                            value={formData.receiver}
                            onChange={(e) =>{
                                setFormData({ ...formData, receiver: e.target.value });
                                fetchOrgName(e.target.value);
                            }
                            }
                        />
                    </label><br/>
                    <label>{orgName.name}</label>
                </div>

                <div>
                    <h2>Hva skal sendes?</h2>
                    <label>
                        Tittel
                        <input
                            required
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={(e) => setFormData({ ...formData, title: e.target.value })
                            }
                        />
                    </label>
                    <label>
                        Kommentar
                        <textarea
                            required
                            name="message"
                            value={formData.message}
                            onChange={(e) =>
                                setFormData({ ...formData, message: e.target.value })
                            }
                        />
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
                    setFormData({
                        ssn: "01129955131",
                        name: "Ola Nordmann",
                        email: "Ola.Nordmann@email.no",
                        receiver: "971524960",
                        title: "Min tå er vond",
                        message: "au au",
                        isSensitive: true,
                        selectedFile: null,
                    });
                }}
            >
                Fill Mock Data
            </button>
        </div>
    );
};

export default Form;
