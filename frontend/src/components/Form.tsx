import React from 'react';
import { useState } from "react";

class Form extends React.Component<{}, { ssn: string, name: string, email: string, receiver: string, title: string, message: string, isSensitive: boolean, selectedFile: File }> {

    constructor(props) {
        super(props);
        this.state =
        {
            ssn: "",
            name: "",
            email: "",
            receiver: "",
            title: "",
            message: "",
            isSensitive: false,
            selectedFile: null
        };
    }
   private handleSubmit() {
       console.log("Submit form handle")
   }

    render() {
        return (
            <div>
                <form>
                    <div>
                        <h2>Hvem Sender Inn?</h2>
                        <label>
                            Personnummer
                            <input
                                required
                                pattern='^(0[1-9]|[1-2][0-9]|31(?!(?:0[2469]|11))|30(?!02))(0[1-9]|1[0-2])\d{7}$'
                                type="text"
                                name="ssn"
                                value={this.state.ssn}
                                onChange={(e) => this.setState({ ssn: e.target.value })}
                            />
                        </label>
                        <label>
                            Navn
                            <input
                                required
                                type="text"
                                name="name"
                                value={this.state.name}
                                onChange={(e) => this.setState({ name: e.target.value })}
                            />
                        </label>
                        <label>
                            Epost
                            <input
                                required
                                type="email"
                                name="email"
                                value={this.state.email}
                                onChange={(e) => this.setState({email: e.target.value })}
                            />
                        </label>
                    </div>

                    <div>
                        <h2>Hvem er Mottaker?</h2>
                        <label>
                            Mottaker
                            <input
                                required
                                type="text"
                                name="receiver"
                                pattern='^([0-9]{4}:)?([0-9]{9})$'
                                value={this.state.receiver}
                                onChange={(e) => this.setState({ receiver: e.target.value })}
                            />
                        </label>
                    </div>

                    <div>
                        <h2>Hva skal sendes?</h2>
                        <label>
                            Tittel
                            <input
                                required
                                type="text"
                                name="title"
                                value={this.state.title}
                                onChange={(e) => this.setState({ title: e.target.value })}
                            />
                        </label>
                        <label>
                            Kommentar
                            <textarea
                                required
                                name="message"
                                value={this.state.message}
                                onChange={(e) => this.setState({ message: e.target.value })}
                            />
                        </label>
                    </div>

                    <div>
                        <h2>Inneholder henvendelsen sensitive eller følsomme opplysninger?</h2>
                        <label>
                            Ja:
                            <input
                                checked={this.state.isSensitive}
                                type="radio"
                                name="isSensitive"
                                onChange={(e) => this.setState({ isSensitive: e.target.checked })}
                            />
                        </label>
                        <label>
                            Nei:
                            <input
                                checked={!this.state.isSensitive}
                                type="radio"
                                name="isSensitive"
                                onChange={(e) => this.setState({  isSensitive: !(e.target.checked) })}
                            />
                        </label>
                    </div>

                    <div>
                        <label>
                            <input
                                type="file"
                                onChange={(e) => this.setState({ selectedFile: e.target.files[0] })} //only accepts 1 fileupload
                            />
                        </label>
                    </div>

                    <button type="button" onClick={this.handleSubmit}>Send Sikker Melding</button>
                </form>
            </div>
        )
    }
}

export default Form