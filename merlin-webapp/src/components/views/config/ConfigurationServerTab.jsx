import React from 'react';
import {Button, Collapse} from 'reactstrap';
import DirectoryItemsFieldset from './DirectoryItemsFieldset';
import {
    FormButton,
    FormCheckbox,
    FormLabelField,
    FormLabelInputField,
    FormFieldset
} from "../../general/forms/FormComponents";
import {getRestServiceUrl} from "../../../utilities/global";
import {IconDanger, IconWarning} from '../../general/IconComponents';
import {getTranslation} from "../../../utilities/i18n";
import I18n from "../../general/translation/I18n";
import ErrorAlertGenericRestFailure from '../../general/ErrorAlertGenericRestFailure';
import Loading from "../../general/Loading";

var directoryItems = [];

class ConfigServerTab extends React.Component {
    loadConfig = () => {
        this.setState({
            loading: true,
            failed: false
        });
        fetch(getRestServiceUrl('configuration/config'), {
            method: 'GET',
            dataType: 'JSON',
            headers: {
                'Content-Type': 'text/plain; charset=utf-8'
            }
        })
            .then((resp) => {
                return resp.json()
            })
            .then((data) => {
                const {templatesDirs, ...config} = data;

                directoryItems.splice(0, directoryItems.length);
                let idx = 0;
                if (templatesDirs) {
                    templatesDirs.forEach(function (item) {
                        directoryItems.push({index: idx++, directory: item.directory, recursive: item.recursive});
                    });
                }
                config.directoryItems = directoryItems;

                this.setState({
                    loading: false,
                    ...config
                })
            })
            .catch((error) => {
                console.log("error", error);
                this.setState({
                    loading: false,
                    failed: true
                });
            })
    };

    constructor(props) {
        super(props);

        this.state = {
            loading: true,
            failed: false,
            port: 8042,
            showTestData: true,
            webDevelopment: false,
            directoryItems: [],
            redirect: false,
            expertSettingsOpen: false
        };

        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleCheckboxChange = this.handleCheckboxChange.bind(this);
        this.addDirectoryItem = this.addDirectoryItem.bind(this);
        this.removeDirectoryItem = this.removeDirectoryItem.bind(this);
        this.onResetConfiguration = this.onResetConfiguration.bind(this);
        this.loadConfig = this.loadConfig.bind(this);
    }

    componentDidMount() {
        this.loadConfig();
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

    save() {
        var config = {
            port: this.state.port,
            showTestData: this.state.showTestData,
            webDevelopment: this.state.webDevelopment,
            templatesDirs: []
        };
        if (this.state.directoryItems) {
            this.state.directoryItems.forEach(function (item) {
                config.templatesDirs.push({directory: item.directory, recursive: item.recursive});
            });
        }
        return fetch(getRestServiceUrl("configuration/config"), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(config)
        })
    }

    onResetConfiguration() {
        if (window.confirm(getTranslation('configuration.resetAllSettings.question'))) {
            fetch(getRestServiceUrl("configuration/reset?IKnowWhatImDoing=true"), {
                method: "GET",
                dataType: "JSON",
                headers: {
                    "Content-Type": "text/plain; charset=utf-8"
                }
            })
        }
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
        if (this.state.loading) {
            return <Loading/>;
        }

        if (this.state.failed) {
            return <ErrorAlertGenericRestFailure handleClick={this.loadConfig} />;
        }

        return (
            <form>
                <FormLabelField label={<I18n name={'configuration.showTestData'}/>} fieldLength={2}>
                    <FormCheckbox checked={this.state.showTestData}
                                  name="showTestData"
                                  onChange={this.handleCheckboxChange}/>
                </FormLabelField>
                <DirectoryItemsFieldset items={this.state.directoryItems} addItem={this.addDirectoryItem}
                                        removeItem={this.removeDirectoryItem}
                                        onDirectoryChange={this.handleDirectoryChange}
                                        onRecursiveFlagChange={this.handleRecursiveFlagChange}/>
                <FormLabelField>
                    <Button className={'btn-outline-primary'}
                            onClick={() => this.setState({expertSettingsOpen: !this.state.expertSettingsOpen})}>
                        <IconWarning/> <I18n name={'configuration.forExpertsOnly'}/>
                    </Button>
                </FormLabelField>
                <Collapse isOpen={this.state.expertSettingsOpen}>
                    <FormFieldset text={<I18n name={'configuration.expertSettings'}/>}>
                        <FormLabelInputField label={'Port'} fieldLength={2} type="number" min={0} max={65535}
                                             step={1}
                                             name={'port'} value={this.state.port}
                                             onChange={this.handleTextChange}
                                             placeholder="Enter port"/>
                        <FormLabelField label={<I18n name={'configuration.webDevelopment'}/>} fieldLength={2}>
                            <FormCheckbox checked={this.state.webDevelopment}
                                          hintKey={'configuration.webDevelopment.hint'}
                                          name="webDevelopment"
                                          onChange={this.handleCheckboxChange}/>
                        </FormLabelField>
                        <FormLabelField>
                            <FormButton id={'resetFactorySettings'} onClick={this.onResetConfiguration}
                                        hintKey={'configuration.resetAllSettings.hint'}> <IconDanger/> <I18n
                                name={'configuration.resetAllSettings'}/>
                            </FormButton>
                        </FormLabelField>
                    </FormFieldset>
                </Collapse>
            </form>
        );
    }
}

export default ConfigServerTab;

