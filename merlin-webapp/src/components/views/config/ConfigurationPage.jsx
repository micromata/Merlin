import React from 'react';
import {Redirect} from 'react-router-dom'
import {Button, Collapse} from 'reactstrap';
import {PageHeader} from '../../general/BootstrapComponents';
import DirectoryItemsFieldset from './DirectoryItemsFieldset';
import {
    FormButton,
    FormCheckbox,
    FormField,
    FormGroup,
    FormLabelField,
    FormLabelInputField,
    FormFieldset,
    FormSelect, FormOption
} from "../../general/forms/FormComponents";
import {getRestServiceUrl, isDevelopmentMode} from "../../../utilities/global";
import {IconDanger, IconWarning} from '../../general/IconComponents';
import {getTranslation} from "../../../utilities/i18n";
import I18n from "../../general/translation/I18n";
import ErrorAlert from '../../general/ErrorAlert';


var directoryItems = [];

class ConfigForm extends React.Component {
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
                const {templatesDirModified, templatesDirs, ...config} = data;

                directoryItems.splice(0, directoryItems.length);
                if (templatesDirModified) {
                    let idx = 0;
                    templatesDirs.forEach(function (item) {
                        directoryItems.push({index: idx++, directory: item.directory, recursive: item.recursive});
                    });
                    config.directoryItems = directoryItems;
                }

                this.setState({
                    loading: false,
                    ...config
                })
            })
            .catch(() => {
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
            language: 'default',
            directoryItems: [],
            redirect: false,
            expertSettingsOpen: false
        };

        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleCheckboxChange = this.handleCheckboxChange.bind(this);
        this.addDirectoryItem = this.addDirectoryItem.bind(this);
        this.removeDirectoryItem = this.removeDirectoryItem.bind(this);
        this.onSave = this.onSave.bind(this);
        this.onCancel = this.onCancel.bind(this);
        this.onResetConfiguration = this.onResetConfiguration.bind(this);
        this.loadConfig = this.loadConfig.bind(this);
    }

    componentDidMount() {
        this.loadConfig();
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
        var config = {
            port: this.state.port,
            showTestData: this.state.showTestData,
            language: this.state.language,
            templatesDirs: []
        };
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
        if (window.confirm(getTranslation('configuration.resetAllSettings.question'))) {
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
        this.setRedirect()
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

        if (this.state.loading) {
            return (
                <div>
                    <i>Loading...</i>
                </div>
            );
        }

        if (this.state.failed) {
            return (
                <ErrorAlert
                    title={'Cannot load Configuration'}
                    description={'Something went wrong during contacting the rest api.'}
                    action={{
                        handleClick: this.loadConfig,
                        title: 'Try again'
                    }}
                />
            );
        }

        return (
            <form>
                <FormLabelField label={<I18n name={'configuration.language'}/>} fieldLength={2}>
                    <FormSelect value={this.state.language} name={'language'} onChange={this.handleTextChange}>
                        <FormOption value={'default'} i18nKey={'configuration.language.option.default'} />
                        <FormOption value={'en'} i18nKey={'language.english'} />
                        <FormOption value={'de'} i18nKey={'language.german'} />
                    </FormSelect>
                </FormLabelField>
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
                    <FormFieldset text={'Expert settings'}>
                        <FormLabelInputField label={'Port'} fieldLength={2} type="number" min={0} max={65535}
                                             step={1}
                                             name={'port'} value={this.state.port}
                                             onChange={this.handleTextChange}
                                             placeholder="Enter port"/>
                        <FormLabelField>
                            <FormButton id={'resetFactorySettings'} onClick={this.onResetConfiguration}
                                        hint="Reset factory settings."> <IconDanger/> Reset
                            </FormButton>
                        </FormLabelField>
                    </FormFieldset>
                </Collapse>
                <FormGroup>
                    <FormField length={12}>
                        <FormButton onClick={this.onCancel}
                                    hintKey="configuration.cancel.hint"><I18n name={'common.cancel'}/>
                        </FormButton>
                        <FormButton onClick={this.onSave} bsStyle="primary"
                                    hintKey="configuration.save.hint"><I18n name={'common.save'}/>
                        </FormButton>
                    </FormField>
                </FormGroup>
            </form>
        );
    }
}

class ConfigurationPage
    extends React
        .Component {

    render() {
        let todo = '';
        if (isDevelopmentMode()) {
            todo = <code><h3>ToDo</h3>
                <ul>
                    <li>Do the form validation (server and/or client side) with error fields.</li>
                </ul>
            </code>
        }
        return (
            <React.Fragment>
                <PageHeader><I18n name={'configuration'}/></PageHeader>
                <ConfigForm/>
                {todo}
            </React.Fragment>
        );
    }
}

export default ConfigurationPage;

