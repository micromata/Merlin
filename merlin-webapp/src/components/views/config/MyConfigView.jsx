import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {getRestServiceUrl} from "../../../actions/global";

class DirectoryField extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            directory: this.props.directory,
            recursive: this.props.recursive
        }
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleCheckedChange = this.handleCheckedChange.bind(this);
    }

    handleTextChange = event => {
        event.preventDefault();
        console.log("id=" + event.target.id + ", name=" + event.target.name + " -> " + event.target.value);
        this.setState({[event.target.name]: event.target.value});
    }

    handleCheckedChange(e) {
        console.log(e.target.value);
        this.setState({[e.target.name]: e.target.value});
    }


    browseDirectory = () => {
        const current = this.state.directory ? "&current=" + encodeURIComponent(this.state.directory) : '';
        fetch(getRestServiceUrl("files/browse-local-filesystem?type=dir" + current), {
            method: "GET",
            dataType: "JSON",
            headers: {
                "Content-Type": "text/plain; charset=utf-8",
            }
        })
            .then((resp) => {
                return resp.json()
            })
            .then((data) => {
                if (data.directory) {
                    this.setState({directory: data.directory})
                }
            })
            .catch((error) => {
                console.log(error, "Oups, what's happened?")
            })
    }


    render() {
        return (
            <div className="form-group row">
                <label className="col-sm-2 col-form-label"
                       htmlFor="inputDirectory">Directory</label>
                <div className="col-sm-6">
                    <input name="directory" type="text" className="form-control" id="inputDirectory" onChange={this.handleTextChange}
                           value={this.state.directory} placeholder="Enter directory"/>
                </div>
                <div className="form-check col-sm-2">
                    <input className="form-check-input" type="checkbox" checked={this.props.recursive}
                           id="checkRecursive"
                           title="If checked, Merlin will search for all templates inside this directory including all sub directories. If not checked, the sub directories will be skipped."/>
                    <label className="form-check-label" htmlFor="checkRecursive">
                        recursive
                    </label>
                </div>
                <div className="col-sm-2">
                    <button type="button" className="btn" onClick={this.browseDirectory}
                            title="Call rest service for browsing local directories">Browse
                    </button>
                    <button type="button" className="btn btn-danger" title="remove this entry"><span
                        className="glyphicon glyphicon-remove"/>
                    </button>
                </div>
            </div>
        );
    }
}

class MyConfigForm extends React.Component {
    render() {
        const port = this.props.port;
        const language = this.props.language;

        return (
            <form>
                <div className="form-group row">
                    <label className="col-sm-2 col-form-label" htmlFor="inputPort">Port</label>
                    <div className="col-sm-2">
                        <input type="number" min="0" max="65535" step="1" className="form-control" id="inputPort"
                               value={port} placeholder="Enter port"/>
                    </div>
                </div>
                <div className="form-group row">
                    <label className="col-sm-2 col-form-label" htmlFor="selectLanguage">Language</label>
                    <div className="col-sm-2">
                        <select className="form-control" id="selectLanguage" value={language}>
                            <option>English</option>
                            <option>German</option>
                        </select>
                    </div>
                </div>
                <fieldset className="form-group">
                    <legend>Template directories</legend>
                    <DirectoryField directory="/Users/kai/Documents/Merlin-templates" recursive="true"/>
                    <DirectoryField directory="/Users/kai/Projects/ACME/contracts/templates"/>
                    <div className="form-group row">
                        <div className="col-sm-2"></div>
                        <div className="col-sm-10">
                            <button type="button" className="btn" title="Add new Template directory row"><span
                                className="glyphicon glyphicon-plus"/></button>
                        </div>
                    </div>
                </fieldset>
                <div className="form-group row">
                    <div className="col-sm-12">
                        <button type="button" className="btn btn-danger"
                                title="Discard changes and go to Start page.">Cancel
                        </button>
                        <button type="button" className="btn btn-success"
                                title="Persist changes and go to Start page.">Save
                        </button>
                    </div>
                </div>
            </form>
        );
    }
}

class MyConfigView extends React.Component {

    render() {
        const port = this.props.port;

        return (
            <div>
                <PageHeader>Config</PageHeader>
                <MyConfigForm port="8042" language="German"/>

                <h3>ToDo</h3>
                <ul>
                    <li>Binding rest services.</li>
                    <li>Functionality: adding and deleting template directories.</li>
                    <li>Cancel-Button: Discard changes and proceed to Start.</li>
                    <li>Submit-Button: Save changes via rest service.</li>
                    <li>Submit-Button: Make Save button as default button (if the user hits return, this button
                        should be executed).
                    </li>
                    <li>Do the form validation (server and/or client side) with error fields.</li>
                </ul>

            </div>
        );
    }
}

export default MyConfigView;

