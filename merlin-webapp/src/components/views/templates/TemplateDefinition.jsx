import React from 'react';
import {getRestServiceUrl, isDevelopmentMode} from "../../../actions/global";
import {
    Form,
    TabContent,
    TabPane,
    Nav,
    NavItem,
    NavLink,
    Table
} from 'reactstrap';
import classnames from 'classnames';
import ErrorAlert from "../../general/ErrorAlert";
import {
    FormGroup,
    FormField,
    FormLabel,
    FormCheckbox,
    FormFieldset,
    FormLabelInputField, FormLabelField, FormButton
} from "../../general/forms/FormComponents";
import {PageHeader} from "../../general/BootstrapComponents";
import EditableTextField from "../../general/forms/EditableTextField";
import {formatDateTime} from "../../../actions/global";

class TemplateDefinition extends React.Component {
    componentDidMount = () => {
        this.fetchTemplateDefinition();
    };

    handleTextChange = (value, name) => {
        let definition = this.state.definition;
        definition[name] = value;
        this.setState({definition: definition});
    }

    handleStateChange = event => {
        let definition = this.state.definition;
        definition[event.target.name] = event.target.checked;
        this.setState({definition: definition});
    }

    fetchTemplateDefinition = () => {
        this.setState({
            isFetching: true,
            failed: false,
            definition: undefined
        });
        fetch(getRestServiceUrl("templates/definition", {
            primaryKey: this.state.primaryKey
        }), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => {
                this.setState({
                    isFetching: false,
                    definition: json
                });
            })
            .catch(() => this.setState({isFetching: false, failed: true}));
    };

    render = () => {
        let content = undefined;
        if (this.state.isFetching) {
            content = <i>Loading...</i>;
        } else if (this.state.failed) {
            content = <ErrorAlert
                title={'Cannot load Template Definition'}
                description={'Something went wrong during contacting the rest api.'}
                action={{
                    handleClick: this.fetchTemplateDefinition,
                    title: 'Try again'
                }}
            />;
        } else if (this.state.definition) {
            content = <div>
                <Nav tabs>
                    <NavItem>
                        <NavLink
                            className={classnames({active: this.state.activeTab === '1'})}
                            onClick={() => {
                                this.toggleTab('1');
                            }}>
                            Main
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink
                            className={classnames({active: this.state.activeTab === '2'})}
                            onClick={() => {
                                this.toggleTab('2');
                            }}>
                            Variables
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink
                            className={classnames({active: this.state.activeTab === '3'})}
                            onClick={() => {
                                this.toggleTab('3');
                            }}>
                            Dependent variables
                        </NavLink>
                    </NavItem>
                </Nav>
                <TabContent activeTab={this.state.activeTab}>
                    <TabPane tabId="1">
                        {this.mainTab()}
                    </TabPane>
                    <TabPane tabId="2">
                        {this.variablesTab()}
                    </TabPane>
                    <TabPane tabId="3">
                        {this.dependentVariablesTab()}
                    </TabPane>
                </TabContent>

            </div>;
        }

        let todo = '';
        if (isDevelopmentMode()) {
            todo = <div><h3>ToDo</h3>
                <ul>
                    <li>Auto grow of textarea.</li>
                </ul>
            </div>
        }
        let title = this.state.definition ? 'Template definition: ' + this.state.definition.id : 'Template definition';

        return <div>
            <PageHeader>
                {title}
            </PageHeader>
            {content}
            {todo}
        </div>;
    };

    constructor(props) {
        super(props);
        this.state = {
            primaryKey: this.props.match.params.primaryKey,
            isFetching: true,
            failed: false,
            definition: undefined,
            activeTab: '1'
        };
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleStateChange = this.handleStateChange.bind(this);
        this.fetchTemplateDefinition = this.fetchTemplateDefinition.bind(this);
        this.toggleTab = this.toggleTab.bind(this);
        this.mainTab = this.mainTab.bind(this);
        this.variablesTab = this.variablesTab.bind(this);
        this.dependentVariablesTab = this.dependentVariablesTab.bind(this);
    }

    toggleTab(tab) {
        if (this.state.activeTab !== tab) {
            this.setState({
                activeTab: tab
            });
        }
    }

    mainTab = () => {
        return <div><Form>
            <FormGroup>
                <FormLabel htmlFor={'id'}>
                    Id
                </FormLabel>
                <FormField length={6}>
                    <EditableTextField
                        type={'text'}
                        value={this.state.definition.id}
                        name={'id'}
                        onChange={this.handleTextChange}
                    />
                </FormField>
            </FormGroup>
            <FormGroup>
                <FormLabel htmlFor={'description'}>
                    Description
                </FormLabel>
                <FormField length={6}>
                    <EditableTextField
                        type={'textarea'}
                        value={this.state.definition.description}
                        name={'description'}
                        onChange={this.handleTextChange}
                    />
                </FormField>
            </FormGroup>
            <FormGroup>
                <FormLabel htmlFor={'filenamePattern'}>
                    File name pattern
                </FormLabel>
                <FormField length={6}>
                    <EditableTextField
                        type={'text'}
                        value={this.state.definition.filenamePattern}
                        name={'filenamePattern'}
                        onChange={this.handleTextChange}
                    />
                </FormField>
                <FormField length={2}>
                    <FormCheckbox checked={this.state.definition.stronglyRestrictedFilenames}
                                  name="stronglyRestrictedFilenames" label={'strong file names'}
                                  onChange={this.handleStateChange}
                                  hint="Merlin will ensure filenames without unallowed chars. If checked, Merlin will only use ASCII-chars and replace e. g. ä by ae (recommended)."/>
                </FormField>
            </FormGroup>
        </Form>
            <h5>Information</h5>
            <Table hover>
                <tbody>
                <tr>
                    <td>Last modified</td>
                    <td>{formatDateTime(this.state.definition.fileDescriptor.lastModified)}</td>
                </tr>
                <tr>
                    <td>Pfad</td>
                    <td>{this.state.definition.fileDescriptor.canonicalPath}</td>
                </tr>
                </tbody>
            </Table>

        </div>;
    }

    variablesTab = () => {
        if (!this.state.definition.variableDefinitions) {
            return null;
        }
        const rows = [];
        this.state.definition.variableDefinitions.forEach((variable) => {
            rows.push(
                <div>{variable.name}</div>
            );
        });

        return <div>
            {rows}</div>
    }
    dependentVariablesTab = () => {
        if (!this.state.definition.dependentVariableDefinitions) {
            return null;
        }
        const rows = [];
        this.state.definition.dependentVariableDefinitions.forEach((variable) => {
            rows.push(
                <div>{variable.name}</div>
            );
        });

        return <div>
            {rows}</div>
    }
}

export default TemplateDefinition;