import React from 'react';
import {PageHeader} from 'react-bootstrap';
import DirectoryItemsFieldset from "./DirectoryItemsFieldset";

var directoryItems = [];
directoryItems.push({index: 1, directory: "/Users/kai/Documents/", recursive: true});
directoryItems.push({index: 2, directory: "/Users/kai/Documents/workspace/Merlin/templates", recursive: false});


class MyConfigForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            port: this.props.port,
            language: this.props.language,
            directoryItems: this.props.directoryItems
        }
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleCheckboxChange = this.handleCheckboxChange.bind(this);
        this.addDirectoryItem = this.addDirectoryItem.bind(this);
        this.removeDirectoryItem = this.removeDirectoryItem.bind(this);
    }

    handleTextChange = event => {
        event.preventDefault();
        this.setState({[event.target.name]: event.target.value});
    }

    handleCheckboxChange = event => {
        this.setState({[event.target.name]: event.target.checked});
    }

    addDirectoryItem() {
        directoryItems.unshift({
            index: directoryItems.length + 1,
            directory: "",
            recursive: false
        });
        this.setState({directoryItems: directoryItems});
    }

    removeDirectoryItem(itemIndex) {
        console.log("removing item with index " + itemIndex);
        directoryItems.splice(itemIndex, 1);
        this.setState({directoryItems: directoryItems});
    }

    render() {
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
                <DirectoryItemsFieldset items={this.props.directoryItems} addItem={this.addDirectoryItem} removeItem={this.removeDirectoryItem}/>
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
        return (
            <div>
                <PageHeader>Config</PageHeader>
                <MyConfigForm port="8042" language="German" directoryItems={directoryItems}/>

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

