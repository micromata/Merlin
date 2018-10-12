import React from 'react';
import {Redirect} from 'react-router-dom'
import {Button, Collapse} from 'reactstrap';
import {PageHeader} from '../../general/BootstrapComponents';
import DirectoryItemsFieldset from "./DirectoryItemsFieldset";
import {
    FormGroup,
    FormLabelField,
    FormLabelInputField,
    FormFieldset,
    FormField, FormButton, FormSelect
} from "../../general/forms/FormComponents";
import {getRestServiceUrl} from "../../../actions/global";

var directoryItems = [];

class ConfigForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            port: 8042,
            language: 'auto',
            directoryItems: [],
            redirect: false,
            expertSettingsOpen: false
        }
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleCheckboxChange = this.handleCheckboxChange.bind(this);
        this.addDirectoryItem = this.addDirectoryItem.bind(this);
        this.removeDirectoryItem = this.removeDirectoryItem.bind(this);
        this.onSave = this.onSave.bind(this);
        this.onCancel = this.onCancel.bind(this);
        this.onResetConfiguration = this.onResetConfiguration.bind(this);
    }

    componentDidMount() {
        fetch(getRestServiceUrl("configuration/config"), {
            method: "GET",
            dataType: "JSON",
            headers: {
                "Content-Type": "text/plain; charset=utf-8"
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

    onResetConfiguration() {
        if (window.confirm('Are you sure you want to reset all settings? All settings will be deleted on server.')) {
            fetch(getRestServiceUrl("configuration/reset?IKnowWhatImDoing=true"), {
                method: "GET",
                dataType: "JSON",
                headers: {
                    "Content-Type": "text/plain; charset=utf-8"
                }
            })
            this.setRedirect();
        }
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
                <FormLabelField label={'Language'} fieldLength={2}>
                    <FormSelect value={this.state.language} name={'language'} onChange={this.handleTextChange}>
                        <option value={'auto'}>Automatically</option>
                        <option value={'en'}>English</option>
                        <option value={'de'}>German</option>
                    </FormSelect>
                </FormLabelField>
                <DirectoryItemsFieldset items={this.state.directoryItems} addItem={this.addDirectoryItem}
                                        removeItem={this.removeDirectoryItem}
                                        onDirectoryChange={this.handleDirectoryChange}
                                        onRecursiveFlagChange={this.handleRecursiveFlagChange}/>
                <FormLabelField>
                    <Button color="link" onClick={() => this.setState({expertSettingsOpen: !this.state.expertSettingsOpen})}>
                        For experts only
                    </Button>
                </FormLabelField>
                <Collapse isOpen={this.state.expertSettingsOpen}>
                    <FormFieldset text={'Expert settings'}>
                        <FormLabelInputField label={'Port'} fieldLength={2} type="number" min={0} max={65535}
                                             step={1}
                                             name={'port'} value={this.state.port}
                                             onChange={this.handleTextChange}
                                             placeholder="Enter port"/>
                        <FormLabelField>
                            <FormButton onClick={this.onResetConfiguration} bsStyle="danger"
                                        hint="Reset factory settings.">Reset
                            </FormButton>
                        </FormLabelField>
                    </FormFieldset>
                </Collapse>
                <FormGroup>
                    <FormField length={12}>
                        <FormButton onClick={this.onCancel} bsStyle="danger"
                                    hint="Discard changes and go to Start page.">Cancel
                        </FormButton>
                        <FormButton onClick={this.onSave} bsStyle="success"
                                    hint="Persist changes and go to Start page.">Save
                        </FormButton>
                    </FormField>
                </FormGroup>
            </form>
        );
    }
}

class Configuration extends React.Component {

    render() {
        return (
            <div>
                <PageHeader>Configuration</PageHeader>
                <ConfigForm/>

                <h3>ToDo</h3>
                <ul>
                    <li>Do the form validation (server and/or client side) with error fields.</li>
                </ul>

            </div>
        );
    }
}

export default Configuration;

