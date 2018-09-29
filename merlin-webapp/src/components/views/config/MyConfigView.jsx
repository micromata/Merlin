import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {fetchConfig, fetchConfigIfNeeded, updateConfigProperty} from '../../../actions/';

class DirectoryField extends React.Component {
    render() {
        const directory = this.props.directory;

        return (
            <form>
                <div className="form-group row">
                    <label className="col-sm-2 col-form-label" htmlFor="inputPort">Directory</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" id="inputDirectory"
                               value={directory} placeholder="Enter directory"/>
                    </div>
                    <div className="col-sm-2">
                        <button type="button" className="btn" title="Call rest service for browsing local directories">Browse</button>
                        <button type="button" className="btn btn-danger" title="remove this entry"><span className="glyphicon glyphicon-remove"/>
                        </button>
                    </div>
                </div>
            </form>
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
                    <DirectoryField directory="/Users/kai/Documents/Merlin-templates"/>
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
                        <button type="button" className="btn btn-danger" title="Discard changes and go to Start page.">Cancel</button>
                        <button type="button" className="btn btn-success" title="Persist changes and go to Start page.">Save</button>
                    </div>
                </div>
            </form>
        );
    }
}

class MyConfigView extends React.Component {
    constructor(props) {
        super(props);
        // props.fetchConfigIfNeeded();
    }

    render() {
        const port = this.props.port;

        return (
            <div>
                <PageHeader>Config</PageHeader>
                <MyConfigForm port="8042" language="German"/>

                <div>
                    <h3>ToDo</h3>
                    <ul>
                        <li>Binding rest services.</li>
                        <li>Implementing Browse-Button: calling rest service: <b>rest/files/browse-local-filesystem?type=dir</b> and putting result in input field.</li>
                        <li>Functionality: adding and deleting template directories.</li>
                        <li>Cancel-Button: Discard changes and proceed to Start.</li>
                        <li>Submit-Button: Save changes via rest service.</li>
                        <li>Submit-Button: Make Save button as default button (if the user hits return, this button should be executed).</li>
                        <li>Do the form validation (server and/or client side) with error fields.</li>
                        <li>I18n: Get the translations from the server via json for labels, languages etc.</li>
                    </ul>
                </div>

            </div>
        );
    }
}

export default MyConfigView;

