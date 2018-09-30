import React from 'react';
import {Redirect} from 'react-router-dom'
import {PageHeader} from 'react-bootstrap';
import DirectoryItemsFieldset from "./DirectoryItemsFieldset";
import {getRestServiceUrl} from "../../../actions/global";

var directoryItems = [];

class MyConfigForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            port: 8042,
            language: 'en',
            directoryItems: [],
            redirect: false
        }
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleCheckboxChange = this.handleCheckboxChange.bind(this);
        this.addDirectoryItem = this.addDirectoryItem.bind(this);
        this.removeDirectoryItem = this.removeDirectoryItem.bind(this);
        this.onSave = this.onSave.bind(this);
        this.onCancel = this.onCancel.bind(this);
    }

    componentDidMount() {
        fetch(getRestServiceUrl("configuration/config"), {
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
                if (data.port) {
                    this.setState({port: data.port});
                }
                if (data.language) {
                    this.setState({language: data.language});
                }
                directoryItems.splice(0, directoryItems.length)
                if (data.templatesDirs) {
                    let idx = 0;
                    data.templatesDirs.forEach(function (item) {
                        directoryItems.push({index: idx++, directory: item.directory, recursive: item.recursive});
                    });
                    this.setState({directoryItems: directoryItems});
                }
            })
            .catch((error) => {
                console.log(error, "Oups, what's happened?")
            })
    }

    setRedirect = () => {
        this.setState({
            redirect: true
        })
    }
    handleTextChange = event => {
        event.preventDefault();
        this.setState({[event.target.name]: event.target.value});
    }

    handleCheckboxChange = event => {
        this.setState({[event.target.name]: event.target.checked});
    }

    handleDirectoryChange = (index, newDirectory) => {
        const items = this.state.directoryItems;
        items[index].directory = newDirectory;
        // update state
        this.setState({
            directoryItems,
        });
    }

    handleRecursiveFlagChange = (index, newRecursiveState) => {
        const items = this.state.directoryItems;
        items[index].recursive = newRecursiveState;
        // update state
        this.setState({
            directoryItems,
        });
    }

    onSave(event) {
        var config = {port: this.state.port, language: this.state.language, templatesDirs: []};
        if (this.state.directoryItems) {
            this.state.directoryItems.forEach(function (item) {
                config.templatesDirs.push({directory: item.directory, recursive: item.recursive});
            });
        }
        fetch(getRestServiceUrl("configuration/config"), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(config)
        })
        this.setRedirect();
    }

    onCancel() {
        this.setRedirect();
    }

    addDirectoryItem() {
        directoryItems.push({
            index: directoryItems.length + 1,
            directory: "",
            recursive: false
        });
        this.setState({directoryItems: directoryItems});
    }

    removeDirectoryItem(itemIndex) {
        directoryItems.splice(itemIndex, 1);
        this.setState({directoryItems: directoryItems});
    }

    render() {
        // https://codepen.io/_arpy/pen/xYoyPW
        if (this.state.redirect) {
            return <Redirect to='/'/>
        }
        return (
            <form>
                <div className="form-group row">
                    <label className="col-sm-2 col-form-label" htmlFor="inputPort">Port</label>
                    <div className="col-sm-2">
                        <input type="number" min="0" max="65535" step="1" className="form-control" id="inputPort"
                               value={this.state.port} name="port" onChange={this.handleTextChange}
                               placeholder="Enter port"/>
                    </div>
                </div>
                <div className="form-group row">
                    <label className="col-sm-2 col-form-label" htmlFor="selectLanguage">Language</label>
                    <div className="col-sm-2">
                        <select className="form-control" id="selectLanguage" value={this.state.language} name="language"
                                onChange={this.handleTextChange}>
                            <option>English</option>
                            <option>German</option>
                        </select>
                    </div>
                </div>
                <DirectoryItemsFieldset items={this.state.directoryItems} addItem={this.addDirectoryItem}
                                        removeItem={this.removeDirectoryItem}
                                        onDirectoryChange={this.handleDirectoryChange}
                                        onRecursiveFlagChange={this.handleRecursiveFlagChange}/>
                <div className="form-group row">
                    <div className="col-sm-12">
                        <button type="button" onClick={this.onCancel} className="btn btn-danger"
                                title="Discard changes and go to Start page.">Cancel
                        </button>
                        <button type="button" onClick={this.onSave} className="btn btn-success"
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
        return (
            <div>
                <PageHeader>Config</PageHeader>
                <MyConfigForm/>

                <h3>ToDo</h3>
                <ul>
                    <li>Do the form validation (server and/or client side) with error fields.</li>
                </ul>

            </div>
        );
    }
}

export default MyConfigView;

